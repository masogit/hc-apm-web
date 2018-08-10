import React from 'react'
import { Table } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import { connect } from 'dva'
import moment from 'moment'
import './index.scss'

class MalfunctionTable extends React.Component {

  getCols = () => {
    let { t, assets } = this.props
    return [
      {
        title: t`设备名称`,
        dataIndex: 'assetName',
        sorter: true,
      },
      {
        title: t`资产编号`,
        dataIndex: 'financingNum',
        sorter: true,
      },
      {
        title: t`报修日期`,
        render: record => moment(record.reportDate).format('YYYY-MM-DD HH:mm'),
        sorter: true,
      },
      {
        title: t`报修人`,
        dataIndex: 'reporter',
        sorter: true,
      },
      {
        title: t`使用科室`,
        dataIndex: 'clinicalDept',
        sorter: true,
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


export class AssetsInMaintain extends React.Component {

  render() {
    let { t, assets } = this.props

    return (
      <div className="assets-in-maintain">

        <h1>{t`报修中的设备`}({assets.length})</h1>

        <div className="assets-in-maintain-content">
          <MalfunctionTable t={t} data={assets} />
        </div>

      </div>
    )
  }

}

function mapState2Props(state) {
  return {
    assets: state.Nurse.assetsInMaintain
  }
}

export default flow(
  AssetsInMaintain,
  translate(),
  connect(mapState2Props)
)