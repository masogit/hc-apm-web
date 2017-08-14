import { cloneDeep } from 'lodash-es'
import moment from 'moment'
import { FA_ACT_UPDATE_PARAM } from 'actions'

const Current = moment()
const YearStart = moment(new Date().getFullYear(), 'YYYY')

const initParameter = {

  filterBy: {
    ['assettype']: 'all_asset_type',
    ['dept']: 'all_dept',
    ['supplier']: 'all_supplier', // currently no dropdown on UI
  },
  period: {
    from: YearStart,
    to: Current
  },
  dataType: 'operation_rate',
  showLastYear: false,
  display: 'display_asset_type', // type | brand | name
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
  }
}

const reducers = {
  ['failureAnalysis/update/param/filter'](next, data) {
    next.filterBy[data.type] = data.data.key
    return next
  },

  ['failureAnalysis/update/param/period'](next, data) {
    next.period = {
      from: data[0],
      to: data[1]
    }
    return next
  },

  ['failureAnalysis/update/param/datatype'](next, data) {
    next.dataType = data
    return next
  },

  ['failureAnalysis/update/param/showlastyear'](next, data) {
    next.showLastYear = data
    return next
  },

  ['failureAnalysis/update/param/display'](next, data) {
    next.display = data
    return next
  },

  ['failureAnalysis/update/param/pagination/sync'](next, data) {
    let pag = next.pagination[data.type]
    Object.assign(pag, data.value)
    return next
  }

}

export default (state = initParameter, action) => {

  if (!action.type.startsWith(FA_ACT_UPDATE_PARAM)) {
    return state
  }
  return reducers[action.type](cloneDeep(state), action.data)

}