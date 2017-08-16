import { Assets } from './assetBrowser'
import { Profit, Forecast, ForecastPUT, ForecastRate } from './assetPerf'
import { StaffPerf } from './staffPerf'
import { URLs } from '../index.js'

const TAG = '[Mock]: '

let info = msg => console.info(TAG + msg)

export default function (mock) {

  mock.onGet(URLs.AssetBrowser_Assets).reply(function (config) {
    info(config.url)
    return [200, Assets]
  })

  mock.onGet(URLs.AssetPerf_Profit).reply(function (config) {
    info(config.url)
    return [200, Profit]
  })

  mock.onGet(URLs.AssetPerf_Forecast).reply(function (config) {
    info(config.url)
    return [200, Forecast]
  })

  mock.onPut(URLs.AssetPerf_Forecast).reply(function (config) {
    info(config.url)
    return [200, ForecastPUT]
  })

  mock.onGet(URLs.AssetPerf_ForecastRate).reply(function (config) {
    info(config.url)
    return [200, ForecastRate]
  })

  mock.onGet(URLs.StaffPerf_Staff).reply(function (config) {
    info(config.url)
    return [200, StaffPerf]
  })

}
