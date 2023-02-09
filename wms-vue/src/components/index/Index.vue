<template>
  <el-container style="height: 100%; border: 1px solid #eee">
    <el-aside :width="aside_width" style=" background-color: rgb(238, 241, 246)">
      <Aside :iscollapse="iscollapse"> </Aside>
    </el-aside>

    <el-container style="height: 100%">
      <el-header style="text-align: right; font-size: 12px ;height: 100%">
        <Header @doColl="doColl"> </Header>
      </el-header>

      <el-main style="height: 100%">
        <router-view> </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}
.el-main{
  padding: 5px;
}
.el-aside {
  color: #333;
}
</style>

<script>
import Aside from "../index/Aside"
import Header from "../index/Header";
export default {
  name:"Index",
  components: {Header, Aside},
  data(){
    return{
      iscollapse:true,//默认折叠
      aside_width:'64px'//aside宽度
    }
  },
  methods:{
    doColl(){
      //console.log("111")
      this.iscollapse=!this.iscollapse//改变折叠状态
      if(!this.iscollapse){//展开
        this.aside_width='200px'
      }else{//折叠
        this.aside_width='64px'
      }
    },
    created(){
      this.$router.push("/Home")
    }
  }

};
</script>