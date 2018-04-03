import { ranges } from '#/constants'
import { IsHead, getOrgId } from '#/utils'

const isHead = IsHead()
const orgId = getOrgId()

export default {
  namespace: 'ScanDetails_filters',
  state: {
    range: {
      from: ranges.oneYear.start.format('YYYY-MM-DD'),
      to: ranges.oneYear.end.format('YYYY-MM-DD')
    },
    dept: orgId,
    hospital: undefined,
    cursor: {}
  },
  effects: {
    *['field/set'](_, { select, put }) {
      yield put({type: 'briefs/data/get'})
      yield put({type: 'assets/page/reset'})
      yield put({type: 'assets/data/get'})
      yield put({type: 'steps/page/reset'})
      yield put({type: 'steps/data/get'})
    },
    *['cursor/toggle']({ payload: { key, value } }, { select, put }){
      const cursor = yield select(state => state.filters.cursor)
      if (key === 'type') {
        yield put({type: 'assets/page/reset'})
        yield put({type: 'assets/data/get'})
        yield put({type: 'steps/page/reset'})
        yield put({type: 'steps/data/get'})
      } else if (key === 'part') {
        yield put({type: 'assets/page/reset'})
        yield put({type: 'assets/data/get'})
        if (cursor['type'] || cursor['asset']) {
          yield put({type: 'steps/page/reset'})
          yield put({type: 'steps/data/get'})
        }
      } else if (key === 'asset') {
        yield put({type: 'steps/page/reset'})
        yield put({type: 'steps/data/get'})
      }
    }
  },
  reducers: {
    ['field/set'](state, { payload: { key, value } }) {
      return {
        ...state,
        [key]: value
      }
    },
    ['cursor/toggle'](state, { payload: { key, value }}) {
      const cursor = state.cursor
      if (key === 'type') delete cursor['asset']
      if (cursor[key] === value) {
        delete cursor[key]
        return {
          ...state,
          cursor: {...cursor}
        }
      } else {
        return {
          ...state,
          cursor: {
            ...cursor,
            [key]: value
          }
        }
      }
    }
  }
}
