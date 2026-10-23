<template>
  <div>
    <el-card>
      <div style="margin-bottom:12px"><el-button type="success" @click="openDialog()">新增菜单</el-button></div>

      <el-table :data="menuTree" border row-key="id" v-loading="loading" default-expand-all>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="menuName" label="菜单名称" width="160" />
        <el-table-column label="类型" width="80">
          <template #default="{row}"><el-tag size="small" :type="row.menuType==='M'?'':'success'">{{ row.menuType==='M'?'目录':row.menuType==='C'?'菜单':'按钮' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="component" label="前端组件" width="200" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="perms" label="权限标识" width="150" />
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="520px" @closed="formRef?.resetFields()">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="菜单名称" required><el-input v-model="form.menuName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="类型"><el-select v-model="form.menuType" style="width:100%"><el-option label="目录" value="M" /><el-option label="菜单" value="C" /><el-option label="按钮" value="F" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="父菜单ID"><el-input-number v-model="form.parentId" :min="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="路由路径"><el-input v-model="form.path" /></el-form-item>
        <el-form-item label="前端组件"><el-input v-model="form.component" placeholder="如: system/user/index" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="如: Setting" /></el-form-item>
        <el-form-item label="权限标识"><el-input v-model="form.perms" /></el-form-item>
        <el-form-item label="是否可见"><el-switch v-model="form.visible" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '../../../api/menu'

const loading = ref(false), menuTree = ref([])
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({ id: null, parentId: 0, menuName: '', menuType: 'C', path: '', component: '', icon: '', perms: '', sort: 0, visible: 1 })
const dialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')

const load = async () => {
  loading.value = true
  const { data } = await getMenuTree(); menuTree.value = data; loading.value = false
}
const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, parentId: 0, menuName: '', menuType: 'C', path: '', component: '', icon: '', perms: '', sort: 0, visible: 1 })
  dialogVisible.value = true
}
const handleSave = async () => {
  isEdit.value ? await updateMenu(form) : await addMenu(form)
  ElMessage.success(isEdit.value?'修改成功':'新增成功'); dialogVisible.value = false; load()
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该菜单吗？子菜单也会被删除', '提示', { type: 'warning' })
  await deleteMenu(id); ElMessage.success('删除成功'); load()
}
onMounted(load)
</script>
