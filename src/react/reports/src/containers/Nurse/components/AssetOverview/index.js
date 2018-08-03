import React from 'react'
import { Table } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import { connect } from 'dva'
import './index.scss'

const Header = () => (
  <div className="asset-overview-header">
    <img src="../../../../assets/nurse.png"/>
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
        title: t`设备分类`
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
    let { t, assetCount, assets } = this.props

    return (
      <div className="asset-overview">

        <h1>{t`科室总设备`}</h1>

        <div className="asset-overview-content">
          <Header />
          <Count t={t} all={assets.length} type={20}/>
          <AssetTable t={t} data={assets} />
        </div>

      </div>
    )
  }

}

function mapState2Props(state) {
  return {
    assets: [1,2,3,4,5,6,7,8,9]
  }
}

export default flow(
  AssetOverview,
  translate(),
  connect(mapState2Props)
)