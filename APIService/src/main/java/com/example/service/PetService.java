package com.example.service;

import com.example.dto.pet.PetCursorQuery;
import com.example.dto.pet.PetDTO;
import com.example.entity.Pet;
import com.example.vo.page.CursorPageVO;
import com.example.vo.pet.PetVO;

public interface PetService {
    //新增宠物
    void insertPet(PetDTO petDTO);
    //查询用户宠物列表
    CursorPageVO<PetVO> getPetList(PetCursorQuery query);
    //更新宠物信息
    void updatePet(PetDTO petDTO);
    //删除宠物信息
    void deletePet(Long petId);
}
