
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

export const PA_ACT_UPDATE_PARAM = 'processAnalysis/update/param/'
export function PaParamUpdate(type, data){
  return {
    type: PA_ACT_UPDATE_PARAM + type,
    data: data
  }
}

export const PA_ACT_PAGE_CHANGE = 'processAnalysis/page/change'
export function PaPageChange(leftOrRight, value) {
  return {
    type: PA_ACT_PAGE_CHANGE,
    data: {
      type: leftOrRight,
      value: value
    }
  }
}

export const ACT_UPDATE_META = 'update/meta/'
export function MetaUpdate(type = '', data) {
  return {
    type: ACT_UPDATE_META + type,
    data: data
  }
}