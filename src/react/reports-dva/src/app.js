import React from 'react'
import ReactDOM from 'react-dom'
import dva from 'dva/mobile'
import { AppContainer } from 'react-hot-loader'
import { useRouterHistory } from 'dva/router'
import createHashHistory from 'history/lib/createHashHistory'
import './app.css'

import router from '#/router'

import assetBrowser from '#/models/assetBrowser'
import profit from '#/models/AssetPerf/profit'
import config from '#/models/AssetPerf/config'
import user from '#/models/AssetPerf/user'
import filters from '#/models/AssetPerf/filters'
import overview from '#/models/AssetPerf/overview'

const historyEngine = useRouterHistory(createHashHistory)({
  queryKey: false,
  basename: '/'
})
const app = dva({
  history: historyEngine,
  onError: e => {
    if (process.env.NODE_ENV === 'production') return
    console.log(e)
  }
})

app.model(assetBrowser)
app.model(profit)
app.model(config)
app.model(user)
app.model(filters)
app.model(overview)
app.router(router)

function render(router) {
  const App = router ? app._getProvider(router) : app.start()
  ReactDOM.render((
    <AppContainer>
      <App />
    </AppContainer>
  ), document.getElementById('root'));
}

app._plugin.apply('onHmr')(render);

render()

if (module.hot) {
  module.hot.accept('#/router', () => { render(router) })
}






