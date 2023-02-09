<template>

  <div class="login-container">
    <el-form :model="loginForm" :rules="rules"  ref="loginFrom" laber-position="left" label-width="0px" class="demo-loginFrom login-page">
    <h3 class="login_title">
        系统登录
    </h3>
  <el-form-item prop="no">
    <el-input type="text" v-model="loginForm.no"  placeholder="用户名" prefix-icon="el-icon-user-solid"></el-input>
  </el-form-item>

  <el-form-item prop="password">
    <el-input type="password" v-model="loginForm.password"  placeholder="密码" prefix-icon="el-icon-lock"></el-input>
  </el-form-item>


  <el-form-item style="width:100%;">
    <el-button type="primary" style="width:100%;border:none" @click="logining">立即登录</el-button>
  </el-form-item>
  </el-form>
  </div>
</template>

<script>


export default {
  name: 'Login',

data() {
      return {
        loginForm: {
          no: 'sa',
          password: '123'
        },

        rules:{//规则验证
          no:[{required:true,message:'请输入用户名',trigger:'blur'}],
          password:[{required:true,message:'请输入密码',trigger:'blur'}]
        }
      }
    },
    methods: {
      logining() {

        this.$refs.loginFrom.validate((valid)=>{//数据校验
          if(valid){
            //去后台验证用户名密码
            this.$axios.post(this.$httpUrl+'/user/login',this.loginForm).then(res=>res.data).then(res=>{
              //console.log(res)
              if(res.code===200){
                //存储登录用户
                sessionStorage.setItem("CurUser",JSON.stringify(res.data.user))
                //console.log(res.data.menuList)
                this.$store.commit("setMenu",res.data.menuList)
                //console.log(this.$store.state.menu)
                //跳转到主页
                this.$router.replace('Home');

              }else{
                this.$message({
                  message:'密码或用户错误',
                  type:'error'
                })
              }
            });

          }
        })


      }

    }
}
</script>
<style>

.login-container{
    border-radius: 15px;
    background-clip:padding-box ;
    margin: 90px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;

}
.login_title{
    margin: 0px auto 40px auto;
    text-align: center;
    color: #505458;
}
</style>