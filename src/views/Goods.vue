<template>
  <div class="main-content">
    <!-- 左侧分类树 -->
    <div class="sidebar">
      <div class="tree-container">
        <div class="tree-title">商品分类</div>
        <el-tree
          :data="categoryTree"
          :props="treeProps"
          node-key="id"
          :default-expanded-keys="expandedKeys"
          :current-node-key="selectedCategoryId"
          @node-click="handleCategoryClick"
          highlight-current
          class="category-tree"
        />
      </div>
    </div>
    
    <!-- 右侧商品列表 -->
    <div class="content">
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="商品名称">
            <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" style="width: 200px;" />
          </el-form-item>
          <el-form-item label="品牌">
            <el-select v-model="searchForm.brandId" placeholder="请选择品牌" style="width: 200px;">
              <el-option
                v-for="brand in brandOptions"
                :key="brand.id"
                :label="brand.brandName"
                :value="brand.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="类别">
            <el-cascader
              v-model="searchForm.categoryId"
              :options="categoryOptions"
              :props="cascaderProps"
              placeholder="请选择类别"
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="primary" @click="handleAdd">添加商品</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 商品表格 -->
      <div class="table-container">
        <div class="table-header">
          <div class="table-title">商品管理</div>
        </div>
        <div class="table-wrapper">
          <el-table
            :data="goodsList"
            v-loading="loading"
            style="width: 100%"
          >
          <el-table-column prop="id" label="编号" width="80" />
          <el-table-column prop="spuImages" label="商品图片" width="120">
            <template #default="scope">
              <el-image
                v-if="scope.row.spuImages"
                :src="getFirstImage(scope.row.spuImages)"
                style="width: 60px; height: 60px;"
                fit="cover"
                :preview-src-list="getImageList(scope.row.spuImages)"
              />
              <span v-else>暂无图片</span>
            </template>
          </el-table-column>
          <el-table-column prop="spuName" label="商品名称" width="200" />
          <el-table-column prop="brandName" label="品牌" width="120" />
          <el-table-column prop="categoryName" label="类别" width="150" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button 
                v-if="scope.row.status === 0" 
                type="success" 
                size="small" 
                @click="handlePublish(scope.row)"
              >
                发布
              </el-button>
              <el-button 
                v-else 
                type="warning" 
                size="small" 
                @click="handleOffline(scope.row)"
              >
                下架
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
          </el-table>
        </div>
        
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
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'Goods',
  setup() {
    const loading = ref(false)
    const goodsList = ref([])
    const brandOptions = ref([])
    const categoryOptions = ref([])
    const categoryTree = ref([])
    const selectedCategoryId = ref(null)
    const expandedKeys = ref([1, 3]) // 默认展开一级和二级分类
    
    const searchForm = reactive({
      keyword: '',
      brandId: '',
      categoryId: []
    })
    
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })
    
    const cascaderProps = {
      value: 'id',
      label: 'categoryName',
      children: 'children',
      checkStrictly: true
    }
    
    const treeProps = {
      children: 'children',
      label: 'categoryName'
    }
    
    // 获取品牌列表
    const getBrandList = async () => {
      try {
        const response = await axios.get('/api/brand/list')
        brandOptions.value = response.data.data
      } catch (error) {
        ElMessage.error('获取品牌列表失败')
      }
    }
    
    // 获取类别列表
    const getCategoryList = async () => {
      try {
        const response = await axios.get('/api/category/tree')
        categoryOptions.value = response.data.data
        categoryTree.value = response.data.data
      } catch (error) {
        ElMessage.error('获取类别列表失败')
      }
    }
    
    // 获取商品列表
    const getGoodsList = async () => {
      loading.value = true
      try {
        const response = await axios.post('/api/goods/page', {
          current: pagination.current,
          size: pagination.size,
          keyword: searchForm.keyword,
          brandId: searchForm.brandId,
          categoryId: selectedCategoryId.value || (searchForm.categoryId.length > 0 ? searchForm.categoryId[searchForm.categoryId.length - 1] : null)
        })
        goodsList.value = response.data.data.records
        pagination.total = response.data.data.total
      } catch (error) {
        ElMessage.error('获取商品列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 分类点击事件
    const handleCategoryClick = (data) => {
      selectedCategoryId.value = data.id
      pagination.current = 1
      getGoodsList()
    }
    
    // 搜索
    const handleSearch = () => {
      selectedCategoryId.value = null // 清空选中的分类
      pagination.current = 1
      getGoodsList()
    }
    
    // 添加商品
    const handleAdd = () => {
      // 跳转到发布商品页面
      window.location.href = '/publish-goods'
    }
    
    // 编辑商品
    const handleEdit = (row) => {
      // 跳转到发布商品页面并传递商品ID
      window.location.href = `/publish-goods?id=${row.id}`
    }
    
    // 发布商品
    const handlePublish = async (row) => {
      try {
        await ElMessageBox.confirm('确定要发布该商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await axios.put(`/api/goods/publish/${row.id}`)
        ElMessage.success('发布成功')
        getGoodsList()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('发布失败')
        }
      }
    }
    
    // 下架商品
    const handleOffline = async (row) => {
      try {
        await ElMessageBox.confirm('确定要下架该商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await axios.put(`/api/goods/offline/${row.id}`)
        ElMessage.success('下架成功')
        getGoodsList()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('下架失败')
        }
      }
    }
    
    // 删除商品
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await axios.delete(`/api/goods/${row.id}`)
        ElMessage.success('删除成功')
        getGoodsList()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.size = size
      getGoodsList()
    }
    
    // 当前页变化
    const handleCurrentChange = (current) => {
      pagination.current = current
      getGoodsList()
    }
    
    // 获取第一张图片
    const getFirstImage = (images) => {
      if (!images) return ''
      const imageList = images.split(',')
      const firstImage = imageList[0] || ''
      // 如果图片URL不是完整路径，添加前缀
      if (firstImage && !firstImage.startsWith('http')) {
        return `/api/upload/${firstImage}`
      }
      return firstImage
    }
    
    // 获取图片列表
    const getImageList = (images) => {
      if (!images) return []
      return images.split(',').filter(img => img.trim()).map(img => {
        // 如果图片URL不是完整路径，添加前缀
        if (img && !img.startsWith('http')) {
          return `/api/upload/${img}`
        }
        return img
      })
    }
    
    onMounted(() => {
      getBrandList()
      getCategoryList()
      getGoodsList()
    })
    
    return {
      loading,
      goodsList,
      brandOptions,
      categoryOptions,
      categoryTree,
      selectedCategoryId,
      expandedKeys,
      searchForm,
      pagination,
      cascaderProps,
      treeProps,
      handleCategoryClick,
      handleSearch,
      handleAdd,
      handleEdit,
      handlePublish,
      handleOffline,
      handleDelete,
      handleSizeChange,
      handleCurrentChange,
      getFirstImage,
      getImageList
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
