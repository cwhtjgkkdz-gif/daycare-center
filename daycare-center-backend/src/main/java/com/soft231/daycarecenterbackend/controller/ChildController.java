package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Child;
import com.soft231.daycarecenterbackend.service.ChildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "儿童档案")
@RestController
@RequestMapping("/api/child")
public class ChildController {
    @Autowired private ChildService childService;

    @Operation(summary = "分页查询儿童")
    @GetMapping("/page")
    public Result<PageResult<Child>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer status) {
        Page<Child> page = childService.page(current, size, name, classId, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询儿童详情")
    @GetMapping("/{id}")
    public Result<Child> getById(@PathVariable Long id) { return Result.ok(childService.getById(id)); }

    @Operation(summary = "新增儿童")
    @PostMapping
    @OperationLog(module = "儿童档案", operation = "新增儿童")
    public Result<Void> add(@RequestBody Child child) { childService.add(child); return Result.ok("新增成功"); }

    @Operation(summary = "修改儿童")
    @PutMapping
    @OperationLog(module = "儿童档案", operation = "修改儿童")
    public Result<Void> update(@RequestBody Child child) { childService.update(child); return Result.ok("修改成功"); }

    @Operation(summary = "删除儿童")
    @DeleteMapping("/{id}")
    @OperationLog(module = "儿童档案", operation = "删除儿童")
    public Result<Void> delete(@PathVariable Long id) { childService.delete(id); return Result.ok("删除成功"); }

    @Operation(summary = "修改儿童状态")
    @PutMapping("/{id}/status")
    @OperationLog(module = "儿童档案", operation = "修改状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        childService.updateStatus(id, status); return Result.ok("操作成功"); }
}
