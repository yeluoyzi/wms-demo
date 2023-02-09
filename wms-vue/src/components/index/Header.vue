<template>
  <div style="display: flex;line-height: 60px;">

    <div style="margin-top: 8px;cursor: pointer;">
      <i class="el-icon-s-fold" style="font-size: 18px;" @click="collapse"></i>
    </div>

    <div style="flex: 1;text-align: center;font-size: 30px;">
      欢迎来到仓库管理系统
    </div>

    <div style="cursor: pointer; font-size: 15px">
    <span>{{user.name}}</span>
  <el-dropdown>
    <i class="el-icon-arrow-down" style="margin-right: 5px"></i>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item @click.native="toUser">个人中心</el-dropdown-item>
      <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
    </div>

  </div>
</template>

<script>
export default {
  name: "Header",
  data(){
    return{
      user:JSON.parse(sessionStorage.getItem('CurUser'))
    }

  },
  methods:{
    toUser(){
      this.$router.push('/Home')
    },
    logout(){
      this.$router.push('/')
      //清空会话
      sessionStorage.clear()
    },
    collapse(){
      this.$emit('doColl')//调用父组件事件
    }
  }
}
</script>

<style scoped>

</style>