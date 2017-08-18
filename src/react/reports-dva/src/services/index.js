import './interceptors'
import axios from 'axios'
import { load } from './mocks/helper'

const URLs = {

  AllDepartments: '/api/org/all',
  AllAssetTypes: '/api/msg?type=assetGroup',

  AssetBrowser_Assets: '/api/assets',

  AssetPerf_Profit: '/api/profit',
  AssetPerf_Forecast: '/api/profit/forecast',
  AssetPerf_ForecastRate: '/api/profit/forecastrate',

  StaffPerf_Staff: '/api/staff',

  ScanDetails_Detail: '/api/scan/detail',
  ScanDetails_Brief: '/api/scan/brief',

  MaintenanceCost_Ma: '/api/ma', // wtf is 'ma' ???
  MaintenanceCost_Asset: '/api/ma/asset/*',

  MaintenanceStats_Pm: '/api/pm', // wtf is 'pm' ???

}

if (process.env.NODE_ENV !== 'production') {
  Object.keys(URLs).forEach(k => URLs[k] = '/geapm' + URLs[k])
}

const UseMock = true
if (__USE_MOCK__ && UseMock) {
  load('./sample')
}

const AssetBrowser = {

  getAssets(params) {
    return axios.get(URLs.AssetBrowser_Assets, { params })
  }

}

export { URLs, AssetBrowser }