import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    menu:[]
  },
  getters: {
    getMenu(){
        return this.state.menu
    }
  },
  mutations: {
    setMenu(state,menuList){
      state.menu=menuList
    }
  },
  actions: {
  },
  modules: {

  }
})
