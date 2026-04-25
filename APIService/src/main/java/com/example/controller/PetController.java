package com.example.controller;

import com.example.dto.pet.PetCursorQuery;
import com.example.dto.pet.PetDTO;
import com.example.dto.post.PostCursorQuery;
import com.example.service.PetService;
import com.example.utils.Result;
import com.example.vo.page.CursorPageVO;
import com.example.vo.pet.PetVO;
import com.example.vo.post.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宠物管理", description = "宠物信息的增删改查接口")
@RestController
@RequestMapping("/pet")
public class PetController {
    @Resource
    private PetService petService;

    @Operation(summary = "添加宠物", description = "为当前用户添加宠物信息")
    @PostMapping("/insert")
    public Result<Void> insert(@RequestBody PetDTO petDTO) {
        petService.insertPet(petDTO);
        return Result.success("新增宠物成功");
    }

    @Operation(summary = "获取宠物列表", description = "分页获取当前用户的宠物列表")
    @GetMapping("/list")
    public Result<CursorPageVO<PetVO>> list(PetCursorQuery query) {
        return Result.success(petService.getPetList(query),"获取宠物列表成功");
    }

    @Operation(summary = "更新宠物信息", description = "更新已有宠物的信息")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody PetDTO petDTO) {
        petService.updatePet(petDTO);
        return Result.success("更新宠物信息成功");
    }

    @Operation(summary = "删除宠物", description = "根据ID删除宠物信息")
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        petService.deletePet(id);
        return Result.success("删除宠物成功");
    }
}
