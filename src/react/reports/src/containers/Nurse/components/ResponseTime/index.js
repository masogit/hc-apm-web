import React from 'react'
import { Table } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import { connect } from 'dva'
import Chart, { echarts } from './chart'
import './index.scss'

const option = {
  color: ['#3398DB'],
  tooltip : {
      trigger: 'axis',
      axisPointer : {            // 坐标轴指示器，坐标轴触发有效
          type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
      }
  },
  grid: {
      left: '6%',
      right: '4%',
      bottom: '8%',
      top: '8%',
      containLabel: true
  },
  xAxis: {
    type : 'category',
    data : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    axisTick: {
      alignWithLabel: true
    },
    axisLabel: {
      textStyle: {
          color: '#999'
      }
    }
  },
  yAxis: {
    name: '分钟',
    nameLocation: 'middle',
    nameGap: 35,
    nameTextStyle: {
      color: '#999',
      fontSize: 15,
    },
    axisLine: {
      show: false
    },
    axisTick: {
        show: false
    },
    axisLabel: {
        textStyle: {
            color: '#999'
        }
    }
},
  series : [
      {
          name:'直接访问',
          type:'bar',
          barWidth: '60%',
          itemStyle: {
            normal: {
              barBorderRadius: [100, 100, 0, 0],
              color: new echarts.graphic.LinearGradient(
                0, 0, 0, 1,
                [
                    {offset: 0, color: '#fab174'},
                    {offset: 0.5, color: '#cb79b4'},
                    {offset: 1, color: '#b65ecf'}
                ]
              )
          },
          },
          data:[10, 52, 200, 334, 390, 330, 220]
      }
  ]
};


export class AssetOverview extends React.Component {

  render() {
    let { t, assetCount, malfuncs } = this.props

    return (
      <div className="response-time-chart">

        <h1>{t`平均响应时间`}</h1>

        <div className="chart-content">
          <Chart option={option} id="test1"/>
        </div>

      </div>
    )
  }

}

function mapState2Props(state) {
  return {
    malfuncs: [1,2,3,4,5,6,7,8,9]
  }
}

export default flow(
  AssetOverview,
  translate(),
  connect(mapState2Props)
)