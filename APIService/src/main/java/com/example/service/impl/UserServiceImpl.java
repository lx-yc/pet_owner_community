package com.example.service.impl;


import com.example.dto.notification.NotificationMessageDTO;
import com.example.dto.page.PageDTO;
import com.example.dto.user.UserLoginDTO;
import com.example.dto.user.UserUpdateDTO;
import com.example.entity.Favorite;
import com.example.entity.Follow;
import com.example.entity.Post;
import com.example.entity.User;
import com.example.enums.NotificationType;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.mapper.FollowMapper;
import com.example.mapper.UserMapper;
import com.example.rabbitMQ.NoticeProducer;
import com.example.service.UserService;
import com.example.utils.*;
import com.example.vo.page.PageVO;
import com.example.vo.user.UserAdminVO;
import com.example.vo.user.UserLoginVO;
import com.example.vo.user.UserVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private FollowMapper followMapper;

    @Resource
    private NoticeProducer noticeProducer;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    //token常量redis
    private static final String LOGIN_TOKEN_KEY = "login:token:";

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        User user = userMapper.selectByUsername(userLoginDTO.getUsername());

        GlobalCheckUtil.checkNotNull(user, ResultCode.USER_NOT_EXIST);
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("nickname", user.getNickname());
        String token = JwtUtil.generateToken(claims);

        // 存入 Redis：key = login:token:xxx  value = userId
        redisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + token, user.getId().toString(), 7, TimeUnit.DAYS);

        UserLoginVO userLoginVO = BeanConvertUtils.convert(user, UserLoginVO.class);
        userLoginVO.setToken(token);

        return userLoginVO;
    }

    @Override
    public void register(UserLoginDTO userLoginDTO) {
        User user = userMapper.selectByUsername(userLoginDTO.getUsername());
        if (user != null) {
            throw new BusinessException(ResultCode.USER_EXIST);
        }
        user = BeanConvertUtils.convert(userLoginDTO, User.class);
        user.setNickname(NicknameGenerator.generate());
        user.setPassword(passwordEncoder.encode(userLoginDTO.getPassword()));
        int rows = userMapper.register(user);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.REGISTER_FAILED);

    }

    @Override
    public PageVO<UserAdminVO> getUserList(PageDTO pageDTO) {
        Integer pageNum = pageDTO.getPageNum();
        Integer pageSize = pageDTO.getPageSize();
        String keyword = pageDTO.getKeyword();

        Long adminId = UserContext.getUserId();
        UserLoginVO admin = userMapper.selectById(adminId);
        GlobalCheckUtil.checkNotNull(admin, ResultCode.USER_NOT_EXIST);

        if (admin.getRole().equals("admin")) {
            // 3. 传统分页计算起始位置
            Long offset = (long) (pageNum - 1) * pageSize;

            // 4. 查询当前页数据
            List<User> userList = userMapper.getUserList(offset, pageSize, keyword);

            // 5. 查询总条数
            Long total = userMapper.getUserCount(keyword);

            // 6. 计算总页数
            int pages = (int) Math.ceil((double) total / pageSize);

            // 7. 转VO
            List<UserAdminVO> voList = BeanConvertUtils.convertList(userList, UserAdminVO.class);

            // 8. 封装返回
            PageVO<UserAdminVO> result = new PageVO<>();
            result.setList(voList);
            result.setTotal(total);
            result.setPages(pages);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setHasMore(pageNum < pages);
            return result;
        } else {
            throw new BusinessException(ResultCode.USER_NO_PERMISSION);
        }
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        Long userId = GlobalCheckUtil.checkLogin();
        User user = BeanConvertUtils.convert(userUpdateDTO, User.class);
        user.setId(userId);
        int rows = userMapper.updateById(user);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.USER_UPDATE_FAILED);

    }

    @Override
    public boolean addOrCancelFollow(Long followUserId) {
        Long userId = GlobalCheckUtil.checkLogin();
        if (Objects.equals(followUserId, userId)) {
            throw new BusinessException(ResultCode.USER_FOLLOW_SELF);
        }
        UserLoginVO user = userMapper.selectById(followUserId);
        GlobalCheckUtil.checkNotNull(user, ResultCode.USER_NOT_EXIST);

        Follow follow = followMapper.selectByUserIdAndFollowId(userId, followUserId);
        if (follow != null) {
            //取消关注
            int deleteRows = followMapper.deleteById(follow.getId());
            GlobalCheckUtil.checkRowAffect(deleteRows, ResultCode.USER_FOLLOW_CANCEL_FAILED);

            //关注数-1
            int updateRows = userMapper.decreaseFollowCount(userId);
            GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.USER_FOLLOW_CANCEL_FAILED);

            //粉丝数-1
            int updateUserRows = userMapper.decreaseFansCount(followUserId);
            GlobalCheckUtil.checkRowAffect(updateUserRows, ResultCode.USER_FOLLOW_CANCEL_FAILED);
            return false;
        } else {
            //关注
            Follow newfollow = new Follow();
            newfollow.setUserId(userId);
            newfollow.setFollowUserId(followUserId);
            int insertRows = followMapper.insert(newfollow);
            GlobalCheckUtil.checkRowAffect(insertRows, ResultCode.USER_FOLLOW_ADD_FAILED);
            //关注数+1
            int updateRows = userMapper.increaseFollowCount(userId);
            GlobalCheckUtil.checkRowAffect(updateRows, ResultCode.USER_FOLLOW_ADD_FAILED);

            //粉丝数+1
            int updateUserRows = userMapper.increaseFansCount(followUserId);
            GlobalCheckUtil.checkRowAffect(updateUserRows, ResultCode.USER_FOLLOW_ADD_FAILED);

            // ============== 发送通知消息 ==============
            NotificationMessageDTO dto = new NotificationMessageDTO();
            dto.setUserId(newfollow.getFollowUserId());
            dto.setFromUserId(userId);
            dto.setType(NotificationType.FOLLOW.getCode());
            dto.setContent(NotificationType.buildContent(dto.getType(), userId));
            noticeProducer.send(dto);


            return true;
        }

    }

    @Override
    public UserVO getUserById(Long userId) {
        UserVO userVO = userMapper.selectUserById(userId);
        GlobalCheckUtil.checkNotNull(userVO, ResultCode.USER_NOT_EXIST);
        if(userVO.getRole().equals("admin")){
            throw new BusinessException(ResultCode.USER_NO_PERMISSION);
        }
        
        Long currentUserId = UserContext.getUserId();
        System.out.println("cur"+currentUserId);
        System.out.println("followuserId"+userId);
        if (currentUserId != null) {
            Follow follow = followMapper.selectByUserIdAndFollowId(currentUserId, userId);
            userVO.setIsFollowed(follow != null);
        } else {
            userVO.setIsFollowed(false);
        }
        
        return userVO;
    }

    @Override
    public Map<String, Object> getFollowingList(Long userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserVO> list = followMapper.selectFollowingList(userId, offset, pageSize);
        int total = followMapper.countFollowing(userId);
        int pages = (int) Math.ceil((double) total / pageSize);
        
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null) {
            for (UserVO user : list) {
                Follow follow = followMapper.selectByUserIdAndFollowId(currentUserId, user.getId());
                user.setIsFollowed(follow != null);
                
                Follow mutualFollow = followMapper.selectByUserIdAndFollowId(user.getId(), currentUserId);
                user.setIsMutualFollow(follow != null && mutualFollow != null);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pages", pages);
        result.put("pageNum", page);
        result.put("pageSize", pageSize);
        result.put("hasMore", page < pages);
        return result;
    }

    @Override
    public Map<String, Object> getFollowerList(Long userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserVO> list = followMapper.selectFollowerList(userId, offset, pageSize);
        int total = followMapper.countFollowers(userId);
        int pages = (int) Math.ceil((double) total / pageSize);
        
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null) {
            for (UserVO user : list) {
                Follow follow = followMapper.selectByUserIdAndFollowId(currentUserId, user.getId());
                user.setIsFollowed(follow != null);
                
                Follow mutualFollow = followMapper.selectByUserIdAndFollowId(user.getId(), currentUserId);
                user.setIsMutualFollow(follow != null && mutualFollow != null);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pages", pages);
        result.put("pageNum", page);
        result.put("pageSize", pageSize);
        result.put("hasMore", page < pages);
        return result;
    }

}
