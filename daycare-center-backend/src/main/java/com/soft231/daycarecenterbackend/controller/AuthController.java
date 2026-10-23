package com.soft231.daycarecenterbackend.controller;

import com.soft231.daycarecenterbackend.common.Result;
import com.soft231.daycarecenterbackend.common.ResultCode;
import com.soft231.daycarecenterbackend.dto.LoginDTO;
import com.soft231.daycarecenterbackend.entity.Menu;
import com.soft231.daycarecenterbackend.entity.User;
import com.soft231.daycarecenterbackend.service.MenuService;
import com.soft231.daycarecenterbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            return Result.error(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error(ResultCode.BAD_REQUEST, "账号已被禁用");
        }
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            return Result.error(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }

        // 存入Session
        session.setAttribute("loginUser", user);

        // 返回用户信息和菜单
        Map<String, Object> data = new HashMap<>();
        user.setPassword(null);
        data.put("userInfo", user);

        List<Menu> menus = menuService.getUserMenus(user.getRoleId());
        data.put("menus", menus);

        return Result.ok("登录成功", data);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return Result.error(ResultCode.UNAUTHORIZED, "未登录");
        }
        // 重新查库获取最新信息
        user = userService.getById(user.getId());
        session.setAttribute("loginUser", user);

        Map<String, Object> data = new HashMap<>();
        user.setPassword(null);
        data.put("userInfo", user);

        List<Menu> menus = menuService.getUserMenus(user.getRoleId());
        data.put("menus", menus);

        return Result.ok(data);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.ok("退出成功");
    }
}
