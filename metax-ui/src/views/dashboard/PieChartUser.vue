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
    pieChart:{
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    pieChart: {
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
      this.setPieOption(this.pieChart)
    },
    setPieOption({success, sending, fail}) {
    this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['发送成功','发送中', '发送失败']
        },
        series: [
          {
            name: 'WEEKLY WRITE ARTICLES',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 100],
            center: ['50%', '38%'],
            data: [
              { value: success, name: '发送成功',itemStyle: {color:'#48c046'} },
              { value: sending, name: '发送中',itemStyle: {color:'#fc8251'} },
              { value: fail, name: '发送失败',itemStyle: {color:'#dc1a2b'} }
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
