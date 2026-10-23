<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" class="search-bar">
        <el-form-item label="班级">
          <el-select v-model="search.classId" clearable placeholder="全部" style="width:140px">
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="search.date" type="date" value-format="YYYY-MM-DD" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="checkInVisible=true">签到</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="50"/>
        <el-table-column label="儿童" width="100">
          <template #default="{row}">{{ getChildName(row.childId) }}</template>
        </el-table-column>
        <el-table-column label="班级" width="120">
          <template #default="{row}">{{ getClassName(row.classId) }}</template>
        </el-table-column>
        <el-table-column prop="attendanceDate" label="日期" width="110"/>
        <el-table-column prop="checkInTime" label="签到时间" width="160"/>
        <el-table-column prop="checkOutTime" label="签退时间" width="160"/>
        <el-table-column label="状态" width="90">
          <template #default="{row}">
            <el-tag
                :type="row.status==='出勤'?'success':row.status==='迟到'?'warning':row.status==='请假'?'info':'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip/>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{row}">
            <el-button v-if="!row.checkOutTime" type="warning" link @click="handleCheckOut(row.id)">签退</el-button>
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.current" v-model:page-size="search.size" :total="total"
                     :page-sizes="[5,10,20]" layout="total,sizes,prev,pager,next" @change="load"
                     style="margin-top:16px;justify-content:flex-end"/>
    </el-card>

    <!-- 签到 -->
    <el-dialog title="签到" v-model="checkInVisible" width="380px">
      <el-form :model="checkInForm" label-width="80px">
        <el-form-item label="儿童">
          <el-select v-model="checkInForm.childId" style="width:100%" filterable>
            <el-option v-for="c in childList" :key="c.id" :label="c.name + ' (' + getClassName(c.classId) + ')'"
                       :value="c.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="checkInForm.classId" style="width:100%">
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInVisible=false">取消</el-button>
        <el-button type="primary" @click="handleCheckIn">确认签到</el-button>
      </template>
    </el-dialog>

    <!-- 编辑 -->
    <el-dialog title="编辑考勤" v-model="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="出勤" value="出勤"/>
            <el-option label="缺勤" value="缺勤"/>
            <el-option label="迟到" value="迟到"/>
            <el-option label="请假" value="请假"/>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark"/>
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
import {ref, reactive, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getAttendancePage, checkIn, checkOut, updateAttendance, deleteAttendance} from '../../api/attendance'
import {getClassList} from '../../api/class'
import {getChildPage} from '../../api/child'

const search = reactive({current: 1, size: 10, classId: '', date: ''})
const tableData = ref([]), total = ref(0), loading = ref(false)
const checkInVisible = ref(false), checkInForm = reactive({childId: null, classId: null})
const dialogVisible = ref(false), form = reactive({id: null, status: '', remark: ''})
const classList = ref([]), classMap = ref({}), childList = ref([]), childMap = ref({})

const getClassName = (id) => classMap.value[id] || ''
const getChildName = (id) => childMap.value[id] || ''

const load = async () => {
  loading.value = true
  const p = {...search, classId: search.classId || undefined, date: search.date || undefined}
  const {data} = await getAttendancePage(p);
  tableData.value = data.records;
  total.value = data.total;
  loading.value = false
}
const handleCheckIn = async () => {
  await checkIn(checkInForm.childId, checkInForm.classId);
  ElMessage.success('签到成功');
  checkInVisible.value = false;
  load()
}
const handleCheckOut = async (id) => {
  await checkOut(id);
  ElMessage.success('签退成功');
  load()
}
const openDialog = (row) => {
  Object.assign(form, row);
  dialogVisible.value = true
}
const handleSave = async () => {
  await updateAttendance(form);
  ElMessage.success('修改成功');
  dialogVisible.value = false;
  load()
}
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', {type: 'warning'})
  await deleteAttendance(id);
  ElMessage.success('删除成功');
  load()
}

const init = async () => {
  const [{data: classes}, {data: children}] = await Promise.all([
    getClassList(),
    getChildPage({current: 1, size: 100})
  ])
  classList.value = classes
  classes.forEach(c => {
    classMap.value[c.id] = c.className
  })
  childList.value = children.records
  children.records.forEach(c => {
    childMap.value[c.id] = c.name
  })
  load()
}
onMounted(init)
</script>
