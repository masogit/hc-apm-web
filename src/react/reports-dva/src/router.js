import React from 'react'
import AssetBrowser from '#/containers/AssetBrowser'
import AssetPerf from '#/containers/AssetPerf'
import { Router, Route } from 'dva/router'

export default ({history}) => (
  <Router history={history}>
    <Route exact path="/" component={AssetBrowser} />
    <Route exact path="/AssetBrowser" component={AssetBrowser} />
    <Route exact path="/AssetPerf" component={AssetPerf} />
  </Router>
)
