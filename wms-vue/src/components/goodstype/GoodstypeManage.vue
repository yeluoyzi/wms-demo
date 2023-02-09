<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input v-model="selname" placeholder="请输入分类名" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadGet"></el-input>
      <el-button type="primary" style="margin-left: 5px;" @click="loadGet">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>

      <el-button type="primary" style="margin-left: 5px;" @click="add" v-if="user.roleId!=2">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
              border
    >
      <el-table-column prop="id" label="ID" width="60">
      </el-table-column>
      <el-table-column prop="name" label="分类名" width="180">
      </el-table-column>
      <el-table-column prop="remark" label="备注" width="280">
      </el-table-column>
      <el-table-column prop="operate" label="操作">
        <template slot-scope="scope">
          <el-button size="small" type="success" @click="edit(scope.row)" v-if="user.roleId!=2">编辑</el-button>
          <el-popconfirm
              title="确定删除吗？"
              @confirm="de(scope.row.id)"
              style="margin-left: 5px;"
          >
            <el-button slot="reference" size="small" type="danger" v-if="user.roleId==0">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20]"
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
        <el-form-item label="分类名" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-col :span="20">
            <el-input type="textarea" v-model="form.remark"></el-input>
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
  name: "GoodstyleManage",
  data() {

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
        name:'',
        remark:''

      },
      rules:{
        name: [
          {required: true, message: '请输入分类名', trigger: 'blur'}
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
    add(){//新增仓库
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
        this.form.name = row.name
        this.form.remark = row.remark
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
    doSave(){//新增仓库
      this.$axios.post(this.$httpUrl+'/goodstype/save',this.form).then(res=>res.data).then(res=>{
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
      this.$axios.post(this.$httpUrl+'/goodstype/update',this.form).then(res=>res.data).then(res=>{
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
      this.$axios.get(this.$httpUrl+'/goodstype/del?id='+id).then(res=>res.data).then(res=>{
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
      this.$axios.post(this.$httpUrl+'/goodstype/listPageC1',
          {
            pageSize:this.pageSize,
            pageNum:this.pageNum,
            param:{
              name:this.selname
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