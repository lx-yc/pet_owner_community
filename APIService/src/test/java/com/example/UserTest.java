package com.example;


import com.example.dto.user.UserLoginDTO;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.vo.user.UserLoginVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
//    @Resource
//    private UserMapper userMapper;
//
//    @Resource
//    private UserService userService;
//
//    @Test
//    public void test() {
//        User user = userMapper.selectByUsername("admin");
//        System.out.println(user);
//    }
//
//    @Test
//    public void testLogin() {
//        UserLoginDTO userLoginDTO = new UserLoginDTO();
//        userLoginDTO.setUsername("admin");
//        userLoginDTO.setPassword("123456");
//        UserLoginVO user = userService.login(userLoginDTO);
//        System.out.println("ok"+user);
//    }
//
//    @Test
//    public void testRegister() {
//        UserLoginDTO userLoginDTO = new UserLoginDTO();
//        userLoginDTO.setUsername("lx");
//        userLoginDTO.setPassword("123456");
//        userService.register(userLoginDTO);
//        System.out.println("ok");
//        System.out.println(NicknameGenerator.generate());
//    }

//    @Test
//    public void testGetUserList() {
//        CursorPageDTO cursorPageDTO = new CursorPageDTO();
//
//        cursorPageDTO.setCursor(0L);
//        cursorPageDTO.setSize(10);
//        cursorPageDTO.setKeyword("");
//        CursorPageVO<UserAdminVO> userList = userService.getUserList(cursorPageDTO);
//        System.out.println(userList);
//    }
}
