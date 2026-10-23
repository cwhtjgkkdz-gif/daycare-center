package com.soft231.daycarecenterbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft231.daycarecenterbackend.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /** 根据角色ID查询菜单ID列表 */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId} AND is_deleted = 0")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
