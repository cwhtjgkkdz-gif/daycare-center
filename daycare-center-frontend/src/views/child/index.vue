<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search">
        <el-form-item label="姓名">
          <el-input v-model="search.name" placeholder="请输入" clearable/>
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="search.classId" placeholder="班级ID" clearable style="width:120px"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="search.status" clearable placeholder="全部" style="width:100px">
            <el-option label="在园" :value="1"/>
            <el-option label="离园" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="openDialog()">新增儿童</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50"/>
        <el-table-column prop="name" label="姓名" width="90"/>
        <el-table-column prop="gender" label="性别" width="60"/>
        <el-table-column prop="birthDate" label="出生日期" width="110"/>
        <el-table-column label="班级" width="110">
          <template #default="{row}">{{ getClassName(row.classId) }}</template>
        </el-table-column>
        <el-table-column prop="parentName" label="家长" width="90"/>
        <el-table-column prop="parentPhone" label="联系电话" width="120"/>
        <el-table-column prop="allergies" label="过敏史" width="120" show-overflow-tooltip/>
        <el-table-column label="状态" width="80">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'info'">{{ row.status === 1 ? '在园' : '离园' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button :type="row.status===1?'warning':'success'" link @click="toggleStatus(row)">
              {{ row.status === 1 ? '设为离园' : '设为在园' }}
            </el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="form.birthDate" type="date" style="width:100%" value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级">
              <el-select v-model="form.classId" style="width:100%">
                <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="家长姓名">
              <el-input v-model="form.parentName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.parentPhone"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="过敏史">
          <el-input v-model="form.allergies"/>
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
import {getChildPage, addChild, updateChild, deleteChild, updateChildStatus} from '../../api/child'
import {getClassList} from '../../api/class'

const search = reactive({current: 1, size: 10, name: '', classId: '', status: ''})
const classList = ref([])
const classMap = ref({})

const getClassName = (id) => classMap.value[id] || `班级${id}`
const tableData = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), formRef = ref(), isEdit = ref(false)
const form = reactive({
  id: null,
  name: '',
  gender: '男',
  birthDate: '',
  classId: 1,
  parentName: '',
  parentPhone: '',
  allergies: ''
})
const rules = {name: [{required: true, message: '请输入姓名'}], gender: [{required: true}]}
const dialogTitle = computed(() => isEdit.value ? '编辑儿童' : '新增儿童')

const load = async () => {
  loading.value = true
  const params = {
    current: search.current,
    size: search.size,
    name: search.name || undefined,
    classId: search.classId || undefined,
    status: search.status !== '' ? search.status : undefined
  }
  const {data} = await getChildPage(params)
  tableData.value = data.records;
  total.value = data.total
  loading.value = false
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) Object.assign(form, row)
  else Object.assign(form, {
    id: null,
    name: '',
    gender: '男',
    birthDate: '',
    classId: 1,
    parentName: '',
    parentPhone: '',
    allergies: ''
  })
  dialogVisible.value = true
}

const resetForm = () => formRef.value?.resetFields()

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  isEdit.value ? await updateChild(form) : await addChild(form)
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false;
  load()
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await updateChildStatus(row.id, newStatus)
  ElMessage.success('状态已更新');
  load()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', {type: 'warning'})
  await deleteChild(id);
  ElMessage.success('删除成功');
  load()
}

const init = async () => {
  const {data: classes} = await getClassList()
  classList.value = classes
  classes.forEach(c => {
    classMap.value[c.id] = c.className
  })
  load()
}
onMounted(init)
</script>
