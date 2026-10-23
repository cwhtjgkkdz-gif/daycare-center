package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.User;
import com.soft231.daycarecenterbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        Page<User> page = userService.page(current, size, username, realName, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询用户详情")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @OperationLog(module = "用户管理", operation = "新增用户")
    @PostMapping
    public Result<Void> add(@RequestBody User user) {
        userService.add(user);
        return Result.ok("新增成功");
    }

    @Operation(summary = "修改用户")
    @OperationLog(module = "用户管理", operation = "修改用户")
    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.update(user);
        return Result.ok("修改成功");
    }

    @Operation(summary = "删除用户")
    @OperationLog(module = "用户管理", operation = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "修改用户状态")
    @OperationLog(module = "用户管理", operation = "修改状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.ok("操作成功");
    }

    @Operation(summary = "重置密码")
    @OperationLog(module = "用户管理", operation = "重置密码")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.ok("密码已重置为123456");
    }
}
