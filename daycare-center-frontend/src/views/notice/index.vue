<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search">
        <el-form-item label="标题">
          <el-input v-model="search.title" placeholder="请输入" clearable/>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="search.type" clearable placeholder="全部" style="width:120px">
            <el-option label="通知" value="通知"/>
            <el-option label="公告" value="公告"/>
            <el-option label="温馨提示" value="温馨提示"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="openDialog()">新增通知</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50"/>
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip/>
        <el-table-column prop="type" label="类型" width="110"/>
        <el-table-column prop="publisher" label="发布人" width="90"/>
        <el-table-column prop="publishTime" label="发布时间" width="160"/>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.current" v-model:page-size="search.size" :total="total"
                     :page-sizes="[5,10,20]" layout="total,sizes,prev,pager,next" @change="load"
                     style="margin-top:16px;justify-content:flex-end"/>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px" @closed="formRef?.resetFields()">
      <el-form :model="form" ref="formRef" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title"/>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="通知" value="通知"/>
            <el-option label="公告" value="公告"/>
            <el-option label="温馨提示" value="温馨提示"/>
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4"/>
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="form.publisher"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, computed, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getNoticePage, addNotice, updateNotice, deleteNotice} from '../../api/notice'

const search = reactive({current: 1, size: 10, title: '', type: ''})
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({id: null, title: '', type: '通知', content: '', publisher: ''})
const dialogTitle = computed(() => isEdit.value ? '编辑通知' : '新增通知')

const load = async () => {
  loading.value = true
  const p = {...search, title: search.title || undefined, type: search.type || undefined}
  const {data} = await getNoticePage(p);
  tableData.value = data.records;
  total.value = data.total;
  loading.value = false
}
const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, {id: null, title: '', type: '通知', content: '', publisher: ''})
  dialogVisible.value = true
}
const handleSave = async () => {
  isEdit.value ? await updateNotice(form) : await addNotice(form)
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功');
  dialogVisible.value = false;
  load()
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', {type: 'warning'})
  await deleteNotice(id);
  ElMessage.success('删除成功');
  load()
}
onMounted(load)
</script>
