import React from 'react'
import { Table } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import { connect } from 'dva'
import Chart, { echarts } from './chart'
import './index.scss'

const getOption = (t, data) =>{
  let labels = [], values = []
  data.forEach(d => {
    labels.push(d.assetGroupName)
    values.push(d.avgResponseTime)
  })
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      }
    },
    grid: {
      left: '6%',
      right: '4%',
      bottom: '8%',
      top: '8%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: labels,
      axisTick: {
        alignWithLabel: true,
      },
      axisLabel: {
        textStyle: {
          color: '#999',
        },
        rotate: 45,
      },
    },
    yAxis: {
      name: t`分钟`,
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
    series: [{
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        normal: {
          barBorderRadius: [100, 100, 0, 0],
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1, [{
                offset: 0,
                color: '#fab174'
              },
              {
                offset: 0.5,
                color: '#cb79b4'
              },
              {
                offset: 1,
                color: '#b65ecf'
              }
            ]
          )
        },
      },
      data: values,
    }]
  }
}

export class AssetOverview extends React.Component {

  render() {
    let { t, avg} = this.props

    return (
      <div className="response-time-chart">

        <h1>{t`平均响应时间`}</h1>

        <div className="chart-content">
          <Chart option={getOption(t, avg)} id="avg-response-time-chart"/>
        </div>

      </div>
    )
  }

}

function mapState2Props(state) {
  return {
    avg: state.Nurse.avgResponseTime
  }
}

export default flow(
  AssetOverview,
  translate(),
  connect(mapState2Props)
)