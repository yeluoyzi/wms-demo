<template>
  <div>
    <div style="margin-bottom: 10px;">
      <el-input v-model="selname" placeholder="请输入名字" suffix-icon="el-icon-search" style="width: 200px" @keyup.enter.native="loadGet" e> </el-input>

      <el-button type="primary" style="margin-left: 5px;" @click="loadGet">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" style="margin-left: 5px;" @click="add" v-if="user.roleId!=2">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{background:'#f3efef',color:'#726d6d'}"
              border>
      <el-table-column prop="id" label="ID" width="80">
      </el-table-column>
      <el-table-column prop="no" label="账号" width="180">
      </el-table-column>
      <el-table-column prop="name" label="姓名" width="180">
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="100">
      </el-table-column>
      <el-table-column prop="sex" label="性别" width="100">
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.sex === 1 ? 'primary' : 'success'"
              disable-transitions>{{scope.row.sex === 1 ? '男' : '女'}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleId" label="职务" width="120">
        <template slot-scope="scope">
          <el-tag
              :type="scope.row.roleId === 0 ? 'danger' : (scope.row.roleId === 1 ? 'primary' : 'success')"
              disable-transitions>{{scope.row.roleId === 0 ? '超级管理员' : (scope.row.roleId === 1 ? '管理员' : '用户')}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系方式" width="180">
      </el-table-column>
      <el-table-column prop="operate" label="操作" >
        <template slot-scope="scope">
          <el-button size="mini" type="success" @click="edit(scope.row)" v-if="user.roleId!=2">编辑</el-button>

          <el-popconfirm
              title="确定删除吗？" style="margin-left: 5px" @confirm="de(scope.row.id)">
            <el-button slot="reference" size="mini" type="danger" v-if="user.roleId!=2">删除</el-button>
          </el-popconfirm>

        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5,10,20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

    <el-dialog
        title="提示"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="账号" prop="no">
          <el-col :span="20">
            <el-input v-model="form.no"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="名字" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-col :span="20">
            <el-input v-model="form.password"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-col :span="20">
            <el-input v-model="form.age"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-col :span="20">
            <el-input v-model="form.phone"></el-input>
          </el-col>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="centerDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: "UserManage",
  data() {
    let checkAge =(rule,value,callback)=>{//
      if(value>120){
        callback(new Error('年龄过大!'))
      }else {
        callback()
      }
    }
    let checkId =(rule,value,callback)=>{//账号查重
      if(this.form.id){
        return callback();
      }else {
        this.$axios.get(this.$httpUrl + "/user/findByNo?no=" + this.form.no).then(res => res.data).then(res => {
          if (res.code != 200) {
            callback()
          } else {
            callback(new Error('账号已经存在'));
          }
        })
      }
    }
    return {
      user : JSON.parse(sessionStorage.getItem('CurUser')),
      tableData: [],//返回数据显示
      pageNum:1,
      pageSize:10,
      total:0,
      selname:'',
      centerDialogVisible:false,
      form:{
        id:'',
        no:'',//账户
        name:'',
        password:'',
        age:'',
        phone:'',
        sex:'1',//默认为男
        roleId:'2'//默认为用户
      },
      rules:{

        no: [
          {required: true, message: '请输入账号', trigger: 'blur'},
          {min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur'},
          {validator:checkId,trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请输入名字', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 8, message: '长度在 3 到 8 个字符', trigger: 'blur'}
        ],
        age: [
          {required: true, message: '请输入年龄', trigger: 'blur'},
          {min: 1, max: 3, message: '长度在 1 到 3 个位', trigger: 'blur'},
          {pattern: /^([1-9][0-9]*){1,3}$/,message: '年龄必须为正整数字',trigger: "blur"},
          {validator:checkAge,trigger: 'blur'}
        ],
        phone: [
          {required: true,message: "手机号不能为空",trigger: "blur"},
          {pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur"}
        ]
      },



    }
  },
  methods:{
    handleSizeChange(val) {
      //console.log(`每页 ${val} 条`);
      this.pageNum=1
      this.pageSize=val
      this.loadGet()
    },
    handleCurrentChange(val) {
      //console.log(`当前页: ${val}`);
      this.pageNum=val
      this.loadGet()
    },
    resetParam(){//重置搜索
      this.selname=''
      this.pageNum=1
    },
    add(){//新增用户
      this.centerDialogVisible=true
      this.$nextTick(()=>{this.resetForm()})//异步处理,先打开后重置数据
      this.form.id=''
      console.log(this.form)
    },
    resetForm(){//重置表单
      this.$refs.form.resetFields()
    },
    edit(row){//编辑
      //打开空表单然后赋值
      this.centerDialogVisible=true
      this.$nextTick(()=> {
        this.form.id = row.id
        this.form.no = row.no
        this.form.name = row.name
        this.form.password = ''
        this.form.age = row.age +''
        this.form.sex = row.sex +''
        this.form.phone = row.phone
        this.form.roleId = row.roleId
      })
      console.log(this.form)
    },
    save(){//提交表单
      this.$refs.form.validate((valid)=>{
        if (valid){
          if(this.form.id){
            console.log('u')
            this.doUpdate()
          }else{
            console.log('s')
            this.doSave()
          }

        }else{
          return false;
        }
      })
    },
    doSave(){//新增用户
      this.$axios.post(this.$httpUrl+'/user/save',this.form).then(res=>res.data).then(res=>{
        //console.log(res)
        if(res.code===200){

          this.$message({
            message:'操作成功',
            type:'success'
          })
          this.centerDialogVisible=false
          this.loadGet()
          this.resetForm()
        }else{
          this.$message({
            message:'操作失败',
            type:'error'
          })
        }

      })
    },
    doUpdate(){//修改
      this.$axios.post(this.$httpUrl+'/user/update',this.form).then(res=>res.data).then(res=>{
        //console.log(res)
        if(res.code===200){
          this.$message({
            message:'修改成功',
            type:'success'
          })
          this.centerDialogVisible=false
          this.loadGet()
          this.resetForm()
        }else{
          this.$message({
            message:'操作失败',
            type:'error'
          })
        }

      })
    },
    de(id){//删除
      //console.log(id)
      this.$axios.get(this.$httpUrl+'/user/del?id='+id).then(res=>res.data).then(res=>{
        //console.log(res)
        if(res.code===200){
          this.$message({
            message:'删除成功',
            type:'success'
          })
          this.loadGet()
        }else{
          this.$message({
            message:'操作失败',
            type:'error'
          })
        }

      })
    },
    loadGet(){//获取用户列表
      this.$axios.post(this.$httpUrl+'/user/listPageC1',
          {
            pageSize:this.pageSize,
            pageNum:this.pageNum,
            param:{
              name:this.selname,
              roleId:'2'
            }}
      ).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code===200){
          this.total=res.total//赋值分页
          this.tableData=res.data//赋值数据
        }else{
          alert("获取数据失败!")
        }

      })
    }

  },
  beforeMount() {
    this.loadGet()
  }
}
</script>

<style scoped>

</style>