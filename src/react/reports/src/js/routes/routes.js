import React from 'react'
import { Route, Switch } from 'react-router-dom'

import FailureAnalysis from 'containers/FailureAnalysis/App'
import ProcessAnalysis from 'containers/ProcessAnalysis/App'

export default (
  <Switch>
    <Route exact path="/" component={FailureAnalysis}/>
    <Route path="/failureAnalysis" component={FailureAnalysis}/>
    <Route path="/processAnalysis" component={ProcessAnalysis}/>
  </Switch>
)