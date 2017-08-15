/* @flow */
import { IsHead, getOrgId } from '#/utils'

const isHead = IsHead()
const orgId = getOrgId()

export default {
  namespace: 'user',
  state: {
    info: {
      isHead,
      orgId
    }
  },
  reducers: {
    ['info/set'] (state, { payload }) {
      return {
        ...state,
        info: payload
      }
    }
  }
}
