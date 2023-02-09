import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',

    component:()=>import('../components/login/Login')
  },
  {
    path:'/Index',
    name:'index',
    component:()=>import('../components/index/Index'),
    children:[
            {
              path:'/Home',
              name:'home',
              meta:{
                title:'个人中心'
              },
              component:()=>import('../components/index/Home')
            },
            {
                path:'/Admin',
                name:'admin',
                meta:{
                    title:'管理员管理'
                },
                component:()=>import('../components/admin/AdminManage.vue')
            },
            {
                path:'/User',
                name:'user',
                meta:{
                    title:'用户管理'
                },
                component:()=>import('../components/user/UserManage.vue')
            },
            {
                path:'/Storage',
                name:'storage',
                meta:{
                    title:'仓库管理'
                },
                component:()=>import('../components/storage/StorageManage')
            },
            {
                path:'/Goodstype',
                name:'goodstype',
                meta:{
                    title:'商品类型管理'
                },
                component:()=>import('../components/goodstype/GoodstypeManage')
            },
            {
                path:'/Goods',
                name:'goods',
                meta:{
                    title:'商品管理'
                },
                component:()=>import('../components/goods/GoodsManage')
            },
            {
                path:'/Record',
                name:'record',
                meta:{
                    title:'记录'
                },
                component:()=>import('../components/record/RecordManage')
            },
    ]
  }

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})
const VueRouterPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (to) {
  return VueRouterPush.call(this, to).catch(err => err)
}
export default router
