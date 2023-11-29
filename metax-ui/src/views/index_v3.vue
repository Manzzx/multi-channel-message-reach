<template>
  <div class="dashboard-editor-container">
    <div>
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
      <el-select @change="getEChartsData" v-model="template" placeholder="请选择模板">
        <el-option
          v-for="item in message_templateList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        >
        </el-option>
      </el-select>
    </div>
    <div>

    </div>

    <panel-group-tem style="padding:-10px -10px;margin-top: 3px" :panel-data="showPanelData"/>


    <el-row :gutter="32">

      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <UserEncode :user-encode="showUserEncodeData"/>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart-tem :pie-chart="showPieChartData"/>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart-tem :bar-chart="showBarChartData"/>
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
      <line-chart-tem :chart-data="showLineChartData"/>
    </el-row>


  </div>
</template>

<script>

import {get_template_eCharts_data} from "@/api/data/eCharts_data";
import UserEncode from "@/views/dashboard/UserEncode";
import PieChartTem from "@/views/dashboard/PieChartTem";
import BarChartTem from "@/views/dashboard/BarChartTem";
import LineChartTem from "@/views/dashboard/LineChartTem";
import PanelGroupTem from "@/views/dashboard/PanelGroupTem";
import {getMessage_template_for_eCharts} from "@/api/web/message_template"


export default {
  name: 'Index',
  components: {
    PanelGroupTem,
    LineChartTem,
    BarChartTem,
    PieChartTem,
    UserEncode,
  },
  data() {
    return {
      template: null,
      message_templateList: null,
      day: null,
      lineChartData: null,
      //第一层版块数据
      showPanelData: {
        totalNum: null,
        dayNum: null,
        deliverAbility: null,
        pushPercentage: null
      },
      //用户情况版块数据
      showUserEncodeData: {
        source: []
      },
      //发送情况版块数据
      showPieChartData: {
        success: null,
        sending: null,
        fail: null
      },
      //当天发送时间段情况数据
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
    this.getTemplates()
  },
  methods: {
    getEChartsData() {
      if (this.template === null) {
        this.template = 0
      }
      get_template_eCharts_data(this.day, this.template).then(resp => {
        this.setShowPanelData(resp)
        this.setShowUserEncodeData(resp)
        this.setShowPieChartData(resp)
        this.setShowBarChartDataData(resp)
        this.setShowLineChartDataData(resp)
      })
    },


    setShowPanelData(resp) {
      this.showPanelData.totalNum = resp.data.showPanelData.totalNum
      this.showPanelData.dayNum = resp.data.showPanelData.dayNum
      this.showPanelData.deliverAbility = resp.data.showPanelData.deliverAbility
      this.showPanelData.pushPercentage = resp.data.showPanelData.pushPercentage
    },
    setShowUserEncodeData(resp) {
      this.showUserEncodeData.source = []
      for (let i = 0; i < resp.data.showUserEncodeData.source.length; i++) {
        this.showUserEncodeData.source.push(resp.data.showUserEncodeData.source[i])
      }
      // this.showUserEncodeData.source = resp.data.showUserEncodeData.source
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

    getTemplates() {
      getMessage_template_for_eCharts().then(response => {
        this.message_templateList = response.data;
        if (this.template === 0) {
          this.template = this.message_templateList[0].id
        }

      });
    },

    handleSetLineChartData(type) {
      this.lineChartData = lineChartData[type]
    }
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
