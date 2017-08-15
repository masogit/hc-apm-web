import { Assets } from './assetBrowser'
import { URLs } from '../index.js'

const TAG = '[Mock]: '

let info = msg => console.info(TAG + msg)

export default function (mock) {

  mock.onGet(URLs.AssetBrowser_Assets).reply(function (config) {
    info(config.url)
    return [200, Assets]
  })

}
