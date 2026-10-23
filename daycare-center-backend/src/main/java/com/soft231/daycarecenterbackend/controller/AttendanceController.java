package com.soft231.daycarecenterbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.aspect.OperationLog;
import com.soft231.daycarecenterbackend.common.PageResult;
import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.entity.Attendance;
import com.soft231.daycarecenterbackend.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "考勤管理")
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired private AttendanceService attendanceService;

    @Operation(summary = "分页查询考勤记录")
    @GetMapping("/page")
    public Result<PageResult<Attendance>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) LocalDate date) {
        Page<Attendance> page = attendanceService.page(current, size, classId, date);
        return Result.ok(PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords()));
    }

    @Operation(summary = "查询考勤详情")
    @GetMapping("/{id}")
    public Result<Attendance> getById(@PathVariable Long id) { return Result.ok(attendanceService.getById(id)); }

    @Operation(summary = "儿童签到")
    @PostMapping("/check-in")
    @OperationLog(module = "考勤管理", operation = "签到")
    public Result<Void> checkIn(@RequestParam Long childId, @RequestParam Long classId) {
        attendanceService.checkIn(childId, classId); return Result.ok("签到成功"); }

    @Operation(summary = "儿童签退")
    @PutMapping("/check-out/{id}")
    @OperationLog(module = "考勤管理", operation = "签退")
    public Result<Void> checkOut(@PathVariable Long id) {
        attendanceService.checkOut(id); return Result.ok("签退成功"); }

    @Operation(summary = "修改考勤记录")
    @PutMapping
    @OperationLog(module = "考勤管理", operation = "修改记录")
    public Result<Void> update(@RequestBody Attendance attendance) {
        attendanceService.update(attendance); return Result.ok("修改成功"); }

    @Operation(summary = "删除考勤记录")
    @DeleteMapping("/{id}")
    @OperationLog(module = "考勤管理", operation = "删除记录")
    public Result<Void> delete(@PathVariable Long id) { attendanceService.delete(id); return Result.ok("删除成功"); }

    @Operation(summary = "考勤统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(
            @RequestParam(required = false) Long classId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return Result.ok(attendanceService.statistics(classId, startDate, endDate));
    }
}
