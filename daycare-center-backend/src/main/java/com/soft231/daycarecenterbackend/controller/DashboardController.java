package com.soft231.daycarecenterbackend.controller;

import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "数据统计")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "首页概览数据")
    public Result<Map<String, Object>> summary() {
        return Result.ok(dashboardService.summary());
    }

    @GetMapping("/children-per-class")
    @Operation(summary = "各班儿童人数")
    public Result<List<Map<String, Object>>> childrenPerClass() {
        return Result.ok(dashboardService.childrenPerClass());
    }

    @GetMapping("/teacher-by-position")
    @Operation(summary = "教职工岗位分布")
    public Result<List<Map<String, Object>>> teacherByPosition() {
        return Result.ok(dashboardService.teacherByPosition());
    }

    @GetMapping("/monthly-fees")
    @Operation(summary = "近6个月收费趋势")
    public Result<List<Map<String, Object>>> monthlyFees() {
        return Result.ok(dashboardService.monthlyFees());
    }

    @GetMapping("/gender-ratio")
    @Operation(summary = "儿童男女比例")
    public Result<List<Map<String, Object>>> genderRatio() {
        return Result.ok(dashboardService.genderRatio());
    }
}
