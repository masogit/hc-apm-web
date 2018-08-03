import React from 'react'
import { Row, Col, Slicer } from 'antd'
import { flow } from 'noru-utils/lib'
import { translate } from 'react-i18next'
import AssetOverview from './components/AssetOverview'
import MalfunctionAssets from './components/MalfunctionAssets'
import ResponseTime from './components/ResponseTime'
import './index.scss'

export class Nurse extends React.Component {



  render() {

    return (
      <Row gutter={16} className="nurse-station">
        <Col span="8" className="left-container">
          <AssetOverview />
        </Col>
        <Col span="16" className="right-container">
          <MalfunctionAssets />
          <ResponseTime />
        </Col>
      </Row>
    )
  }

}

export default flow(
  Nurse,
  translate()
)