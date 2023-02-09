<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input v-model="selname" placeholder="请输入操作人" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadGet"></el-input>
      <el-button type="primary" style="margin-left: 5px;" @click="loadGet">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
              border
    >
      <el-table-column prop="id" label="ID" width="60">
      </el-table-column>
      <el-table-column prop="goodsid" label="物品ID" width="120">
      </el-table-column>
      <el-table-column prop="goodsname" label="物品名" width="120">
      </el-table-column>
      <el-table-column prop="userid" label="操作人ID" width="120">
      </el-table-column>
      <el-table-column prop="adminname" label="操作人" width="120">
      </el-table-column>
      <el-table-column prop="count" label="数量" width="120">
      </el-table-column>
      <el-table-column prop="type" label="出/入库" width="120">
      </el-table-column>
      <el-table-column prop="createtime" label="时间" width="120">
      </el-table-column>
      <el-table-column prop="remark" label="备注" >
      </el-table-column>
      <el-table-column prop="operate" label="操作">
        <template slot-scope="scope">
          <el-button size="small" type="success" @click="edit(scope.row)" v-if="user.roleId==0">编辑</el-button>
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
        title="数据维护"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>

      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="物品id" prop="goodsid">
          <el-col :span="20">
            <el-input v-model="form.goodsid" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="物品名" prop="goodsname">
          <el-col :span="20">
            <el-input v-model="form.goodsname" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="操作人ID" prop="userid">
          <el-col :span="20">
            <el-input v-model="form.userid" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="操作人" prop="adminname">
          <el-col :span="20">
            <el-input v-model="form.adminname" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-col :span="20">
            <el-input v-model="form.count" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="出/入库" prop="type">
          <el-col :span="20">
            <el-input v-model="form.type" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="时间" prop="createtime">
          <el-col :span="20">
            <el-input v-model="form.createtime" readonly></el-input>
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
  name: "StorageManage",
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
        goodsid:'',
        goodsname:'',
        userid:'',
        adminname:'',
        count:'',
        type:'',
        createtime:'',
        remark:''

      },
      rules:{

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

    resetForm(){//重置表单
      this.$refs.form.resetFields()
    },
    edit(row){//编辑
      //打开空表单然后赋值
      this.centerDialogVisible=true
      this.$nextTick(()=> {
        this.form.id = row.id
        this.form.goodsid=row.id
        this.form.goodsname=row.goodsname
        this.form.userid=row.userid
        this.form.adminname=row.adminname
        this.form.type=row.type
        this.form.count=row.count
        this.form.createtime=row.createtime
        this.form.remark=row.remark
      })
      console.log(this.form)
    },
    save(){//提交表单
      this.$refs.form.validate((valid)=>{
        if (valid){
          this.doUpdate()

        }else{
          return false;
        }
      })
    },
    doUpdate(){//修改记录
      this.$axios.post(this.$httpUrl+'/record/update',this.form).then(res=>res.data).then(res=>{
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
      this.$axios.get(this.$httpUrl+'/record/del?id='+id).then(res=>res.data).then(res=>{
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
    loadGet(){//获取记录列表
      this.$axios.post(this.$httpUrl+'/record/listPageC1',
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