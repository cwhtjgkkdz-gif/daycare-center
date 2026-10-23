package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Notice;
import com.soft231.daycarecenterbackend.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知公告")
@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    @Autowired private NoticeService noticeService;

    @Operation(summary = "分页查询通知")
    @GetMapping("/page")
    public Result<PageResult<Notice>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        Page<Notice> page = noticeService.page(current, size, title, type);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询通知详情")
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) { return Result.ok(noticeService.getById(id)); }

    @Operation(summary = "新增通知")
    @PostMapping
    @OperationLog(module = "通知公告", operation = "新增通知")
    public Result<Void> add(@RequestBody Notice notice) { noticeService.add(notice); return Result.ok("新增成功"); }

    @Operation(summary = "修改通知")
    @PutMapping
    @OperationLog(module = "通知公告", operation = "修改通知")
    public Result<Void> update(@RequestBody Notice notice) { noticeService.update(notice); return Result.ok("修改成功"); }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    @OperationLog(module = "通知公告", operation = "删除通知")
    public Result<Void> delete(@PathVariable Long id) { noticeService.delete(id); return Result.ok("删除成功"); }
}
