import React from 'react'
import AssetBrowser from '#/containers/AssetBrowser'
import AssetPerf from '#/containers/AssetPerf'
import StaffPerf from '#/containers/StaffPerf'
import ScanDetails from '#/containers/ScanDetails'
import { Router, Route } from 'dva/router'

export default ({history}) => (
  <Router history={history}>
    <Route exact path="/AssetBrowser" component={AssetBrowser} />
    <Route exact path="/AssetPerf" component={AssetPerf} />
    <Route exact path="/StaffPerf" component={StaffPerf} />
    <Route exact path="/ScanDetails" component={ScanDetails} />
  </Router>
)
