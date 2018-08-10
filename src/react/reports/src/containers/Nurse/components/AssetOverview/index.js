import React from 'react'
import { Table } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import { connect } from 'dva'
import { uniqBy } from 'lodash-es'
import './index.scss'
import nursePicBg from '#/assets/bgd.png'
import nursePic from '#/assets/nurse.png'

const Header = () => (
  <div className="asset-overview-header">
    <img src={nursePicBg}/>
    <img src={nursePic}/>
  </div>
)

const Count = ({ all, type, t }) => (
  <nav className="level asset-count">
    <div className="level-left">
      <div className="level-item has-text-centered">
        <div>
          <p>{t`设备总数`}</p>
          <p>{all}</p>
        </div>
      </div>
      <div className="level-item has-text-centered">
        <div>
          <p>{t`设备类型`}</p>
          <p>{type}</p>
        </div>
      </div>
    </div>
  </nav>
)

class AssetTable extends React.Component {

  getCols = (data) => {
    let { t } = this.props
    return [
      {
        title: t`设备名称`,
        dataIndex: 'name',
        key: 'name',
        sorter: true,
      },
      {
        title: t`资产编号`,
        dataIndex: 'financingNum',
        key: 'financingNum',
        sorter: true,
      },
      {
        title: t`设备分类`,
        dataIndex: 'assetGroupName',
        key: 'assetGroupName',
        sorter: true,
      },
      {
        title: t`使用科室`,
        dataIndex: 'clinicalDeptName',
        key: 'clinicalDeptName',
        sorter: true,
      },
    ]
  }

  render() {
    let { t, data } = this.props
    return (
      <Table dataSource={data} columns={this.getCols(data)} className="asset-table" pagination={false} />
    )
  }
}


export class AssetOverview extends React.Component {

  render() {
    let { t, assets } = this.props

    let typeCount = assets.length
    return (
      <div className="asset-overview">

        <h1>{t`科室总设备`}</h1>

        <div className="asset-overview-content">
          <Header />
          <Count t={t} all={assets.length} type={_.uniqBy(assets, a => a.assetGroup).length}/>
          <AssetTable t={t} data={assets} />
        </div>

      </div>
    )
  }

}

function mapState2Props(state) {
  return {
    assets: state.Nurse.assets,
  }
}

export default flow(
  AssetOverview,
  translate(),
  connect(mapState2Props)
)