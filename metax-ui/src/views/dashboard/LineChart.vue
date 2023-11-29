<template>
<!--  <div>-->
  <div :class="className" :style="{height:height,width:width}">
  </div>

<!--    <p>555555555</p>-->
<!--  </div>-->
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
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
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
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
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
      this.setOptions(this.chartData)
    },
    setOptions({xAxis, series} = {}) {
      this.chart.setOption({
        xAxis: {
          data: xAxis.data,
          boundaryGap: false,
          axisTick: {
            show: false
          },
          axisLine: {
            lineStyle: {
              width: 0 // 设置轴线宽度
            }
          },
          // axisLabel: {
          //   interval:0,      //坐标轴刻度标签的显示间隔(在类目轴中有效) 0:显示所有  1：隔一个显示一个 :3：隔三个显示一个...
          //   rotate:-20    //标签倾斜的角度，显示不全时可以通过旋转防止标签重叠（-90到90）
          // },
        },
        grid: {
          left: 28,
          right: 28,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        legend: {
          data: ['发送成功', '发送中','发送失败']
        },
        series: [{
          name: '发送成功', itemStyle: {
            normal: {
              color: '#48c046',
              lineStyle: {
                color: '#48c046',
                width: 3
              }
            }
          },
          smooth: true,
          type: 'line',
          data: series.data.successList,
          animationDuration: 6800,
          animationEasing: 'cubicInOut'
        },
        {
          name: '发送中',
          smooth: true,
          type: 'line',
          itemStyle: {
            normal: {
              color: '#f5a16c',
              lineStyle: {
                color: '#f5a16c',
                width: 3
              },
              areaStyle: {
                color: '#f3f8ff'
              }
            }
          },
          data: series.data.sendingList,
          animationDuration: 6800,
          animationEasing: 'quadraticOut'
        },
          {
            name: '发送失败',
            smooth: true,
            type: 'line',
            itemStyle: {
              normal: {
                color: '#ec0a11',
                lineStyle: {
                  color: '#ec0a11',
                  width: 3
                },
                areaStyle: {
                  color: '#f3f8ff'
                }
              }
            },
            data: series.data.failList,
            animationDuration: 6800,
            animationEasing: 'quadraticOut'
          }
        ],
        // dataZoom: [{
        //   type: 'slider', // 滑动条型 dataZoom 组件
        //   start: 0, // 起始位置
        //   end: 100, // 结束位置
        //   xAxisIndex: [0], // 需要控制的 x 轴的索引
        //   filterMode: 'filter' // 过滤模式，数据范围外的数据将被过滤掉
        // }]
      })
    }
  }
}
</script>
