import React from 'react'
import AssetBrowser from '#/containers/AssetBrowser'
import { Router, Route } from 'dva/router'

export default ({history}) => (
  <Router history={history}>
    <Route exact path="/" component={AssetBrowser} />
  </Router>
)
