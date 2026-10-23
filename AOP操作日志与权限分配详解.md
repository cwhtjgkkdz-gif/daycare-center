# AOP操作日志 & RBAC权限分配 — 完整逻辑详解

---

## 第一部分：AOP操作日志

### 1. 概述

**目标**：每次用户在系统中做增/删/改操作时，自动记录"谁、什么时候、在哪个模块、做了什么操作、耗时多久、IP是多少"，存入 `sys_operation_log` 表。

**核心思想**：通过 Spring AOP 的 `@Around` 环绕通知，在 Controller 方法上加一个 `@OperationLog` 注解，切面自动拦截并记录日志。**业务代码和日志记录完全分离，零耦合。**

### 2. 涉及的文件

| 文件 | 职责 |
|------|------|
| `aspect/OperationLog.java` | 自定义注解定义 |
| `aspect/OperationLogAspect.java` | AOP切面实现（核心） |
| `entity/OperationLog.java` | 操作日志实体，映射 `sys_operation_log` 表 |
| `mapper/OperationLogMapper.java` | 数据访问，继承 `BaseMapper<OperationLog>` |
| 所有Controller | 在增删改方法上标注 `@OperationLog` |

### 3. 第一步：注解定义

```java
// aspect/OperationLog.java
@Target(ElementType.METHOD)   // 只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时可通过反射读取
@Documented
public @interface OperationLog {
    String module() default "";     // 模块名：如"儿童档案"
    String operation() default "";  // 操作类型：如"新增儿童"
}
```

没有任何实现逻辑，纯粹是一个标记。相当于给方法贴了一个标签。

### 4. 第二步：切面实现（核心）

```java
// aspect/OperationLogAspect.java
@Slf4j
@Aspect             // ← 告诉Spring这是一个切面
@Component          // ← 让Spring管理它的生命周期
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;  // 用于写入数据库

    /**
     * @Around("@annotation(opLog)") 的含义：
     * 拦截所有标注了 @OperationLog 注解的方法，
     * 在方法执行前后执行这段代码。
     *
     * 参数 opLog 由Spring自动传入——就是那个被标注的注解实例，
     * 可以直接读取它的 module() 和 operation() 值。
     */
    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog opLog) throws Throwable {

        // === 计时开始 ===
        long start = System.currentTimeMillis();

        // === 执行目标方法 ===
        // joinPoint.proceed() 就是调用原始的Controller方法
        // 比如 ChildController.add() 或 UserController.update()
        Object result = joinPoint.proceed();

        // === 计时结束 ===
        long costTime = System.currentTimeMillis() - start;

        // === 记录日志 ===
        // try-catch 包裹：即使日志写入失败，也不影响业务返回
        try {
            OperationLog logEntity = new OperationLog();

            // 从注解取值
            logEntity.setModule(opLog.module());      // "儿童档案"
            logEntity.setOperation(opLog.operation()); // "新增儿童"

            // 从切点获取方法签名
            // getSignature().toShortString() → "ChildController.add()"
            logEntity.setMethod(joinPoint.getSignature().toShortString());

            logEntity.setCostTime(costTime);          // 耗时毫秒数

            // === 从当前请求中获取操作人和IP ===
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                // 从Session中取当前登录用户
                HttpSession session = request.getSession(false);
                if (session != null) {
                    User user = (User) session.getAttribute("loginUser");
                    if (user != null) {
                        logEntity.setOperator(user.getUsername()); // "admin"
                    }
                }

                // 取客户端IP
                logEntity.setIp(request.getRemoteAddr());  // "127.0.0.1"
            }

            // === 写入数据库 ===
            operationLogMapper.insert(logEntity);

        } catch (Exception e) {
            log.error("记录操作日志失败", e);  // 只打日志，不影响业务
        }

        // === 返回原始结果 ===
        return result;
    }
}
```

### 5. 第三步：在 Controller 中使用

```java
// controller/ChildController.java
// 只需要加一行注解，AOP自动完成剩余工作

@OperationLog(module = "儿童档案", operation = "新增儿童")
@PostMapping
public Result<Void> add(@RequestBody Child child) {
    childService.add(child);
    return Result.ok("新增成功");
}

@OperationLog(module = "儿童档案", operation = "修改儿童")
@PutMapping
public Result<Void> update(@RequestBody Child child) {
    childService.update(child);
    return Result.ok("修改成功");
}

@OperationLog(module = "儿童档案", operation = "删除儿童")
@DeleteMapping("/{id}")
public Result<Void> delete(@PathVariable Long id) {
    childService.delete(id);
    return Result.ok("删除成功");
}
```

所有模块的增删改方法都标注了 `@OperationLog`，共计约 30 个方法。

### 6. 第四步：数据存储

日志写入 `sys_operation_log` 表：

```
sys_operation_log
┌──────────────┬──────────────────────────────────┐
│ module       │ "儿童档案"                        │
│ operation    │ "新增儿童"                        │
│ method       │ "ChildController.add()"           │
│ operator     │ "admin"                           │
│ ip           │ "127.0.0.1"                       │
│ cost_time    │ 45 (毫秒)                         │
│ create_time  │ 2026-06-30 10:23:15               │
└──────────────┴──────────────────────────────────┘
```

### 7. 完整调用时序

```
用户点击「保存」
  │
  ▼
前端: POST /api/child  Body: {name:"陈一诺",...}
  │
  ▼
LoginInterceptor.preHandle() → Session有效 → 放行
  │
  ▼
┌─────────────────────────────────────────────┐
│ Spring AOP 拦截到 @OperationLog 注解         │
│                                              │
│ OperationLogAspect.around() 开始执行          │
│   │                                          │
│   ├─ ① start = System.currentTimeMillis()    │
│   │                                          │
│   ├─ ② result = joinPoint.proceed()          │
│   │     │                                    │
│   │     └→ ChildController.add()             │
│   │         └→ ChildServiceImpl.add()         │
│   │             └→ childMapper.insert(child)  │
│   │                 └→ SQL: INSERT INTO child │
│   │                                          │
│   ├─ ③ costTime = now() - start  = 45ms     │
│   │                                          │
│   ├─ ④ 构建 OperationLog 对象                 │
│   │     module="儿童档案" (来自注解)           │
│   │     operation="新增儿童" (来自注解)        │
│   │     method="ChildController.add()"        │
│   │     operator="admin" (来自Session)        │
│   │     ip="127.0.0.1" (来自Request)          │
│   │     costTime=45                           │
│   │                                          │
│   ├─ ⑤ operationLogMapper.insert(logEntity)  │
│   │     └→ SQL: INSERT INTO sys_operation_log│
│   │                                          │
│   └─ ⑥ return result (返回给前端)            │
│                                              │
│   如果⑤失败 → catch → log.error → 不影响⑥     │
└─────────────────────────────────────────────┘
  │
  ▼
前端: 收到 Result<Void>("新增成功") → ElMessage.success → 刷新列表
```

### 8. 关键设计点

| 设计点 | 说明 |
|--------|------|
| **注解标记而非代码调用** | Controller只需加一行注解，无需手动写日志代码 |
| **环绕通知而非前置/后置** | @Around可以精确计时，拿到方法执行前后的时间差 |
| **异常隔离** | 日志失败不影响业务，try-catch只记error日志不抛异常 |
| **Session中取操作人** | 不依赖参数传入，自动从登录Session中提取当前用户 |
| **只记录Controller层** | 注解用在Controller方法上，不污染Service层代码 |

---

## 第二部分：RBAC权限分配

### 1. 概述

**RBAC**（Role-Based Access Control，基于角色的访问控制）：

```
User（用户）→ Role（角色）→ Menu（菜单）
  admin        超管           全部12个菜单
  张老师       班主任          6个菜单
  李财务       财务人员        3个菜单
```

用户不直接拥有权限。用户属于某个角色，角色拥有某些菜单，用户通过角色间接获得菜单权限。

### 2. 涉及的文件

| 文件 | 职责 |
|------|------|
| `entity/User.java` | 用户实体，含 `roleId` 外键 |
| `entity/Role.java` | 角色实体 |
| `entity/Menu.java` | 菜单实体（树形结构，含 `children` 和 `checked` 非数据库字段） |
| `entity/RoleMenu.java` | 角色-菜单关联实体（多对多中间表） |
| `mapper/RoleMenuMapper.java` | 关联表Mapper，含自定义SQL：`selectMenuIdsByRoleId` |
| `service/impl/RoleServiceImpl.java` | 角色服务，含 `assignMenus` 权限分配逻辑 |
| `service/impl/MenuServiceImpl.java` | 菜单服务，含 `getUserMenus` 和 `getRoleTree` |
| `config/DataInitializer.java` | 启动时初始化角色+菜单+关联数据 |
| `controller/AuthController.java` | 登录时调用 `getUserMenus` 返回动态菜单 |
| `views/system/role/index.vue` | 前端角色管理页面，含权限分配对话框 |
| `components/layout/Sidebar.vue` | 前端侧边栏，根据 `store.menus` 动态渲染 |

### 3. 数据模型

```
sys_user                    sys_role
┌──────────────┐           ┌──────────────┐
│ id           │           │ id (PK)      │
│ username     │  role_id  │ role_name    │
│ password     │──────────→│ role_code    │
│ real_name    │   FK      │ description  │
│ role_id ─────┘           └──────┬───────┘
│ status       │                  │
└──────────────┘                  │
                                  │ role_id (FK)
                    sys_role_menu │
                    ┌─────────────┴──────┐
                    │ id (PK)            │
                    │ role_id (FK) ──────┤→ sys_role.id
                    │ menu_id (FK) ──────┤→ sys_menu.id
                    └────────────────────┘
                                  │ menu_id (FK)
                    sys_menu      │
                    ┌─────────────▼──────┐
                    │ id (PK)            │
                    │ parent_id          │←自引用(0=顶级)
                    │ menu_name          │
                    │ menu_type (M/C/F)  │
                    │ path               │
                    │ component          │
                    │ icon               │
                    │ sort               │
                    │ visible            │
                    │ children           │← @TableField(exist=false)
                    │ checked            │← @TableField(exist=false)
                    └────────────────────┘
```

**Menu 的两个非数据库字段**：
- `children: List<Menu>` — 子菜单列表，`buildTree()` 递归填充
- `checked: Boolean` — 该菜单是否已分配给某个角色，`getRoleTree()` 填充

### 4. 初始化：启动时自动创建权限数据

`DataInitializer` 在应用首次启动时执行（检测表为空才插入）：

```java
// === initRoles() — 3个内置角色 ===
Role(id=1, roleName="超级管理员", roleCode="ROLE_ADMIN")
Role(id=2, roleName="班主任",     roleCode="ROLE_TEACHER")
Role(id=3, roleName="财务人员",   roleCode="ROLE_FINANCE")

// === initMenus() — 12个菜单(9个顶级 + 3个子菜单) ===
// 顶级菜单
Menu(id=1,  parentId=0, menuName="首页",     menuType="C", path="/dashboard",    icon="HomeFilled")
Menu(id=2,  parentId=0, menuName="系统管理", menuType="M", path="/system",       icon="Setting")
Menu(id=3,  parentId=0, menuName="班级管理", menuType="C", path="/class",        icon="School")
Menu(id=4,  parentId=0, menuName="儿童档案", menuType="C", path="/child",        icon="UserFilled")
Menu(id=5,  parentId=0, menuName="教职工管理",menuType="C", path="/teacher",     icon="Avatar")
Menu(id=6,  parentId=0, menuName="园区活动", menuType="C", path="/activity",     icon="Trophy")
Menu(id=7,  parentId=0, menuName="通知公告", menuType="C", path="/notice",       icon="Bell")
Menu(id=8,  parentId=0, menuName="财务管理", menuType="C", path="/finance",      icon="Money")
Menu(id=9,  parentId=0, menuName="考勤管理", menuType="C", path="/attendance",   icon="Clock")
// 子菜单（parentId=2，挂载在"系统管理"下）
Menu(id=21, parentId=2, menuName="用户管理", menuType="C", path="/system/user",  icon="User")
Menu(id=22, parentId=2, menuName="角色管理", menuType="C", path="/system/role",  icon="Key")
Menu(id=23, parentId=2, menuName="菜单管理", menuType="C", path="/system/menu",  icon="Menu")

// === initRoleMenus() — 3个角色分别拥有的菜单 ===
超管(roleId=1) → [1,2,3,4,5,6,7,8,9,21,22,23]  → 全部12个
班主任(roleId=2) → [1,3,4,6,7,9]                 → 首页/班级/儿童/活动/通知/考勤 6个
财务(roleId=3)   → [1,7,8]                       → 首页/通知/财务 3个
```

### 5. 登录时：根据角色返回动态菜单

```
POST /api/auth/login {username, password}
  │
  ▼
AuthController.login()
  │
  ├─ ① 验证用户名密码 (BCrypt)
  ├─ ② session.setAttribute("loginUser", user)
  │
  └─ ③ MenuService.getUserMenus(user.getRoleId())
       │
       ├─ RoleMenuMapper.selectMenuIdsByRoleId(roleId)
       │   └→ SQL: SELECT menu_id FROM sys_role_menu
       │           WHERE role_id = ? AND is_deleted = 0
       │   返回: [1,2,3,4,5,6,7,8,9,21,22,23]  (超管为例)
       │
       ├─ MenuMapper.selectList(in(menuIds) + visible=1 + orderBy sort)
       │   └→ SQL: SELECT * FROM sys_menu
       │           WHERE id IN (1,2,3,...) AND visible=1
       │           ORDER BY sort ASC
       │
       ├─ 过滤: 只要 M(目录) 和 C(菜单)，不要 F(按钮)
       │
       └─ buildTree(menus, 0L)  递归构建树形结构
            │
            ├─ parentId=0 的作为第一层
            ├─ parentId>0 的作为对应父菜单的 children
            └─ 返回完整的菜单树
```

**`buildTree` 核心算法**：

```java
private List<Menu> buildTree(List<Menu> all, Long parentId) {
    List<Menu> tree = new ArrayList<>();
    for (Menu menu : all) {
        if (menu.getParentId().equals(parentId)) {
            // 递归：当前节点的子节点 = parentId等于当前节点id的所有节点
            menu.setChildren(buildTree(all, menu.getId()));
            tree.add(menu);
        }
    }
    return tree;
}
```

**返回值示例**（超管admin登录后获得的菜单）：

```json
[
  { "id":1, "menuName":"首页", "path":"/dashboard", "children":[] },
  { "id":2, "menuName":"系统管理", "path":"/system", "children":[
      { "id":21, "menuName":"用户管理", "path":"/system/user" },
      { "id":22, "menuName":"角色管理", "path":"/system/role" },
      { "id":23, "menuName":"菜单管理", "path":"/system/menu" }
  ]},
  { "id":3, "menuName":"班级管理", "path":"/class", "children":[] },
  ...
]
```

### 6. 前端渲染：Sidebar 动态菜单

```vue
<!-- components/layout/Sidebar.vue -->
<template v-for="menu in userStore.menus" :key="menu.id">
  <!-- 有 children → 可展开的子菜单组 -->
  <el-sub-menu v-if="menu.children?.length" :index="String(menu.id)">
    <template #title>
      <el-icon><component :is="menu.icon" /></el-icon>
      <span>{{ menu.menuName }}</span>
    </template>
    <el-menu-item v-for="child in menu.children" :key="child.id"
                  :index="child.path">
      <span>{{ child.menuName }}</span>
    </el-menu-item>
  </el-sub-menu>

  <!-- 无 children → 直接可点击的菜单项 -->
  <el-menu-item v-else :index="menu.path">
    <el-icon><component :is="menu.icon" /></el-icon>
    <span>{{ menu.menuName }}</span>
  </el-menu-item>
</template>
```

**效果**：
- admin 登录 → 侧边栏显示全部 12 个菜单项
- 班主任登录 → 侧边栏只显示 6 个（看不到系统管理、财务管理、用户管理等）
- 财务登录 → 侧边栏只显示 3 个

### 7. 权限分配操作（超级管理员修改角色权限）

```
管理员点击「角色管理」→ 某角色的「分配权限」按钮
  │
  ▼
前端调用链:
  ① getMenuTree()       → GET /api/menu/tree          → 获取全量菜单树
  ② getRoleMenuIds(id)  → GET /api/role/{id}/menuIds  → 获取该角色已有权限
  │
  ├─ ①返回完整菜单树，作为 el-tree 的 :data
  └─ ②返回已分配的 menuId 列表，作为 el-tree 的 :default-checked-keys
  │
  ▼
┌───────────────────────────────────────────┐
│  el-tree 组件显示:                         │
│                                           │
│  ☑ 首页                                   │
│  ☑ 系统管理                               │
│    ☑ 用户管理                             │
│    ☑ 角色管理                             │
│    ☑ 菜单管理                             │
│  ☑ 班级管理                               │
│  ☑ 儿童档案                               │
│  ☐ 教职工管理    ← 未勾选                  │
│  ☑ 园区活动                               │
│  ☑ 通知公告                               │
│  ☐ 财务管理      ← 未勾选                  │
│  ☑ 考勤管理                               │
└───────────────────────────────────────────┘
  │
  │ 管理员勾选/取消勾选 → 点击保存
  ▼
③ assignMenus(roleId, menuIds)
   → PUT /api/role/{id}/menus  Body: { menuIds: [1,2,21,22,23,3,4,...] }
   │
   ▼
RoleServiceImpl.assignMenus(roleId, menuIds)
   │
   ├─ ① 删除旧关联
   │   roleMenuMapper.delete(
   │       new LambdaQueryWrapper<RoleMenu>()
   │           .eq(RoleMenu::getRoleId, roleId))
   │   └→ SQL: DELETE FROM sys_role_menu WHERE role_id = ?
   │
   └─ ② 逐个插入新关联
       for (Long menuId : menuIds) {
           RoleMenu rm = new RoleMenu();
           rm.setRoleId(roleId);
           rm.setMenuId(menuId);
           roleMenuMapper.insert(rm);
       }
       └→ SQL: INSERT INTO sys_role_menu (role_id, menu_id) VALUES (?, ?)
```

**`@Transactional` 注解确保两步操作原子性**：先删后插，要么全部成功，要么全部回滚，不会出现"删了旧的但新的没插进去"的中间状态。

### 8. 获取角色已有权限（前端回显）

```java
// RoleServiceImpl.getMenuIds()
public List<Long> getMenuIds(Long roleId) {
    return roleMenuMapper.selectMenuIdsByRoleId(roleId);
}

// RoleMenuMapper.selectMenuIdsByRoleId()
@Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId} AND is_deleted = 0")
List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
```

返回该角色当前拥有的 menuId 列表，如 `[1,2,3,4,6,7,9,21,22,23]`，前端 el-tree 据此勾选。

### 9. 获取角色菜单树（含勾选状态）

```java
// MenuServiceImpl.getRoleTree()
public List<Menu> getRoleTree(Long roleId) {
    // ①查全部菜单
    List<Menu> all = menuMapper.selectList(
        new LambdaQueryWrapper<Menu>().orderByAsc(Menu::getSort));

    // ②查该角色已有的菜单ID
    List<Long> roleMenuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);

    // ③标记每个菜单是否已被分配
    for (Menu menu : all) {
        menu.setChecked(roleMenuIds.contains(menu.getId()));
    }

    // ④构建树形结构返回
    return buildTree(all, 0L);
}
```

返回示例：
```json
[
  { "id":1, "menuName":"首页",     "checked":true  },
  { "id":2, "menuName":"系统管理", "checked":true, "children":[...] },
  { "id":3, "menuName":"班级管理", "checked":true  },
  { "id":5, "menuName":"教职工管理","checked":false  },  ← 未分配
  ...
]
```

### 10. 完整权限数据流总结

```
┌─────────────────────────────────────────────────────────────┐
│  启动时                                                      │
│  DataInitializer.run()                                      │
│    → 表空则插入 3角色+12菜单+角色→菜单关联                     │
│    → sys_role_menu 表:                                      │
│        role_id=1 → menu_ids=[1,2,3,4,5,6,7,8,9,21,22,23]  │
│        role_id=2 → menu_ids=[1,3,4,6,7,9]                  │
│        role_id=3 → menu_ids=[1,7,8]                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  登录时                                                      │
│  AuthController.login()                                     │
│    → getUserMenus(roleId)                                   │
│      → 查 sys_role_menu 获取拥有的 menuId 列表               │
│      → 查 sys_menu 获取菜单详情(visible=1, M或C类型)          │
│      → buildTree() 递归构建树形结构                           │
│      → 返回给前端 → Pinia Store → Sidebar.vue 动态渲染        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  管理员修改权限时                                             │
│  RoleServiceImpl.assignMenus(roleId, menuIds)                │
│    → @Transactional 保证原子性                               │
│    → ① DELETE 旧关联 (WHERE role_id=?)                      │
│    → ② INSERT 新关联 (循环插入，每条一个 role_id+menu_id)    │
│    → 下次该角色用户登录时，菜单自动更新                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  前端层面                                                     │
│  Sidebar.vue: 根据 store.menus 渲染                           │
│    有children → el-sub-menu 可展开                            │
│    无children → el-menu-item 直接跳转                         │
│                                                              │
│  role/index.vue: 权限分配对话框                               │
│    el-tree :data="全量菜单树"                                 │
│    el-tree :default-checked-keys="已有权限的menuId列表"       │
│    保存时 → treeRef.getCheckedKeys() + getHalfCheckedKeys()  │
└─────────────────────────────────────────────────────────────┘
```

### 11. 安全性说明

当前实现中，权限控制分两层：

| 层面 | 实现 | 说明 |
|------|------|------|
| **前端** | Sidebar 根据角色渲染不同菜单 | 不同角色看到不同页面数，看不到就无法操作 |
| **后端** | LoginInterceptor 校验登录态 | 未登录无法调用任何API |

未在 Controller 方法级做角色权限校验。对于本项目规模，前端菜单控制 + 后端登录校验已经足够。如需更严格的接口级权限，可在拦截器中增加角色判断逻辑。
