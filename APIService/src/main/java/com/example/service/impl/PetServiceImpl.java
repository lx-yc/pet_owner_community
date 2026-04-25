package com.example.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.example.dto.pet.PetCursorQuery;
import com.example.dto.pet.PetDTO;
import com.example.entity.Pet;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.mapper.PetMapper;
import com.example.service.PetService;
import com.example.utils.BeanConvertUtils;
import com.example.utils.GlobalCheckUtil;
import com.example.utils.UserContext;
import com.example.vo.page.CursorPageVO;
import com.example.vo.pet.PetVO;
import com.example.vo.post.PostVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PetServiceImpl implements PetService {
    @Resource
    private PetMapper petMapper;

    @Override
    public void insertPet(PetDTO petDTO) {
        Long userId = GlobalCheckUtil.checkLogin();

        petDTO.setUserId(userId);
        int rows = petMapper.insertPet(BeanConvertUtils.convert(petDTO, Pet.class));
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.PET_INSERT_FAILED);
    }

    @Override
    public CursorPageVO<PetVO> getPetList(PetCursorQuery query) {
        Long userId = GlobalCheckUtil.checkLogin();

        query.setUserId(userId);
        // 多查1条判断是否还有下一页
        int pageSize = query.getPageSize();
        query.setPageSize(pageSize + 1);

        List<PetVO> list = petMapper.getUserPetList(query);
        boolean isLast = list.size() <= pageSize;

        CursorPageVO<PetVO> result = new CursorPageVO<>();
        result.setIsLast(isLast);

        if (!list.isEmpty()) {
            // 移除多余的一条
            if (!isLast) {
                list.remove(list.size() - 1);
            }
            PetVO last = list.get(list.size() - 1);
            result.setNextCursorId(last.getId());
            result.setNextCursorTime(LocalDateTimeUtil.format(last.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        result.setList(list);
        return result;
    }

    @Override
    public void updatePet(PetDTO petDTO) {
        Long userId = GlobalCheckUtil.checkLogin();
        Pet pet = petMapper.selectById(petDTO.getId());
        GlobalCheckUtil.checkNotNull(pet, ResultCode.PET_NOT_EXIST);
        GlobalCheckUtil.checkOwner(pet.getUserId(), userId, ResultCode.PET_NO_PERMISSION);

        petDTO.setUserId(userId);
        int rows = petMapper.updatePet(BeanConvertUtils.convert(petDTO, Pet.class));
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.PET_UPDATE_FAILED);
    }

    @Override
    public void deletePet(Long petId) {
        Long userId = GlobalCheckUtil.checkLogin();
        Pet pet = petMapper.selectById(petId);
        GlobalCheckUtil.checkNotNull(pet, ResultCode.PET_NOT_EXIST);
        GlobalCheckUtil.checkOwner(pet.getUserId(), userId, ResultCode.PET_NO_PERMISSION);

        int rows = petMapper.deleteById(petId);
        GlobalCheckUtil.checkRowAffect(rows, ResultCode.PET_DELETE_FAILED);

    }
}
