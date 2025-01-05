import { createStore } from 'vuex'

export default createStore({
  state: {
    userInfo: null
  },
  mutations: {
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
    },
    CLEAR_USER_INFO(state) {
      state.userInfo = null
    }
  },
  actions: {
    login({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo)
    },
    logout({ commit }) {
      commit('CLEAR_USER_INFO')
    }
  }
}) 