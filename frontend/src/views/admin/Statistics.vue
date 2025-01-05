<!-- 统计分析页面 -->
<template>
  <div class="statistics">
    <el-row :gutter="20">
      <!-- 成绩统计 -->
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>课程成绩统计</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshGradeStats">刷新</el-button>
          </div>
          <div class="chart-container">
            <div id="gradeDistributionChart" style="height: 300px"></div>
          </div>
          <div class="data-list">
            <el-table :data="courseAverages" style="width: 100%">
              <el-table-column prop="courseName" label="课程名称"></el-table-column>
              <el-table-column prop="avgScore" label="平均分">
                <template slot-scope="scope">
                  {{ Number(scope.row.avgScore).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="passRate" label="及格率">
                <template slot-scope="scope">
                  {{ Number(scope.row.passRate).toFixed(2) }}%
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <!-- 教室使用统计 -->
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>教室使用统计</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshClassroomStats">刷新</el-button>
          </div>
          <div class="chart-container">
            <div id="classroomUsageChart" style="height: 300px"></div>
          </div>
          <div class="data-list">
            <el-table :data="classroomStats" style="width: 100%">
              <el-table-column prop="classroomName" label="教室"></el-table-column>
              <el-table-column prop="usageRate" label="使用率">
                <template slot-scope="scope">
                  <el-progress :percentage="Number(scope.row.usageRate).toFixed(1)"></el-progress>
                </template>
              </el-table-column>
              <el-table-column prop="utilizationRate" label="容量利用率">
                <template slot-scope="scope">
                  <el-progress :percentage="Number(scope.row.utilizationRate).toFixed(1)" type="success"></el-progress>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩预警 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>成绩预警</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshAlerts">刷新</el-button>
          </div>
          <div class="alert-list">
            <el-table :data="gradeAlerts" style="width: 100%">
              <el-table-column prop="studentName" label="学生姓名"></el-table-column>
              <el-table-column prop="courseName" label="课程名称"></el-table-column>
              <el-table-column prop="score" label="成绩">
                <template slot-scope="scope">
                  <span :class="{ 'text-danger': scope.row.score < scope.row.threshold }">
                    {{ scope.row.score }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="threshold" label="预警阈值"></el-table-column>
              <el-table-column label="操作" width="200">
                <template slot-scope="scope">
                  <el-button size="mini" type="warning" @click="handleAlert(scope.row)">处理</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getGradeStats, getClassroomStats, getGradeAlerts } from '@/api/statistics'

export default {
  name: 'Statistics',
  data() {
    return {
      courseAverages: [],
      gradeDistribution: {},
      classroomStats: [],
      gradeAlerts: [],
      gradeChart: null,
      classroomChart: null
    }
  },
  mounted() {
    this.initCharts()
    this.fetchAllData()
  },
  beforeDestroy() {
    if (this.gradeChart) {
      this.gradeChart.dispose()
    }
    if (this.classroomChart) {
      this.classroomChart.dispose()
    }
  },
  methods: {
    async fetchAllData() {
      await Promise.all([
        this.fetchGradeStats(),
        this.fetchClassroomStats(),
        this.fetchGradeAlerts()
      ])
    },
    async fetchGradeStats() {
      try {
        const response = await getGradeStats()
        this.courseAverages = response.data.courseAverages
        this.gradeDistribution = response.data.gradeDistribution
        this.updateGradeChart()
      } catch (error) {
        this.$message.error('获取成绩统计数据失败')
      }
    },
    async fetchClassroomStats() {
      try {
        const response = await getClassroomStats()
        this.classroomStats = Object.entries(response.data.usageRates).map(([name, rate]) => ({
          classroomName: name,
          usageRate: rate,
          utilizationRate: response.data.capacityUtilization[name] || 0
        }))
        this.updateClassroomChart()
      } catch (error) {
        this.$message.error('获取教室统计数据失败')
      }
    },
    async fetchGradeAlerts() {
      try {
        const response = await getGradeAlerts()
        this.gradeAlerts = response.data
      } catch (error) {
        this.$message.error('获取成绩预警数据失败')
      }
    },
    initCharts() {
      this.gradeChart = echarts.init(document.getElementById('gradeDistributionChart'))
      this.classroomChart = echarts.init(document.getElementById('classroomUsageChart'))
    },
    updateGradeChart() {
      const option = {
        title: {
          text: '成绩分布'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['人数']
        },
        xAxis: {
          data: ['优秀', '良好', '中等', '及格', '不及格']
        },
        yAxis: {},
        series: [{
          name: '人数',
          type: 'bar',
          data: [
            this.gradeDistribution.excellent || 0,
            this.gradeDistribution.good || 0,
            this.gradeDistribution.fair || 0,
            this.gradeDistribution.pass || 0,
            this.gradeDistribution.fail || 0
          ],
          itemStyle: {
            color: function(params) {
              const colors = ['#67C23A', '#409EFF', '#E6A23C', '#F56C6C', '#909399']
              return colors[params.dataIndex]
            }
          }
        }]
      }
      this.gradeChart.setOption(option)
    },
    updateClassroomChart() {
      const option = {
        title: {
          text: '教室使用情况'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['使用率', '容量利用率']
        },
        xAxis: {
          type: 'category',
          data: this.classroomStats.map(item => item.classroomName),
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: {
            formatter: '{value}%'
          }
        },
        series: [
          {
            name: '使用率',
            type: 'bar',
            data: this.classroomStats.map(item => item.usageRate)
          },
          {
            name: '容量利用率',
            type: 'bar',
            data: this.classroomStats.map(item => item.utilizationRate)
          }
        ]
      }
      this.classroomChart.setOption(option)
    },
    refreshGradeStats() {
      this.fetchGradeStats()
    },
    refreshClassroomStats() {
      this.fetchClassroomStats()
    },
    refreshAlerts() {
      this.fetchGradeAlerts()
    },
    handleAlert(alert) {
      this.$confirm(`是否确认已处理 ${alert.studentName} 的 ${alert.courseName} 课程预警?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 这里可以添加处理预警的逻辑
        this.$message.success('预警已处理')
        this.fetchGradeAlerts()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.statistics {
  padding: 20px;
}
.chart-container {
  margin-bottom: 20px;
}
.data-list {
  margin-top: 20px;
}
.text-danger {
  color: #F56C6C;
}
.el-card {
  margin-bottom: 20px;
}
</style> 