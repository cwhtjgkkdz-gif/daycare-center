<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search">
        <el-form-item label="姓名"><el-input v-model="search.name" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="search.position" clearable placeholder="全部" style="width:110px">
            <el-option label="教师" value="教师" /><el-option label="保育员" value="保育员" />
            <el-option label="后勤" value="后勤" /><el-option label="财务" value="财务" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="search.status" clearable placeholder="全部" style="width:100px">
            <el-option label="在职" :value="1" /><el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="success" @click="openDialog()">新增教职工</el-button></el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="name" label="姓名" width="90" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="position" label="岗位" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="hireDate" label="入职日期" width="110" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{ row.status===1?'在职':'离职' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="search.current" v-model:page-size="search.size" :total="total" :page-sizes="[5,10,20]" layout="total,sizes,prev,pager,next" @change="load" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="480px" @closed="formRef?.resetFields()">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="姓名" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio value="男">男</el-radio><el-radio value="女">女</el-radio></el-radio-group></el-form-item>
        <el-form-item label="岗位"><el-select v-model="form.position" style="width:100%"><el-option label="教师" value="教师" /><el-option label="保育员" value="保育员" /><el-option label="后勤" value="后勤" /><el-option label="财务" value="财务" /></el-select></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="入职日期"><el-date-picker v-model="form.hireDate" type="date" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeacherPage, addTeacher, updateTeacher, deleteTeacher } from '../../api/teacher'

const search = reactive({ current: 1, size: 10, name: '', position: '', status: '' })
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({ id: null, name: '', gender: '女', position: '教师', phone: '', hireDate: '', status: 1 })
const dialogTitle = computed(() => isEdit.value ? '编辑教职工' : '新增教职工')

const load = async () => {
  loading.value = true
  const p = { ...search, name: search.name||undefined, position: search.position||undefined, status: search.status!==''?search.status:undefined }
  const { data } = await getTeacherPage(p)
  tableData.value = data.records; total.value = data.total; loading.value = false
}
const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, name: '', gender: '女', position: '教师', phone: '', hireDate: '', status: 1 })
  dialogVisible.value = true
}
const handleSave = async () => {
  isEdit.value ? await updateTeacher(form) : await addTeacher(form)
  ElMessage.success(isEdit.value?'修改成功':'新增成功'); dialogVisible.value = false; load()
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  await deleteTeacher(id); ElMessage.success('删除成功'); load()
}
onMounted(load)
</script>
