import { routerReducer as routing } from 'react-router-redux'
import context from 'reducers/context'
import failureAnalysis from 'reducers/failureAnalysis'
import processAnalysis from 'reducers/processAnalysis'
import meta from 'reducers/meta'

export default {
  routing,
  context,
  failureAnalysis,
  processAnalysis,
  meta
}
