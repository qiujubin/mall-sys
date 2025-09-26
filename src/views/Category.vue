<template>
  <div class="main-content">
    <!-- 左侧类别树形菜单 -->
    <!-- <div class="sidebar">
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
    </div> -->

    <!-- 右侧类别管理 -->
    <div class="content">
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="类别名称">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入类别名称"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="primary" @click="handleAdd">添加类别</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 类别表格 -->
      <div class="table-container">
        <div class="table-header">
          <div class="table-title">类别管理</div>
        </div>
        <el-table
          :data="categoryTree"
          v-loading="loading"
          style="width: 100%"
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          :default-expand-all="true"
        >
          <el-table-column prop="id" label="编号" width="80">
            <template #default="scope">
              <span
                :style="{
                  paddingLeft: (scope.row.level - 1) * 20 + 20 + 'px',
                  display: 'inline-block',
                  lineHeight: '32px',
                  verticalAlign: 'middle',
                }"
              >
                {{ scope.row.id }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="类别名称" width="200">
            <template #default="scope">
              <span
                style="cursor: pointer; color: #409eff"
                @click="handleEdit(scope.row)"
              >
                {{ scope.row.categoryName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="层级" width="80" />
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? "启用" : "禁用" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="400">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(scope.row)"
                >编辑</el-button
              >
              <el-button
                v-if="scope.row.level < 3"
                type="success"
                size="small"
                @click="handleAddChild(scope.row)"
              >
                添加子类别
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="handleBrandRelation(scope.row)"
                >关联品牌</el-button
              >
              <el-button
                v-if="!hasChildren(scope.row)"
                type="danger"
                size="small"
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 添加/编辑类别弹窗 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        :model="categoryForm"
        :rules="categoryRules"
        ref="categoryFormRef"
        label-width="100px"
      >
        <el-form-item label="类别名称" prop="categoryName">
          <el-input
            v-model="categoryForm.categoryName"
            placeholder="请输入类别名称"
          />
        </el-form-item>
        <el-form-item label="父类别" prop="parentId">
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="categoryTree"
            :props="treeProps"
            placeholder="请选择父类别"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="categoryForm.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 关联品牌弹窗 -->
    <el-dialog title="关联品牌" v-model="brandDialogVisible" width="800px">
      <div style="margin-bottom: 20px">
        <el-input
          v-model="brandSearchKeyword"
          placeholder="搜索品牌"
          style="width: 200px; margin-right: 10px"
        />
        <el-button type="primary" @click="searchBrands">搜索</el-button>
      </div>
      <el-table
        :data="brandList"
        @selection-change="handleBrandSelectionChange"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="brandName" label="品牌名称" />
        <el-table-column prop="brandDesc" label="品牌描述" />
        <el-table-column prop="brandLogo" label="品牌LOGO" width="100">
          <template #default="scope">
            <el-image
              v-if="scope.row.brandLogo"
              :src="scope.row.brandLogo"
              style="width: 40px; height: 40px"
              fit="cover"
            />
            <span v-else>暂无图片</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 品牌分页 -->
      <div
        class="pagination-container"
        style="margin-top: 20px; text-align: center"
      >
        <el-pagination
          v-model:current-page="brandPagination.current"
          v-model:page-size="brandPagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="brandPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleBrandSizeChange"
          @current-change="handleBrandCurrentChange"
        />
      </div>
      <template #footer>
        <el-button @click="brandDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBrandSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

export default {
  name: "Category",
  setup() {
    const loading = ref(false);
    const categoryTree = ref([]);
    const brandList = ref([]);
    const selectedBrands = ref([]);
    const currentCategory = ref(null);
    const dialogVisible = ref(false);
    const brandDialogVisible = ref(false);
    const dialogTitle = ref("添加类别");
    const categoryFormRef = ref();
    const brandSearchKeyword = ref("");

    const brandPagination = reactive({
      current: 1,
      size: 10,
      total: 0,
    });

    const searchForm = reactive({
      keyword: "",
    });

    const categoryForm = reactive({
      categoryName: "",
      parentId: 0,
      sortOrder: 0,
      status: true,
    });

    const categoryRules = {
      categoryName: [
        { required: true, message: "请输入类别名称", trigger: "blur" },
      ],
    };

    const treeProps = {
      children: "children",
      label: "categoryName",
      value: "id",
    };

    // 获取类别树形结构
    const getCategoryTree = async () => {
      loading.value = true;
      try {
        const response = await axios.get("/api/category/tree");
        originalCategoryTree.value = response.data.data;
        categoryTree.value = response.data.data;
      } catch (error) {
        ElMessage.error("获取类别列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 获取品牌列表
    const getBrandList = async () => {
      try {
        const response = await axios.post("/api/brand/page", {
          current: brandPagination.current,
          size: brandPagination.size,
          keyword: brandSearchKeyword.value,
        });
        brandList.value = response.data.data.records;
        brandPagination.total = response.data.data.total;
      } catch (error) {
        ElMessage.error("获取品牌列表失败");
      }
    };

    // 类别点击事件
    const handleCategoryClick = (data) => {
      console.log("选中类别:", data);
    };

    // 原始类别树数据
    const originalCategoryTree = ref([]);

    // 搜索
    const handleSearch = () => {
      if (searchForm.keyword.trim()) {
        categoryTree.value = searchCategoryTree(
          originalCategoryTree.value,
          searchForm.keyword
        );
      } else {
        categoryTree.value = originalCategoryTree.value;
      }
    };

    // 递归搜索类别树
    const searchCategoryTree = (tree, keyword) => {
      const result = [];
      tree.forEach((node) => {
        const matchesKeyword = node.categoryName
          .toLowerCase()
          .includes(keyword.toLowerCase());
        const children = node.children
          ? searchCategoryTree(node.children, keyword)
          : [];

        if (matchesKeyword || children.length > 0) {
          result.push({
            ...node,
            children: children,
          });
        }
      });
      return result;
    };

    // 添加类别
    const handleAdd = () => {
      dialogTitle.value = "添加类别";
      dialogVisible.value = true;
    };

    // 添加子类别
    const handleAddChild = (row) => {
      dialogTitle.value = "添加子类别";
      categoryForm.parentId = row.id;
      dialogVisible.value = true;
    };

    // 编辑类别
    const handleEdit = (row) => {
      dialogTitle.value = "编辑类别";
      Object.assign(categoryForm, row);
      dialogVisible.value = true;
    };

    // 关联品牌
    const handleBrandRelation = (row) => {
      currentCategory.value = row;
      getBrandList();
      brandDialogVisible.value = true;
    };

    // 删除类别
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm("确定要删除该类别吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });

        const response = await axios.delete(`/api/category/${row.id}`);
        if (response.data.code === 200) {
          ElMessage.success("删除成功");
          getCategoryTree();
        } else {
          ElMessage.error(response.data.message || "删除失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          if (
            error.response &&
            error.response.data &&
            error.response.data.message
          ) {
            ElMessage.error(error.response.data.message);
          } else {
            ElMessage.error("删除失败");
          }
        }
      }
    };

    // 搜索品牌
    const searchBrands = () => {
      brandPagination.current = 1;
      getBrandList();
    };

    // 品牌分页大小变化
    const handleBrandSizeChange = (size) => {
      brandPagination.size = size;
      brandPagination.current = 1;
      getBrandList();
    };

    // 品牌分页页码变化
    const handleBrandCurrentChange = (current) => {
      brandPagination.current = current;
      getBrandList();
    };

    // 品牌选择变化
    const handleBrandSelectionChange = (selection) => {
      selectedBrands.value = selection;
    };

    // 提交品牌关联
    const handleBrandSubmit = async () => {
      if (selectedBrands.value.length === 0) {
        ElMessage.warning("请选择要关联的品牌");
        return;
      }

      try {
        const brandIds = selectedBrands.value.map((brand) => brand.id);
        await axios.post("/api/category/brand-relation", {
          categoryId: currentCategory.value.id,
          brandIds: brandIds,
        });
        ElMessage.success("关联成功");
        brandDialogVisible.value = false;
      } catch (error) {
        ElMessage.error("关联失败");
      }
    };

    // 提交表单
    const handleSubmit = async () => {
      if (!categoryFormRef.value) return;

      await categoryFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            if (categoryForm.id) {
              const response = await axios.put("/api/category", categoryForm);
              if (response.data.code === 200) {
                ElMessage.success("修改成功");
                dialogVisible.value = false;
                getCategoryTree();
              } else {
                ElMessage.error(response.data.message || "修改失败");
              }
            } else {
              const response = await axios.post("/api/category", categoryForm);
              if (response.data.code === 200) {
                ElMessage.success("添加成功");
                dialogVisible.value = false;
                getCategoryTree();
              } else {
                ElMessage.error(response.data.message || "添加失败");
              }
            }
          } catch (error) {
            if (
              error.response &&
              error.response.data &&
              error.response.data.message
            ) {
              ElMessage.error(error.response.data.message);
            } else {
              ElMessage.error("操作失败");
            }
          }
        }
      });
    };

    // 检查是否有子节点
    const hasChildren = (row) => {
      return row.children && row.children.length > 0;
    };

    // 弹窗关闭
    const handleDialogClose = () => {
      if (categoryFormRef.value) {
        categoryFormRef.value.resetFields();
      }
      Object.assign(categoryForm, {
        categoryName: "",
        parentId: 0,
        sortOrder: 0,
        status: true,
      });
    };

    onMounted(() => {
      getCategoryTree();
    });

    return {
      loading,
      categoryTree,
      brandList,
      selectedBrands,
      currentCategory,
      dialogVisible,
      brandDialogVisible,
      dialogTitle,
      categoryFormRef,
      brandSearchKeyword,
      searchForm,
      categoryForm,
      categoryRules,
      treeProps,
      handleCategoryClick,
      handleSearch,
      handleAdd,
      handleAddChild,
      handleEdit,
      handleBrandRelation,
      handleDelete,
      searchBrands,
      handleBrandSelectionChange,
      handleBrandSubmit,
      handleSubmit,
      handleDialogClose,
      hasChildren,
      brandPagination,
      handleBrandSizeChange,
      handleBrandCurrentChange,
    };
  },
};
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
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
