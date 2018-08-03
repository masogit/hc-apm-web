import React from 'react'
import { Table } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import { connect } from 'dva'
import './index.scss'

class MalfunctionTable extends React.Component {

  getCols = () => {
    let { t } = this.props
    return [
      {
        title: t`设备名称`
      },
      {
        title: t`资产编号`
      },
      {
        title: t`报修日期`
      },
      {
        title: t`报修人`
      },
      {
        title: t`使用科室`
      },
    ]
  }

  render() {
    let { t, data } = this.props
    return (
      <Table dataSource={data} columns={this.getCols(t)} className="asset-table" pagination={false} />
    )
  }
}


export class AssetOverview extends React.Component {

  render() {
    let { t, assetCount, malfuncs } = this.props

    return (
      <div className="malfunction-assets">

        <h1>{t`报修中的设备`}({malfuncs.length})</h1>

        <div className="asset-overview-content">
          <MalfunctionTable t={t} data={malfuncs} />
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