<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search">
        <el-form-item label="流水号"><el-input v-model="search.recordNo" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="search.type" clearable placeholder="全部" style="width:100px">
            <el-option label="收费" value="收费" /><el-option label="退费" value="退费" />
          </el-select>
        </el-form-item>
        <el-form-item label="儿童"><el-input v-model="search.childName" placeholder="儿童姓名" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
        <el-form-item><el-button type="success" @click="openDialog()">新增记录</el-button></el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="recordNo" label="流水号" width="180" />
        <el-table-column prop="childName" label="儿童" width="80" />
        <el-table-column label="类型" width="80">
          <template #default="{row}"><el-tag :type="row.type==='收费'?'success':'warning'">{{ row.type }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="amount" label="金额(元)" width="100">
          <template #default="{row}">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="handler" label="经办人" width="90" />
        <el-table-column prop="handleTime" label="办理时间" width="160" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
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
        <el-form-item label="儿童姓名" required><el-input v-model="form.childName" /></el-form-item>
        <el-form-item label="类型" required><el-radio-group v-model="form.type"><el-radio value="收费">收费</el-radio><el-radio value="退费">退费</el-radio></el-radio-group></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="经办人"><el-input v-model="form.handler" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFinancePage, addFinance, updateFinance, deleteFinance } from '../../api/finance'

const search = reactive({ current: 1, size: 10, recordNo: '', type: '', childName: '' })
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({ id: null, childName: '', type: '收费', amount: 0, handler: '', remark: '' })
const dialogTitle = computed(() => isEdit.value ? '编辑记录' : '新增记录')

const load = async () => {
  loading.value = true
  const p = { ...search, recordNo: search.recordNo||undefined, type: search.type||undefined, childName: search.childName||undefined }
  const { data } = await getFinancePage(p); tableData.value = data.records; total.value = data.total; loading.value = false
}
const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, childName: '', type: '收费', amount: 0, handler: '', remark: '' })
  dialogVisible.value = true
}
const handleSave = async () => {
  isEdit.value ? await updateFinance(form) : await addFinance(form)
  ElMessage.success(isEdit.value?'修改成功':'新增成功'); dialogVisible.value = false; load()
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  await deleteFinance(id); ElMessage.success('删除成功'); load()
}
onMounted(load)
</script>
