package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Teacher;
import com.soft231.daycarecenterbackend.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "教职工管理")
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired private TeacherService teacherService;

    @Operation(summary = "分页查询教职工")
    @GetMapping("/page")
    public Result<PageResult<Teacher>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer status) {
        Page<Teacher> page = teacherService.page(current, size, name, position, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询全部教职工")
    @GetMapping("/list")
    public Result<List<Teacher>> list() { return Result.ok(teacherService.list()); }

    @Operation(summary = "查询教职工详情")
    @GetMapping("/{id}")
    public Result<Teacher> getById(@PathVariable Long id) { return Result.ok(teacherService.getById(id)); }

    @Operation(summary = "新增教职工")
    @PostMapping
    @OperationLog(module = "教职工管理", operation = "新增教职工")
    public Result<Void> add(@RequestBody Teacher teacher) { teacherService.add(teacher); return Result.ok("新增成功"); }

    @Operation(summary = "修改教职工")
    @PutMapping
    @OperationLog(module = "教职工管理", operation = "修改教职工")
    public Result<Void> update(@RequestBody Teacher teacher) { teacherService.update(teacher); return Result.ok("修改成功"); }

    @Operation(summary = "删除教职工")
    @DeleteMapping("/{id}")
    @OperationLog(module = "教职工管理", operation = "删除教职工")
    public Result<Void> delete(@PathVariable Long id) { teacherService.delete(id); return Result.ok("删除成功"); }
}
