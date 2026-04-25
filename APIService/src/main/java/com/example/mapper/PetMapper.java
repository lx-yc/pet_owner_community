package com.example.mapper;

import com.example.dto.pet.PetCursorQuery;
import com.example.entity.Pet;
import com.example.vo.pet.PetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {
    //新增宠物
    int insertPet(@Param("pet") Pet pet);

    //查询用户宠物列表
    List<PetVO> getUserPetList(@Param("query") PetCursorQuery query);

    //更新宠物信息
    int updatePet(@Param("pet") Pet pet);

    //根据id查询宠物信息
    Pet selectById(@Param("id") Long id);

    //删除宠物信息
    int deleteById(@Param("id") Long id);
}
