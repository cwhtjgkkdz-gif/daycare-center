package com.soft231.daycarecenterbackend.controller;

import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Menu;
import com.soft231.daycarecenterbackend.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<Menu>> getTree() {
        return Result.ok(menuService.getTree());
    }

    @Operation(summary = "获取角色菜单树")
    @GetMapping("/role-tree/{roleId}")
    public Result<List<Menu>> getRoleTree(@PathVariable Long roleId) {
        return Result.ok(menuService.getRoleTree(roleId));
    }

    @Operation(summary = "查询菜单详情")
    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        return Result.ok(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @OperationLog(module = "菜单管理", operation = "新增菜单")
    @PostMapping
    public Result<Void> add(@RequestBody Menu menu) {
        menuService.add(menu);
        return Result.ok("新增成功");
    }

    @Operation(summary = "修改菜单")
    @OperationLog(module = "菜单管理", operation = "修改菜单")
    @PutMapping
    public Result<Void> update(@RequestBody Menu menu) {
        menuService.update(menu);
        return Result.ok("修改成功");
    }

    @Operation(summary = "删除菜单")
    @OperationLog(module = "菜单管理", operation = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.ok("删除成功");
    }
}
