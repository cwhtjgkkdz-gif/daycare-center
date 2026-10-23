package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Activity;
import com.soft231.daycarecenterbackend.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "园区活动")
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired private ActivityService activityService;

    @Operation(summary = "分页查询活动")
    @GetMapping("/page")
    public Result<PageResult<Activity>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        Page<Activity> page = activityService.page(current, size, title, type);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询活动详情")
    @GetMapping("/{id}")
    public Result<Activity> getById(@PathVariable Long id) { return Result.ok(activityService.getById(id)); }

    @Operation(summary = "新增活动")
    @PostMapping
    @OperationLog(module = "园区活动", operation = "新增活动")
    public Result<Void> add(@RequestBody Activity activity) { activityService.add(activity); return Result.ok("新增成功"); }

    @Operation(summary = "修改活动")
    @PutMapping
    @OperationLog(module = "园区活动", operation = "修改活动")
    public Result<Void> update(@RequestBody Activity activity) { activityService.update(activity); return Result.ok("修改成功"); }

    @Operation(summary = "删除活动")
    @DeleteMapping("/{id}")
    @OperationLog(module = "园区活动", operation = "删除活动")
    public Result<Void> delete(@PathVariable Long id) { activityService.delete(id); return Result.ok("删除成功"); }
}
