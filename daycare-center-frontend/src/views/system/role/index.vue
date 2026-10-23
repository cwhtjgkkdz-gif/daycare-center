<template>
  <div>
    <el-card>
      <el-form :inline="true">
        <el-form-item label="角色名"><el-input v-model="search.roleName" clearable placeholder="请输入" /></el-form-item>
        <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="success" @click="openDialog()">新增角色</el-button></el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleCode" label="编码" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="warning" link @click="openPermDialog(row)">分配权限</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.current" v-model:page-size="search.size" :total="total" :page-sizes="[5,10,20]" layout="total,sizes,prev,pager,next" @change="load" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <!-- 新增/编辑角色 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="460px" @closed="formRef?.resetFields()">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="角色名称" required><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="角色编码" required><el-input v-model="form.roleCode" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>

    <!-- 分配权限 -->
    <el-dialog title="分配权限" v-model="permVisible" width="460px">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedKeys"
        :props="{ label: 'menuName', children: 'children' }"
        default-expand-all
      />
      <template #footer><el-button @click="permVisible=false">取消</el-button><el-button type="primary" @click="handleAssignPerm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRolePage, addRole, updateRole, deleteRole, assignMenus, getRoleMenuIds } from '../../../api/role'
import { getMenuTree } from '../../../api/menu'

const search = reactive({ current: 1, size: 10, roleName: '' })
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({ id: null, roleName: '', roleCode: '', description: '' })
const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')
const permVisible = ref(false), treeRef = ref(), menuTree = ref([]), checkedKeys = ref([]), currentRoleId = ref()

const load = async () => {
  loading.value = true
  const { data } = await getRolePage(search); tableData.value = data.records; total.value = data.total; loading.value = false
}
const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, roleName: '', roleCode: '', description: '' })
  dialogVisible.value = true
}
const handleSave = async () => {
  isEdit.value ? await updateRole(form) : await addRole(form)
  ElMessage.success(isEdit.value?'修改成功':'新增成功'); dialogVisible.value = false; load()
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该角色吗？', '提示', { type: 'warning' })
  await deleteRole(id); ElMessage.success('删除成功'); load()
}
const openPermDialog = async (row) => {
  currentRoleId.value = row.id
  const { data: tree } = await getMenuTree()
  menuTree.value = tree
  const { data: ids } = await getRoleMenuIds(row.id)
  checkedKeys.value = ids
  permVisible.value = true
}
const handleAssignPerm = async () => {
  const keys = treeRef.value.getCheckedKeys()
  const halfKeys = treeRef.value.getHalfCheckedKeys()
  await assignMenus(currentRoleId.value, [...keys, ...halfKeys])
  ElMessage.success('权限分配成功'); permVisible.value = false
}
onMounted(load)
</script>
