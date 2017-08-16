import { Assets } from './assetBrowser'
import { Profit, Forecast, ForecastPUT, ForecastRate } from './assetPerf'
import { StaffPerf } from './staffPerf'
import { DetailByAsset, DetailByStep, Brief } from './scanDetails'
import { Acyman20, TypeAcyman10, TypeAcyman, TypeMtpm, AssetAcyman, AssetMtpm } from './maintenanceCost'
import { Orgs, AssetTypes } from './common'
import { URLs } from '../index.js'

const TAG = '[Mock]: '

let info = msg => console.info(TAG + msg)

export default function (mock) {

  mock.onGet(URLs.AllAssetTypes).reply(function (config) {
    info(config.url)
    return [200, AssetTypes]
  })

  mock.onGet(URLs.AllDepartments).reply(function (config) {
    info(config.url)
    return [200, Orgs]
  })

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

  mock.onGet(URLs.ScanDetails_Brief).reply(function (config) {
    info(config.url)
    return [200, Brief]
  })

  mock.onGet(URLs.ScanDetails_Detail).reply(function ({ params, url }) {
    info(url)
    if (params.groupby === 'asset') {
      return [200, DetailByAsset]
    } else {
      return [200, DetailByStep]
    }
  })

  mock.onGet(URLs.MaintenanceCost_Ma).reply(function ({ params, url }) {
    info(url)
    let resp
    if (params.limit == 20) {
      resp = Acyman20
    } else if (params.limit == 10 && params.groupby === 'type') {
      resp = TypeAcyman10
    } else if (params.rltgrp === 'mtpm') {
      resp = TypeMtpm
    } else if (params.rltgrp === 'acyman' && params.groupby === 'type') {
      resp = TypeAcyman
    }

    return [200, resp]
  })

  mock.onGet(RegExp(URLs.MaintenanceCost_Asset)).reply(function ({ params, url }) {
    info(url)
    if (params.rltgrp === 'acyman') {
      return [200, AssetAcyman]
    } else {
      return [200, AssetMtpm]
    }
  })
}
