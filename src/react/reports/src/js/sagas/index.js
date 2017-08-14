import { call, put, takeLatest, all } from 'redux-saga/effects'
import Services from 'services'
import cache from 'utils/cache'
import { error } from 'utils/logger'
import FailureAnalysisSagas from './FailureAnalysis'
import ProcessAnalysisSagas from './ProcessAnalysis'

const API = Services.api

function* fetchMeta() {
  try {
    let [ depts, types ] = yield all([call(API.getDepartments), call(API.getAssetTypes)])
    cache.set('departments', depts)
    cache.set('assettypes', types)
    yield put({ type: 'update/meta/departments', data: depts })
    yield put({ type: 'update/meta/assettypes', data: types })
  } catch(e) {
    error(e)
  }
}

function* metaSaga() {
  yield takeLatest('update/meta/', fetchMeta)
}

export default function* sagas() {
  yield all([
    metaSaga(),
    ...FailureAnalysisSagas,
    ...ProcessAnalysisSagas
  ])
}