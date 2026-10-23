package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Finance;
import com.soft231.daycarecenterbackend.service.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "财务管理")
@RestController
@RequestMapping("/api/finance")
public class FinanceController {
    @Autowired private FinanceService financeService;

    @Operation(summary = "分页查询财务记录")
    @GetMapping("/page")
    public Result<PageResult<Finance>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String recordNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String childName) {
        Page<Finance> page = financeService.page(current, size, recordNo, type, childName);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询财务详情")
    @GetMapping("/{id}")
    public Result<Finance> getById(@PathVariable Long id) { return Result.ok(financeService.getById(id)); }

    @Operation(summary = "新增财务记录")
    @PostMapping
    @OperationLog(module = "财务管理", operation = "新增记录")
    public Result<Void> add(@RequestBody Finance finance) { financeService.add(finance); return Result.ok("新增成功"); }

    @Operation(summary = "修改财务记录")
    @PutMapping
    @OperationLog(module = "财务管理", operation = "修改记录")
    public Result<Void> update(@RequestBody Finance finance) { financeService.update(finance); return Result.ok("修改成功"); }

    @Operation(summary = "删除财务记录")
    @DeleteMapping("/{id}")
    @OperationLog(module = "财务管理", operation = "删除记录")
    public Result<Void> delete(@PathVariable Long id) { financeService.delete(id); return Result.ok("删除成功"); }
}
