<template>
  <div class="dashboard-editor-container">
    <div>
      <el-select @change="getEChartsData" v-model="user" placeholder="选择用户">
        <el-option
          v-for="item in userList"
          :key="item.userId"
          :label="item.userName"
          :value="item.userId"
        >
        </el-option>
      </el-select>
    <el-date-picker clearable
                    v-model="day"
                    style="padding-right:-30%;"
                    type="date"
                    value-format="yyyy-MM-dd"
                    @change="getEChartsData"
                    align="left"
                    :picker-options="pickerOptions"
                    placeholder="请选择时间">
    </el-date-picker>
    </div>

    <panel-group style="padding:-10px -10px;margin-top: 3px" :panel-data="showPanelData"/>


    <el-row :gutter="32">

      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart :pie-chart="showPieChartData"/>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart :bar-chart="showBarChartData"/>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart-audit :pie-chart-audit="showPieChartAuditData"/>
        </div>
      </el-col>

      <!--      <el-col :xs="24" :sm="24" :lg="8">-->
      <!--        <div class="chart-wrapper">-->
      <!--          <raddar-chart />-->
      <!--        </div>-->
      <!--      </el-col>-->

    </el-row>

    <div class="block">
      <!--      <span class="demonstration">选择日期</span>-->
<!--      <el-date-picker clearable-->
<!--                      style="position: relative;right: -1047px;"-->
<!--                      v-model="day"-->
<!--                      type="date"-->
<!--                      value-format="yyyy-MM-dd"-->
<!--                      @change="getEChartsData"-->
<!--                      placeholder="请选择时间">-->
<!--      </el-date-picker>-->
    </div>
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:22px;">
      <line-chart :chart-data="showLineChartData"/>
    </el-row>


  </div>
</template>

<script>
import PanelGroup from '@/views/dashboard/PanelGroupUser'
import LineChart from '@/views/dashboard/LineChartUser'
import RaddarChart from '@/views/dashboard/RaddarChart'
import PieChart from '@/views/dashboard/PieChartUser'
// import PieChartAudit from './dashboard/PieChartAudit'
import BarChart from '@/views/dashboard/BarChart'
import PieChartAudit from "@/views/dashboard/PieChartAuditUser";
import {get_index_eCharts_data, get_user_index_eCharts_data, get_users} from "@/api/data/eCharts_data";
import {getMessage_template_for_eCharts} from "@/api/web/message_template";


export default {
  name: 'Index',
  components: {
    PieChartAudit,
    PanelGroup,
    LineChart,
    RaddarChart,
    PieChart,
    BarChart
  },
  data() {
    return {
      user: null,
      day: null,
      userList: null,
      lineChartData: null,
      //第一层版块数据
      showPanelData: {
        totalNum: null,
        dayNum: null,
        temNum: null,
        startNum: null
      },
      //审核模板版块数据
      showPieChartAuditData: {
        waitAudit: null,
        passAudit: null,
        failAudit: null
      },
      //发送情况版块数据
      showPieChartData: {
        success: null,
        sending: null,
        fail: null
      },
      //发送渠道情况数据
      showBarChartData: {
        xAxis: {
          data: [],
        },
        series: [],
      },
      serie: {
        name: null,
        type: null,
        stack: null,
        barWidth: null,
        data: [],
      },
      //发送情况线性版块数据
      showLineChartData: {
        xAxis: {
          data: []
        },
        series: {
          data: {
            successList: [],
            sendingList: [],
            failList: []
          }
        }
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date());
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }]
      },
    }
  },
  created() {
    this.getEChartsData();
    this.getUsers();
  },
  methods: {
    getUsers() {
      get_users().then(response => {
        this.userList = response.data;
        if (this.user === 0) {
          this.user = this.userList[0].userId
        }

      });
    },

    getEChartsData() {
      if (this.user == null){
        this.user = 0;
      }
      get_user_index_eCharts_data(this.day,this.user).then(resp => {
        if (resp.data != null) {
          this.setShowPanelData(resp)
          this.setShowPieChartAuditData(resp)
          this.setShowPieChartData(resp)
          this.setShowBarChartDataData(resp)
          this.setShowLineChartDataData(resp)
        }
      })
    },


    setShowPanelData(resp) {
      this.showPanelData.totalNum = resp.data.showPanelData.totalNum
      this.showPanelData.dayNum = resp.data.showPanelData.dayNum
      this.showPanelData.temNum = resp.data.showPanelData.temNum
      this.showPanelData.startNum = resp.data.showPanelData.startNum
    },
    setShowPieChartAuditData(resp) {
      this.showPieChartAuditData.waitAudit = resp.data.showPieChartAuditData.waitAudit
      this.showPieChartAuditData.passAudit = resp.data.showPieChartAuditData.passAudit
      this.showPieChartAuditData.failAudit = resp.data.showPieChartAuditData.failAudit
    },
    setShowPieChartData(resp) {
      this.showPieChartData.success = resp.data.showPieChartData.success
      this.showPieChartData.sending = resp.data.showPieChartData.sending
      this.showPieChartData.fail = resp.data.showPieChartData.fail
    },
    setShowBarChartDataData(resp) {
      //清空原先数据
      this.showBarChartData.xAxis.data = []
      this.showBarChartData.xAxis.data = resp.data.showBarChartData.xaxis.data

      this.showBarChartData.series = []
      for (let i = 0; i < resp.data.showBarChartData.series.length; i++) {
        const serie = {
          name: resp.data.showBarChartData.series[i].name,
          type: resp.data.showBarChartData.series[i].type,
          stack: resp.data.showBarChartData.series[i].stack,
          barWidth: resp.data.showBarChartData.series[i].barWidth,
          data: resp.data.showBarChartData.series[i].data,
        }
        this.showBarChartData.series.push(serie)
      }
    },
    setShowLineChartDataData(resp) {
      //清空原先数据
      this.showLineChartData.xAxis.data = []
      this.showLineChartData.xAxis.data = resp.data.showLineChartData.xaxis.data

      this.showLineChartData.series.data.successList = resp.data.showLineChartData.series.data.successList
      this.showLineChartData.series.data.sendingList = resp.data.showLineChartData.series.data.sendingList
      this.showLineChartData.series.data.failList = resp.data.showLineChartData.series.data.failList

    },
    // handleSetLineChartData(type) {
    //   this.lineChartData = lineChartData[type]
    // },

  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding-top: 2px;
  padding-left: 15px;
  padding-right: 15px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
