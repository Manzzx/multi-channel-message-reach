<template>
  <div id="container">
    <panel-group />
    <!--图表容器-->
    <div>
      <el-button>ggg</el-button>
    </div>
    <div id="newCharts"></div>
  </div>
</template>


<script>
import PanelGroup from './dashboard/PanelGroup'

export default {
  name: "example",
  components: {
    PanelGroup,

  },
  data() {
    return {
      formatter:'{value}',
    }
  },
  mounted() {
    this.showCharts();
  },
  methods: {
    // 展示折线图
    showCharts(){
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById('newCharts'));//也可以通过$refs.newCharts的方式去获取到dom实例。
      // 绘制图表
      myChart.setOption({
        title: { text: '在vue中使用echarts绘制图表' },//图标的标题
        // X轴
        xAxis: {
          type: 'category',//坐标轴类型,类目轴，适用于离散的类目数据。为该类型时类目数据可自动从 series.data 或 dataset.source 中取，或者可通过 xAxis.data 设置类目数据
          //坐标轴刻度相关设置
          axisTick: {
            alignWithLabel: true,//为true时，可以让刻度跟底部的标签内容对齐
          },
          data: ['星期一','星期二','星期三','星期四','星期五','星期六','星期日'],//X轴的刻度数据
          name: "时间",//坐标轴名称
          nameLocation: "start",//坐标轴名称显示位置
          //坐标轴名称的文字样式
          nameTextStyle: {
            align: "center",//文字水平对齐方式，默认自动
            verticalAlign: "top",//文字垂直对齐方式，默认自动
            lineHeight: 28,//行高
            fontSize: 10,//字体大小
            color: "rgba(0, 0, 0, 1)"//字体颜色
          },
          //坐标轴刻度标签的相关设置
          axisLabel: {
            interval: 'auto'//坐标轴刻度标签的显示间隔，在类目轴中有效。可以设置成 0 强制显示所有标签,如果设置为 1，表示『隔一个标签显示一个标签』，如果值为 2，表示『隔两个标签显示一个标签』，以此类推。
          }
        },
        // Y轴
        yAxis: {
          type: 'value',//坐标轴类型,'value' 数值轴，适用于连续数据
          //坐标轴刻度标签的相关设置
          axisLabel: {
            formatter: this.formatter//刻度标签的内容格式器，支持字符串模板和回调函数两种形式。简单讲就是可以自己格式化标签的内容。
          },
        },
        //直角坐标系内绘图网格,简单理解指的就是这个折线图。
        grid: {
          left: 50//grid 组件离容器左侧的距离
        },
        // 数据
        series: [
          {
            data: [150,45,87,123,89,116,173],//折线图要展示的数据
            type: 'line'//数据以什么样的类型展示。line为折线图
          }
        ]
      })
    },

  }
}
</script>




<style lang="scss" scoped>
#container {
  width: 100%;
  height: 500vh;
  padding: 28px;
  background-color: rgb(240, 242, 245);
  #newCharts{
    margin: 0 auto;
    width: 800px;
    height: 800px;
  }
}
</style>

