<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input v-model="selname" placeholder="请输物品名" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadGet"></el-input>
      <el-select v-model="storage" placeholder="请选择仓库" style="margin-left: 5px;">
        <el-option
            v-for="item in storageData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
        </el-option>
      </el-select>
      <el-select v-model="goodstype" placeholder="请选择分类" style="margin-left: 5px;">
        <el-option
            v-for="item in goodstypeData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
        </el-option>
      </el-select>

      <el-button type="primary" style="margin-left: 5px;" @click="loadGet">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" style="margin-left: 5px;" @click="add" v-if="InUser.roleId!=2">新增</el-button>

      <el-button type="primary" style="margin-left: 20px;" @click="inGood" v-if="InUser.roleId!=2">入库</el-button>
      <el-button type="primary" style="margin-left: 5px;" @click="outGood" v-if="InUser.roleId!=2">出库</el-button>

    </div>
    <el-table :data="tableData"
              :header-cell-style="{ background: '#f2f5fc', color: '#555555' }"
              border
              highlight-current-row
              @current-change="selectCurrentChange"
    >
      <el-table-column prop="id" label="ID" width="60">
      </el-table-column>
      <el-table-column prop="name" label="物品名" width="180">
      </el-table-column>
      <el-table-column prop="storage" label="仓库" width="180" :formatter="formatterStorage">
      </el-table-column>
      <el-table-column prop="goodstype" label="分类" width="180" :formatter="formatterGoodsType">
      </el-table-column>
      <el-table-column prop="count" label="数量" width="180">
      </el-table-column>
      <el-table-column prop="remark" label="备注" width="180">
      </el-table-column>
      <el-table-column prop="operate" label="操作">
        <template slot-scope="scope">
          <el-button size="small" type="success" @click="edit(scope.row)" v-if="InUser.roleId!=2">编辑</el-button>
          <el-popconfirm
              title="确定删除吗？"
              @confirm="de(scope.row.id)"
              style="margin-left: 5px;"
          >
            <el-button slot="reference" size="small" type="danger" v-if="InUser.roleId!=2">删除</el-button>
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
        title="物品维护"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>

      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="物品名" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="仓库" prop="storage">
          <el-col :span="20">
            <el-select v-model="form.storage" placeholder="请选择仓库" style="margin-left: 5px;">
              <el-option
                  v-for="item in storageData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-col>
        </el-form-item>
        <el-form-item label="分类" prop="goodstype">
          <el-col :span="20">
            <el-select v-model="form.goodstype" placeholder="请选择分类" style="margin-left: 5px;">
              <el-option
                  v-for="item in goodstypeData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-col>
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-col :span="20">
            <el-input v-model="form.count"></el-input>
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



    <el-dialog
        title="出入库"
        :visible.sync="inDialogVisible"
        width="30%"
        center>

      <el-form ref="form1" :rules="rules1" :model="form1" label-width="80px">
        <el-form-item label="物品名" prop="name">
          <el-col :span="20">
            <el-input v-model="form1.goodsname" readonly></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="数量" prop="count">
          <el-col :span="20">
            <el-input v-model="form1.count"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-col :span="20">
            <el-input type="textarea" v-model="form1.remark"></el-input>
          </el-col>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="inDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="doInGoods">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "GoodsManage",
    data() {
      let checkCount = (rule, value, callback) => {
        if (value > 9999) {
          callback(new Error('数量输入过大'));
        } else {
          callback();
        }
      };
    return {
      InUser:JSON.parse(sessionStorage.getItem('CurUser')),//出入库操作员
      storageData:[],
      goodstypeData:[],
      tableData: [],//返回数据显示
      pageNum:1,
      pageSize:10,
      total:0,
      selname:'',
      storage:'',
      goodstype:'',
      centerDialogVisible:false,
      inDialogVisible:false,
      currenRow:{},//出入库选中行
      form:{//物品
        id:'',
        name:'',
        storage:'',
        goodstype:'',
        count:'',
        remark:''

      },

      form1:{//出入库记录
        id:'',
        goodsid:'',
        goodsname:'',
        userid:'',
        adminname:'',
        count:'',
        type:'',
        remark:'',
        name:''
      },

      rules:{
        name: [
          {required: true, message: '请输入物品名', trigger: 'blur'}
        ],
        storage:[
          {required: true, message: '请选择仓库', trigger: 'blur'}
        ],
        goodstype:[
          {required: true, message: '请选择分类', trigger: 'blur'}
        ],
        count: [
          {required: true, message: '请输入数量', trigger: 'blur'},
          {pattern: /^([1-9][0-9]*){1,4}$/,message: '数量必须为正整数字',trigger: "blur"},
          {validator:checkCount,trigger: 'blur'}
        ]
      },
      rules1:{

      },

    }
  },
  methods:{
    selectCurrentChange(val){//选中行取值
      this.currenRow= val
      //console.log(this.currenRow)
    },
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
    formatterStorage(row){
      let temp = this.storageData.find(item=>{
        return item.id == row.storage
      })
      return temp && temp.name
    },
    formatterGoodsType(row){
      let temp = this.goodstypeData.find(item=>{
        return item.id == row.goodstype
      })
      return temp && temp.name
    },
    resetParam(){//重置搜索
      this.selname=''
      this.storage=''
      this.goodstype=''
      this.pageNum=1
    },
    add(){//新增仓库
      this.centerDialogVisible=true
      this.$nextTick(()=>{this.resetForm()})//异步处理,先打开后重置数据
      this.form.id=''
      console.log(this.form)
    },
    inGood(){//入库
      if(!this.currenRow.id){
        this.$message({
          message:'请选择记录',
          type:'error'
        })
        return;
      }
      this.inDialogVisible=true

      this.$nextTick(()=> {
        this.resetInForm()
        this.form1.id=''
        this.form1.goodsid=this.currenRow.id
        this.form1.goodsname = this.currenRow.name
        this.form1.userid=this.InUser.id
        this.form1.adminname = this.InUser.name
        //this.form1.count = this.currenRow.count
        this.form1.type='入库'
        //this.form1.remark
      })
      console.log(this.form1)
    },
    outGood(){//出库
      if(!this.currenRow.id){
        this.$message({
          message:'请选择记录',
          type:'error'
        })
        return;
      }
      this.inDialogVisible=true

      this.$nextTick(()=> {
        this.resetInForm()
        this.form1.id=''
        this.form1.goodsid=this.currenRow.id
        this.form1.goodsname = this.currenRow.name
        this.form1.userid=this.InUser.id
        this.form1.adminname = this.InUser.name
        this.form1.count = this.currenRow.count
        this.form1.type='出库'
        //this.form1.remark
      })
      console.log(this.form1)
    },
    resetForm(){//重置表单
      this.$refs.form.resetFields()
    },
    resetInForm(){//重置表单
      this.$refs.form1.resetFields()
    },
    edit(row){//编辑
      //打开空表单然后赋值
      this.centerDialogVisible=true
      this.$nextTick(()=> {
        this.form.id = row.id
        this.form.name = row.name
        this.form.storage=row.storage
        this.form.goodstype=row.goodstype
        this.form.count=row.count
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
    doSave(){//新增物品
      this.$axios.post(this.$httpUrl+'/goods/save',this.form).then(res=>res.data).then(res=>{
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
    doInGoods(){//出入库操作
      if(this.form1.type=='出库'){
          if(this.form1.count>this.currenRow.count){
            this.$message({
              message:'库存不足!',
              type:'error'
            })
            return
          }
        this.form = this.currenRow
        this.form.count=this.currenRow.count-this.form1.count
        this.$axios.post(this.$httpUrl+'/goods/update',this.form).then(res=>res.data).then(res=>{
          //console.log(res)
          if(res.code===200){
            this.$message({
              message:'出库成功',
              type:'success'
            })
            this.inDialogVisible=false
            this.loadGet()
            this.resetForm()
          }else{
            this.$message({
              message:'出库失败',
              type:'error'
            })
          }

        })

      }else{//入库
        this.form = this.currenRow
        this.form.count=((this.currenRow.count-0)+(this.form1.count-0))+''
        this.$axios.post(this.$httpUrl+'/goods/update',this.form).then(res=>res.data).then(res=>{
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
      }
      this.$axios.post(this.$httpUrl+'/record/save',this.form1).then(res=>res.data).then(res=>{
        //console.log(res)
        if(res.code===200){
          this.$message({
            message:'记录提交成功',
            type:'success'
          })
          this.inDialogVisible=false
          this.resetInForm()
        }else{
          this.$message({
            message:'记录提交失败',
            type:'error'
          })
        }

      })

    },
    doUpdate(){//修改
      this.$axios.post(this.$httpUrl+'/goods/update',this.form).then(res=>res.data).then(res=>{
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
      this.$axios.get(this.$httpUrl+'/goods/del?id='+id).then(res=>res.data).then(res=>{
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
      this.$axios.post(this.$httpUrl+'/goods/listPageC1',
          {
            pageSize:this.pageSize,
            pageNum:this.pageNum,
            param:{
              name:this.selname,
              storage:this.storage+'',//使传到后端数据为String
              goodstype:this.goodstype+''
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
    },
    loadStorage(){
      this.$axios.get(this.$httpUrl+'/storage/list').then(res=>res.data).then(res=>{
        //console.log(res)
        if(res.code===200){
          this.storageData=res.data
        }else{
          alert("获取数据失败!")
        }

      })
    },
    loadGoodsType(){
      this.$axios.get(this.$httpUrl+'/goodstype/list').then(res=>res.data).then(res=>{
        //console.log(res)
        if(res.code===200){
          this.goodstypeData=res.data
        }else{
          alert("获取数据失败!")
        }

      })
    }

  },
  beforeMount() {
    this.loadStorage()
    this.loadGet()
    this.loadGoodsType()
  }
}
</script>

<style scoped>

</style>