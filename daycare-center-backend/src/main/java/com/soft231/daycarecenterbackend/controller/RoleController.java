package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Role;
import com.soft231.daycarecenterbackend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<Role>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String roleName) {
        Page<Role> page = roleService.page(current, size, roleName);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询全部角色")
    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.ok(roleService.list());
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    @Operation(summary = "新增角色")
    @OperationLog(module = "角色管理", operation = "新增角色")
    @PostMapping
    public Result<Void> add(@RequestBody Role role) {
        roleService.add(role);
        return Result.ok("新增成功");
    }

    @Operation(summary = "修改角色")
    @OperationLog(module = "角色管理", operation = "修改角色")
    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        roleService.update(role);
        return Result.ok("修改成功");
    }

    @Operation(summary = "删除角色")
    @OperationLog(module = "角色管理", operation = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "为角色分配菜单权限")
    @OperationLog(module = "角色管理", operation = "分配权限")
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        roleService.assignMenus(id, body.get("menuIds"));
        return Result.ok("分配成功");
    }

    @Operation(summary = "获取角色的菜单ID列表")
    @GetMapping("/{id}/menuIds")
    public Result<List<Long>> getMenuIds(@PathVariable Long id) {
        return Result.ok(roleService.getMenuIds(id));
    }
}
