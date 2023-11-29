<template>
  <div :class="className" :style="{height:height,width:width}"/>
</template>

<script>
import * as echarts from 'echarts'

require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      // default: 'chart',
      width: '100%',
      height: '300px'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    userEncode: {
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
    userEncode: {
      deep: true,
      handler(val) {
        this.setBar(val)
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
      this.setBar(this.userEncode)
    },
    setBar({source}) {
      this.chart.setOption({
        dataset: {
          source: source
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        xAxis: {name: 'amount',
          // minInterval: 1, //不允许出现小数位
        },
        yAxis: {type: 'category'},
        series: [
          {
            type: 'bar',
            encode: {
              // Map the "amount" column to X axis.
              x: 'amount',

              // Map the "product" column to Y axis
              y: 'product'
            },
            itemStyle: {
              normal: {
                //这里是循环开始的地方
                color: function(params) {
                  var colorList = ['#4564cc', '#92CDBB', '#EDD977', '#E89F6A', '#92CDCB', '#82CDBB', '#96CDBB', '#EDD877', '#EDC977']
                  if (params.dataIndex >= colorList.length) {
                    params.dataIndex = params.dataIndex - colorList.length
                  }
                  return colorList[params.dataIndex]
                },
              }
            }
          }
        ]
      })
    }
  }
}
</script>
