<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" style="margin-bottom:12px">
        <el-form-item label="班级名称"><el-input v-model="search.className" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="班级类型">
          <el-select v-model="search.classType" placeholder="全部" clearable style="width:130px">
            <el-option label="托班" value="托班" /><el-option label="小班" value="小班" />
            <el-option label="中班" value="中班" /><el-option label="大班" value="大班" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="success" @click="openDialog()">新增班级</el-button></el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="className" label="班级名称" width="140" />
        <el-table-column prop="classType" label="类型" width="100" />
        <el-table-column prop="maxCapacity" label="最大容量" width="100" />
        <el-table-column prop="currentCount" label="当前人数" width="100" />
        <el-table-column prop="description" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="search.current" v-model:page-size="search.size"
        :total="total" :page-sizes="[5,10,20]" layout="total,sizes,prev,pager,next" @change="load"
        style="margin-top:16px;justify-content:flex-end"
      />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="班级名称" prop="className"><el-input v-model="form.className" /></el-form-item>
        <el-form-item label="班级类型" prop="classType">
          <el-select v-model="form.classType" style="width:100%">
            <el-option label="托班" value="托班" /><el-option label="小班" value="小班" />
            <el-option label="中班" value="中班" /><el-option label="大班" value="大班" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大容量"><el-input-number v-model="form.maxCapacity" :min="10" :max="50" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClassPage, addClass, updateClass, deleteClass } from '../../api/class'

const search = reactive({ current: 1, size: 10, className: '', classType: '' })
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({ id: null, className: '', classType: '小班', maxCapacity: 30, description: '' })
const rules = { className: [{ required: true, message: '请输入班级名称' }], classType: [{ required: true, message: '请选择类型' }] }
const dialogTitle = computed(() => isEdit.value ? '编辑班级' : '新增班级')

const load = async () => {
  loading.value = true
  const { data } = await getClassPage({ ...search, className: search.className || undefined, classType: search.classType || undefined })
  tableData.value = data.records; total.value = data.total
  loading.value = false
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

const resetForm = () => { formRef.value?.resetFields(); Object.assign(form, { id: null, className: '', classType: '小班', maxCapacity: 30, description: '' }) }

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  isEdit.value ? await updateClass(form) : await addClass(form)
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false; load()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该班级吗？', '提示', { type: 'warning' })
  await deleteClass(id); ElMessage.success('删除成功'); load()
}

onMounted(load)
</script>
