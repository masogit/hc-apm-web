import axios from 'axios'
import { API_HOST } from '#/constants'

export default {

  namespace: 'depts',

  state: [],

  subscriptions: {
    setup({history, dispatch}) {
      return history.listen(({ query, pathname }) => {
        dispatch({
          type: 'get'
        })
      })
    }
  },

  effects: {
    *['get']({ payload }, { put, call }) {
      try {
        const { data } = yield call(axios, API_HOST + '/org/all')
        yield put({
          type: 'get/succeeded',
          payload: data.orgInfos
        })
      } catch(err) {
        console.error(err)
      }
    }
  },

  reducers: {
    ['get/succeeded'](state, { payload }) {
      return payload
    }
  }

}
