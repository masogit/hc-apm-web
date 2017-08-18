import { cloneDeep } from 'lodash-es'
import moment from 'moment'
import { PA_ACT_UPDATE_PARAM } from 'actions'

const Current = moment()
const YearStart = moment(new Date().getFullYear(), 'YYYY')
const initParameter = {
  filterBy: {
    'assettype': 'all_asset_type',
    'dept': 'all_dept',
  },
  period: {
    from: YearStart,
    to: Current
  },
  dataType: 'arrival_time',
  display: 'display_assettype', // type | supplier | name
  pagination: {
    left: {
      skip: 0,
      top: 6,
      total: 0,
    },
    right: {
      skip: 0,
      top: 16,
      total: 0
    }
  },
  distributionEttr: [0, 24 * 3600, 3 * 24 * 3600 , 7 * 24 * 3600], // 1, 3, 7 days
  distributionResponse: [0, 15 * 60, 30 * 60 , 3600], // 15, 30, 60 mins
  distributionArrival: [0, 12 * 3600, 24 * 3600 , 48 * 3600], // 12, 24, 48 hours
}

const reducers = {
  ['processAnalysis/update/param/filter'](next, data) {
    next.filterBy[data.type] = data.data.key
    return next
  },

  ['processAnalysis/update/param/period'](next, data) {
    next.period = {
      from: data[0],
      to: data[1]
    }
    return next
  },

  ['processAnalysis/update/param/datatype'](next, data) {
    next.dataType = data
    return next
  },

  ['processAnalysis/update/param/showlastyear'](next, data) {
    next.showLastYear = data
    return next
  },

  ['processAnalysis/update/param/display'](next, data) {
    next.display = data
    return next
  },

  ['processAnalysis/update/param/distributionEttr'](next, data) {
    next.distributionEttr = data
    return next
  },
  ['processAnalysis/update/param/distributionResponse'](next, data) {
    next.distributionResponse = data
    return next
  },
  ['processAnalysis/update/param/distributionArrival'](next, data) {
    next.distributionArrival = data
    return next
  },

  ['processAnalysis/update/param/pagination/sync'](next, data) {
    let pag = next.pagination[data.type]
    Object.assign(pag, data.value)
    return next
  }
}

export default (state = initParameter, action) => {

  if (!action.type.startsWith(PA_ACT_UPDATE_PARAM)) {
    return state
  }
  return reducers[action.type](cloneDeep(state), action.data)

}