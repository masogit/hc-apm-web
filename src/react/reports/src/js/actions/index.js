
export const FA_ACT_UPDATE_PARAM = 'failureAnalysis/update/param/'
export function FaParamUpdate(type, data){
  return {
    type: FA_ACT_UPDATE_PARAM + type,
    data: data
  }
}

export const FA_ACT_PAGE_CHANGE = 'failureAnalysis/page/change'

export function FaPageChange(leftOrRight, value) {
  return {
    type: FA_ACT_PAGE_CHANGE,
    data: {
      type: leftOrRight,
      value: value
    }
  }
}

export const ACT_UPDATE_PARAM = 'update/param/'
export function ParamUpdate(type, data){
  return {
    type: ACT_UPDATE_PARAM + type,
    data: data
  }
}

export const ACT_UPDATE_META = 'update/meta/'
export function MetaUpdate(type = '', data) {
  return {
    type: ACT_UPDATE_META + type,
    data: data
  }
}

export const ACT_PAGE_CHANGE = 'page/change'

export function PageChange(leftOrRight, value) {
  return {
    type: ACT_PAGE_CHANGE,
    data: {
      type: leftOrRight,
      value: value
    }
  }
}
