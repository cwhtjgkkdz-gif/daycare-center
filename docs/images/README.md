# 界面截图

运行项目后，把截图放到本目录，**文件名与下表一一对应**（README 里的图片占位符会引用这些文件名）。

建议截图分辨率：**1440×900 或 1920×1080**，格式 PNG，一张图尽量控制在 500KB 以内（太大可用工具压缩，避免仓库膨胀）。

| 文件名 | 截图内容 |
|--------|----------|
| `login.png` | 登录页 |
| `dashboard.png` | 数据看板（首页） |
| `child.png` | 儿童管理列表页 |
| `attendance.png` | 考勤管理页 |
| `finance.png` | 财务管理页 |
| `role.png` | 权限管理（角色/菜单分配）页 |

## 操作步骤

1. 启动后端（`./mvnw spring-boot:run`）和前端（`npm run dev`）
2. 登录系统后逐个进入上述页面截图
3. 将图片重命名后放入本目录（`docs/images/`）
4. 提交推送：

```bash
git add docs/images
git commit -m "docs: 补充界面截图"
git push
```

放好后，根目录 README 的「界面截图」板块会自动显示对应图片。
