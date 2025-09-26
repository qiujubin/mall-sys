<template>
  <div class="main-content">
    <!-- 左侧类别树形菜单 -->
    <div class="sidebar">
      <div class="tree-container">
        <div class="tree-title">请选择分类</div>
        <el-tree
          :data="categoryTree"
          :props="treeProps"
          node-key="id"
          :default-expanded-keys="[1, 10]"
          @node-click="handleCategoryClick"
          class="category-tree"
        />
      </div>
    </div>
    
    <!-- 右侧销售属性列表 -->
    <div class="content">
      <!-- 子导航 -->
      <el-tabs v-model="activeTab" class="content-tabs">
        <el-tab-pane label="销售属性" name="list">
          <!-- 搜索表单 -->
          <div class="search-form">
            <el-form :model="searchForm" inline>
              <el-form-item label="当前显示类别:">
                <el-input v-model="currentCategory" readonly style="width: 200px;" />
              </el-form-item>
              <el-form-item label="属性名称:">
                <el-input v-model="searchForm.keyword" placeholder="属性名称" style="width: 200px;" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="handleAdd">新增属性</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 销售属性表格 -->
          <div class="table-container">
            <div class="table-header">
              <div class="table-title">销售属性</div>
            </div>
            <el-table
              :data="saleAttrList"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="id" label="编号" width="80" />
              <el-table-column prop="attrName" label="属性名称" width="150" />
              <el-table-column prop="valueType" label="值类型" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.valueType === 1 ? 'info' : 'warning'">
                    {{ scope.row.valueType === 1 ? '单值型' : '多值型' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="attrValue" label="可选值" width="200" />
              <el-table-column prop="status" label="状态" width="80">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '启用' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 分页 -->
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="pagination.current"
                v-model:page-size="pagination.size"
                :page-sizes="[10, 20, 50, 100]"
                :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="添加销售属性" name="add">
          <!-- 添加销售属性表单 -->
          <div class="search-form">
            <el-form :model="saleAttrForm" :rules="saleAttrRules" ref="saleAttrFormRef" label-width="100px">
              <el-form-item label="属性名称" prop="attrName">
                <el-input v-model="saleAttrForm.attrName" placeholder="新属性名称" />
              </el-form-item>
              <el-form-item label="值类型" prop="valueType">
                <el-select v-model="saleAttrForm.valueType" placeholder="请选择值类型">
                  <el-option label="单值" :value="1" />
                  <el-option label="多值" :value="2" />
                </el-select>
              </el-form-item>
              <el-form-item label="可选值" prop="attrValue">
                <el-input v-model="saleAttrForm.attrValue" placeholder="请输入可选值，多值用分号分隔" />
              </el-form-item>
              <el-form-item label="所属类别" prop="categoryId">
                <el-cascader
                  v-model="saleAttrForm.categoryId"
                  :options="categoryOptions"
                  :props="cascaderProps"
                  placeholder="请选择类别"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="可否检索" prop="searchable">
                <el-switch v-model="saleAttrForm.searchable" />
              </el-form-item>
              <el-form-item label="启用状态" prop="status">
                <el-switch v-model="saleAttrForm.status" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
                <el-button @click="handleReset">取消</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'SaleAttr',
  setup() {
    const activeTab = ref('list')
    const loading = ref(false)
    const saleAttrList = ref([])
    const categoryTree = ref([])
    const categoryOptions = ref([])
    const currentCategory = ref('游戏手机')
    const selectedCategoryId = ref(7) // 默认选中游戏手机类别
    const saleAttrFormRef = ref()
    
    const searchForm = reactive({
      keyword: ''
    })
    
    const saleAttrForm = reactive({
      attrName: '',
      valueType: 1,
      attrValue: '',
      categoryId: [],
      searchable: true,
      status: true
    })
    
    const saleAttrRules = {
      attrName: [{ required: true, message: '请输入属性名称', trigger: 'blur' }],
      valueType: [{ required: true, message: '请选择值类型', trigger: 'change' }],
      categoryId: [{ required: true, message: '请选择类别', trigger: 'change' }]
    }
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })
    
    const treeProps = {
      children: 'children',
      label: 'categoryName'
    }
    
    const cascaderProps = {
      value: 'id',
      label: 'categoryName',
      children: 'children',
      checkStrictly: true
    }
    
    // 获取类别树形结构
    const getCategoryTree = async () => {
      try {
        const response = await axios.get('/api/category/tree')
        categoryTree.value = response.data.data
        categoryOptions.value = response.data.data
      } catch (error) {
        ElMessage.error('获取类别列表失败')
      }
    }
    
    // 获取销售属性列表
    const getSaleAttrList = async () => {
      loading.value = true
      try {
        const response = await axios.post('/api/sale-attr/page', {
          current: pagination.current,
          size: pagination.size,
          keyword: searchForm.keyword,
          categoryId: selectedCategoryId.value // 使用选中的类别ID
        })
        saleAttrList.value = response.data.data.records
        pagination.total = response.data.data.total
      } catch (error) {
        ElMessage.error('获取销售属性列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 类别点击事件
    const handleCategoryClick = (data) => {
      console.log('选中类别:', data)
      currentCategory.value = data.categoryName
      selectedCategoryId.value = data.id // 保存选中的类别ID
      pagination.current = 1 // 重置到第一页
      getSaleAttrList()
    }
    
    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      getSaleAttrList()
    }
    
    // 添加销售属性
    const handleAdd = () => {
      activeTab.value = 'add'
    }
    
    // 编辑销售属性
    const handleEdit = (row) => {
      Object.assign(saleAttrForm, {
        ...row,
        categoryId: [row.categoryId]
      })
      activeTab.value = 'add'
    }
    
    // 删除销售属性
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该销售属性吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await axios.delete(`/api/sale-attr/${row.id}`)
        ElMessage.success('删除成功')
        getSaleAttrList()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.size = size
      getSaleAttrList()
    }
    
    // 当前页变化
    const handleCurrentChange = (current) => {
      pagination.current = current
      getSaleAttrList()
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!saleAttrFormRef.value) return
      
      await saleAttrFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const formData = {
              ...saleAttrForm,
              categoryId: saleAttrForm.categoryId[saleAttrForm.categoryId.length - 1],
              attrType: 2 // 销售属性
            }
            
            if (saleAttrForm.id) {
              await axios.put('/api/sale-attr', formData)
              ElMessage.success('修改成功')
            } else {
              await axios.post('/api/sale-attr', formData)
              ElMessage.success('添加成功')
            }
            handleReset()
            activeTab.value = 'list'
            getSaleAttrList()
          } catch (error) {
            ElMessage.error('操作失败')
          }
        }
      })
    }
    
    // 重置表单
    const handleReset = () => {
      if (saleAttrFormRef.value) {
        saleAttrFormRef.value.resetFields()
      }
      Object.assign(saleAttrForm, {
        attrName: '',
        valueType: 1,
        attrValue: '',
        categoryId: [],
        searchable: true,
        status: true
      })
    }
    
    onMounted(() => {
      getCategoryTree()
      getSaleAttrList()
    })
    
    return {
      activeTab,
      loading,
      saleAttrList,
      categoryTree,
      categoryOptions,
      currentCategory,
      selectedCategoryId,
      saleAttrFormRef,
      searchForm,
      saleAttrForm,
      saleAttrRules,
      pagination,
      treeProps,
      cascaderProps,
      handleCategoryClick,
      handleSearch,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSizeChange,
      handleCurrentChange,
      handleSubmit,
      handleReset
    }
  }
}
</script>

<style scoped>
.main-content {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background: #f5f5f5;
  border-right: 1px solid #e0e0e0;
  overflow-y: auto;
}

.tree-container {
  padding: 20px;
}

.tree-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.category-tree {
  background: transparent;
}

.category-tree :deep(.el-tree-node__content) {
  height: 40px;
  line-height: 40px;
  padding: 0 10px;
  border-radius: 4px;
  margin-bottom: 2px;
}

.category-tree :deep(.el-tree-node__content:hover) {
  background-color: #e6f7ff;
}

.category-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #1890ff;
  color: white;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.content-tabs {
  height: 100%;
}

.content-tabs .el-tab-pane {
  height: calc(100vh - 120px);
  overflow-y: auto;
}

.search-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
}

.table-title {
  font-size: 18px;
  font-weight: bold;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  background: #fafafa;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
}

.pagination-container {
  padding: 20px;
  text-align: right;
  border-top: 1px solid #e0e0e0;
}
</style>
