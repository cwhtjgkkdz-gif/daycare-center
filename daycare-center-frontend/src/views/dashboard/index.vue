<template>
  <div class="dashboard">
    <!-- 欢迎条 -->
    <div class="welcome-bar">
      <div>
        <h2>数据概览</h2>
        <p>实时展示园区核心运营数据</p>
      </div>
      <el-tag type="info" effect="plain">{{ today }}</el-tag>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="18" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="(card, i) in cards" :key="i">
        <div class="stat-card" :style="{ '--card-color': card.color }">
          <div class="stat-icon">
            <el-icon :size="24">
              <component :is="card.icon"/>
            </el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ card.value.toLocaleString() }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
          <div class="stat-badge">
            <el-icon :size="12">
              <Top/>
            </el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="18" style="margin-top:18px">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-title"><span>各班级人数对比</span>
              <el-tag size="small" type="primary" effect="plain">柱状图</el-tag>
            </div>
          </template>
          <div ref="barChart" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-title"><span>教职工岗位分布</span>
              <el-tag size="small" type="success" effect="plain">饼图</el-tag>
            </div>
          </template>
          <div ref="pieChart" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="18" style="margin-top:18px">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-title"><span>近6个月收费趋势</span>
              <el-tag size="small" type="warning" effect="plain">折线图</el-tag>
            </div>
          </template>
          <div ref="lineChart" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-title"><span>儿童男女比例</span>
              <el-tag size="small" type="danger" effect="plain">环形图</el-tag>
            </div>
          </template>
          <div ref="ringChart" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import * as echarts from 'echarts'
import {Top} from '@element-plus/icons-vue'
import {
  getSummary,
  getChildrenPerClass,
  getTeacherByPosition,
  getMonthlyFees,
  getGenderRatio
} from '../../api/dashboard'

const barChart = ref(), pieChart = ref(), lineChart = ref(), ringChart = ref()

const today = new Date().toLocaleDateString('zh-CN', {year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'})

const cards = reactive([
  {label: '在园儿童', value: 0, icon: 'UserFilled', color: '#409EFF'},
  {label: '教职工', value: 0, icon: 'Avatar', color: '#67C23A'},
  {label: '班级数量', value: 0, icon: 'School', color: '#E6A23C'},
  {label: '本月收费(元)', value: 0, icon: 'Money', color: '#F56C6C'}
])

onMounted(async () => {
  const {data: s} = await getSummary()
  cards[0].value = s.totalChildren ?? 0
  cards[1].value = s.totalTeacher ?? 0
  cards[2].value = s.totalClasses ?? 0
  cards[3].value = s.monthlyFee ?? 0

  const {data: barD} = await getChildrenPerClass()
  initChart(barChart.value, {
    tooltip: {},
    grid: {left: 50, right: 20, top: 10, bottom: 40},
    xAxis: {type: 'category', data: barD.map(d => d.name), axisLabel: {rotate: 25, fontSize: 11}},
    yAxis: {type: 'value'},
    series: [{
      type: 'bar',
      data: barD.map(d => d.value),
      barWidth: '50%',
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{offset: 0, color: '#409EFF'}, {
          offset: 1,
          color: '#79bbff'
        }])
      }
    }]
  })

  const {data: pieD} = await getTeacherByPosition()
  initChart(pieChart.value, {
    tooltip: {trigger: 'item'},
    legend: {bottom: 0},
    series: [{
      type: 'pie',
      radius: ['50%', '75%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      label: {show: false},
      emphasis: {scaleSize: 8},
      data: pieD.map(d => ({name: d.name, value: d.value}))
    }]
  })

  const {data: lineD} = await getMonthlyFees()
  initChart(lineChart.value, {
    tooltip: {},
    grid: {left: 60, right: 20, top: 10, bottom: 30},
    xAxis: {type: 'category', data: lineD.map(d => d.name)},
    yAxis: {type: 'value'},
    series: [{
      type: 'line',
      data: lineD.map(d => d.value),
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {width: 3, color: '#409EFF'},
      itemStyle: {color: '#409EFF'},
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
          offset: 0,
          color: 'rgba(64,158,255,0.2)'
        }, {offset: 1, color: 'rgba(64,158,255,0.02)'}])
      }
    }]
  })

  const {data: ringD} = await getGenderRatio()
  initChart(ringChart.value, {
    tooltip: {trigger: 'item'},
    legend: {bottom: 0},
    series: [{
      type: 'pie',
      radius: ['55%', '78%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      label: {show: false},
      data: ringD.map(d => ({name: d.name, value: d.value})),
      itemStyle: {borderRadius: 4, borderColor: '#fff', borderWidth: 3}
    }]
  })
})

const initChart = (el, opt) => {
  if (!el) return
  const c = echarts.init(el)
  c.setOption(opt)
  window.addEventListener('resize', () => c.resize())
}
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
}

.welcome-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.welcome-bar h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1f36;
}

.welcome-bar p {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.stat-row {
  margin: 0 -9px;
}

.stat-row :deep(.el-col) {
  padding: 0 9px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 12px;
  padding: 20px 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: default;
  transition: all 0.25s;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--card-color);
  color: #fff;
  flex-shrink: 0;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #1a1f36;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}

.stat-badge {
  position: absolute;
  right: 12px;
  top: 12px;
  color: var(--card-color);
  opacity: 0.3;
}

.chart-card {
  border-radius: 12px;
  overflow: hidden;
}

.chart-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title span {
  font-weight: 600;
  font-size: 15px;
  color: #1a1f36;
}
</style>
