<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    //接收父组件传过来的数据
    pieChartAudit:{
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null,
    }
  },
  watch: {
    pieChartAudit: {
      deep: true,
      handler(val) {
        this.setPieOption(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      //将传来的数据传给图表
      this.setPieOption(this.pieChartAudit)
    },
    setPieOption({ waitAudit, passAudit,failAudit } = {}){
      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['待审核模板','通过模板', '不通过模板']
        },
        series: [
          {
            name: 'WEEKLY WRITE ARTICLES',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 92],
            center: ['50%', '38%'],
            data: [
              { value: waitAudit, name: '待审核模板',itemStyle: {color:'#f5a16c'} },
              { value: passAudit, name: '通过模板',itemStyle: {color:'#94d9af'} },
              { value: failAudit, name: '不通过模板',itemStyle: {color:'#b43f51'} }
            ],
            animationEasing: 'cubicInOut',
            animationDuration: 3600
          }
        ]
      })
    }
  }
}
</script>
