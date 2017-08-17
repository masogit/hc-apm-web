import React from 'react'
import AssetBrowser from '#/containers/AssetBrowser'
import AssetPerf from '#/containers/AssetPerf'
import StaffPerf from '#/containers/StaffPerf'
import ScanDetails from '#/containers/ScanDetails'
import MaintenanceCost from '#/containers/MaintenanceCost'
import MaintenanceStats from '#/containers/MaintenanceStats'
import DecisionMaking from '#/containers/DecisionMaking'
import { Router, Route } from 'dva/router'

export default ({history}) => (
  <Router history={history}>
    <Route exact path="/AssetBrowser" component={AssetBrowser} />
    <Route exact path="/AssetPerf" component={AssetPerf} />
    <Route exact path="/DecisionMaking" component={DecisionMaking} />
    <Route exact path="/StaffPerf" component={StaffPerf} />
    <Route exact path="/ScanDetails" component={ScanDetails} />
    <Route exact path="/MaintenanceCost" component={MaintenanceCost} />
    <Route exact path="/MaintenanceStats" component={MaintenanceStats} />
  </Router>
)
