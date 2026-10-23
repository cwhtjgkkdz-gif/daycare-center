<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search">
        <el-form-item label="用户名"><el-input v-model="search.username" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="search.realName" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="search.status" clearable placeholder="全部" style="width:100px">
            <el-option label="启用" :value="1" /><el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="success" @click="openDialog()">新增用户</el-button></el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleName" label="角色" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button :type="row.status===1?'warning':'success'" link @click="toggleStatus(row)">
              {{ row.status===1?'禁用':'启用' }}
            </el-button>
            <el-button type="info" link @click="handleResetPwd(row.id)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.current" v-model:page-size="search.size" :total="total" :page-sizes="[5,10,20]" layout="total,sizes,prev,pager,next" @change="load" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="460px" @closed="formRef?.resetFields()">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="用户名" required><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="form.roleId" style="width:100%">
            <el-option label="超级管理员" :value="1" /><el-option label="班主任" :value="2" /><el-option label="财务人员" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, addUser, updateUser, deleteUser, updateUserStatus, resetPassword } from '../../../api/user'

const search = reactive({ current: 1, size: 10, username: '', realName: '', status: '' })
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({ id: null, username: '', realName: '', phone: '', email: '', roleId: 2 })
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const load = async () => {
  loading.value = true
  const p = { ...search, username: search.username||undefined, realName: search.realName||undefined, status: search.status!==''?search.status:undefined }
  const { data } = await getUserPage(p); tableData.value = data.records; total.value = data.total; loading.value = false
}
const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, username: '', realName: '', phone: '', email: '', roleId: 2 })
  dialogVisible.value = true
}
const handleSave = async () => {
  isEdit.value ? await updateUser(form) : await addUser(form)
  ElMessage.success(isEdit.value?'修改成功':'新增成功，默认密码123456'); dialogVisible.value = false; load()
}
const toggleStatus = async (row) => {
  const s = row.status===1?0:1
  await updateUserStatus(row.id, s); ElMessage.success('状态已更新'); load()
}
const handleResetPwd = async (id) => {
  await ElMessageBox.confirm('确定重置密码为123456吗？', '提示', { type: 'warning' })
  await resetPassword(id); ElMessage.success('密码已重置为123456')
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
  await deleteUser(id); ElMessage.success('删除成功'); load()
}
onMounted(load)
</script>
