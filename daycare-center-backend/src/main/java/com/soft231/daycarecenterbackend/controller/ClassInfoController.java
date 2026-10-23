package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.ClassInfo;
import com.soft231.daycarecenterbackend.service.ClassInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班级管理")
@RestController
@RequestMapping("/api/class")
public class ClassInfoController {
    @Autowired private ClassInfoService classInfoService;

    @Operation(summary = "分页查询班级")
    @GetMapping("/page")
    public Result<PageResult<ClassInfo>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String classType) {
        Page<ClassInfo> page = classInfoService.page(current, size, className, classType);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询全部班级")
    @GetMapping("/list")
    public Result<List<ClassInfo>> list() { return Result.ok(classInfoService.list()); }

    @Operation(summary = "查询班级详情")
    @GetMapping("/{id}")
    public Result<ClassInfo> getById(@PathVariable Long id) { return Result.ok(classInfoService.getById(id)); }

    @Operation(summary = "新增班级")
    @OperationLog(module = "班级管理", operation = "新增班级")
    @PostMapping
    public Result<Void> add(@RequestBody ClassInfo classInfo) { classInfoService.add(classInfo); return Result.ok("新增成功"); }

    @Operation(summary = "修改班级")
    @OperationLog(module = "班级管理", operation = "修改班级")
    @PutMapping
    public Result<Void> update(@RequestBody ClassInfo classInfo) { classInfoService.update(classInfo); return Result.ok("修改成功"); }

    @Operation(summary = "删除班级")
    @OperationLog(module = "班级管理", operation = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { classInfoService.delete(id); return Result.ok("删除成功"); }
}
