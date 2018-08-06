import { error } from '#/utils/logger'
import produce from 'immer'
import { Nurse as API } from '#/services'


const REPORT_NAME = 'Nurse'

export default {

  namespace: `${REPORT_NAME}`,

  state: {
    assets: [],
  },

  reducers: {
    'set/assets'(state, { payload }) {
      return produce(state, state => {
        state.assets = payload
      })
    }
  },

  effects: {
    *'get/all'({ }, { put }) {
      yield put({ type: 'get/assets' })
    },
    *'get/assets'({ }, { put, call, select }) {
      let dept = yield select(state => state.user.info.orgId)
      let { data } = yield call(API.getAssets, dept)
      yield put({ type: 'set/assets', payload: data })
    }
  },

}
