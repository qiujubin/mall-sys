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

    <!-- 右侧属性分组列表 -->
    <div class="content">
      <!-- 子导航 -->
      <el-tabs v-model="activeTab" class="content-tabs">
        <el-tab-pane label="属性分组" name="list">
          <!-- 搜索表单 -->
          <div class="search-form">
            <el-form :model="searchForm" inline>
              <el-form-item label="当前类别:">
                <el-input
                  v-model="currentCategory"
                  readonly
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item label="分组名称:">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="分组名称"
                  style="width: 200px"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="handleAdd"
                  >新增分组</el-button
                >
              </el-form-item>
            </el-form>
          </div>

          <!-- 属性分组表格 -->
          <div class="table-container">
            <div class="table-header">
              <div class="table-title">属性分组</div>
            </div>
            <el-table
              :data="attrGroupList"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="id" label="编号" width="80" />
              <el-table-column prop="groupName" label="分组名称" width="150" />
              <el-table-column prop="categoryId" label="类别ID" width="100" />
              <el-table-column
                prop="categoryDesc"
                label="类别描述"
                width="150"
              />
              <el-table-column prop="icon" label="图标" width="100" />
              <el-table-column prop="sortOrder" label="排序" width="80" />
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleEdit(scope.row)"
                    >编辑</el-button
                  >
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleDelete(scope.row)"
                    >删除</el-button
                  >
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
        <el-tab-pane label="添加属性分组" name="add">
          <!-- 添加属性分组表单 -->
          <div class="search-form">
            <el-form
              :model="attrGroupForm"
              :rules="attrGroupRules"
              ref="attrGroupFormRef"
              label-width="100px"
            >
              <el-form-item label="分组名称" prop="groupName">
                <el-input
                  v-model="attrGroupForm.groupName"
                  placeholder="新的分组"
                />
              </el-form-item>
              <el-form-item label="所属类别" prop="categoryId">
                <el-cascader
                  v-model="attrGroupForm.categoryId"
                  :options="categoryOptions"
                  :props="cascaderProps"
                  placeholder="请选择类别"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="可否检索" prop="searchable">
                <el-switch v-model="attrGroupForm.searchable" />
              </el-form-item>
              <el-form-item label="启用状态" prop="status">
                <el-switch v-model="attrGroupForm.status" />
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
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

export default {
  name: "AttrGroup",
  setup() {
    const activeTab = ref("list");
    const loading = ref(false);
    const attrGroupList = ref([]);
    const categoryTree = ref([]);
    const categoryOptions = ref([]);
    const currentCategory = ref("游戏手机");
    const selectedCategoryId = ref(11); // 默认选中游戏手机类别
    const attrGroupFormRef = ref();

    const searchForm = reactive({
      keyword: "",
    });

    const attrGroupForm = reactive({
      groupName: "",
      categoryId: [],
      searchable: true,
      status: true,
    });

    const attrGroupRules = {
      groupName: [
        { required: true, message: "请输入分组名称", trigger: "blur" },
      ],
      categoryId: [
        { required: true, message: "请选择类别", trigger: "change" },
      ],
    };

    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0,
    });

    const treeProps = {
      children: "children",
      label: "categoryName",
    };

    const cascaderProps = {
      value: "id",
      label: "categoryName",
      children: "children",
      checkStrictly: true,
    };

    // 获取类别树形结构
    const getCategoryTree = async () => {
      try {
        const response = await axios.get("/api/category/tree");
        categoryTree.value = response.data.data;
        categoryOptions.value = response.data.data;
      } catch (error) {
        ElMessage.error("获取类别列表失败");
      }
    };

    // 获取属性分组列表
    const getAttrGroupList = async () => {
      loading.value = true;
      try {
        const response = await axios.post("/api/attr-group/page", {
          current: pagination.current,
          size: pagination.size,
          keyword: searchForm.keyword,
          categoryId: selectedCategoryId.value, // 使用选中的类别ID
        });
        attrGroupList.value = response.data.data.records;
        pagination.total = response.data.data.total;
      } catch (error) {
        ElMessage.error("获取属性分组列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 类别点击事件
    const handleCategoryClick = (data) => {
      console.log("选中类别:", data);
      currentCategory.value = data.categoryName;
      selectedCategoryId.value = data.id; // 保存选中的类别ID
      pagination.current = 1; // 重置到第一页
      getAttrGroupList();
    };

    // 搜索
    const handleSearch = () => {
      pagination.current = 1;
      getAttrGroupList();
    };

    // 添加属性分组
    const handleAdd = () => {
      activeTab.value = "add";
    };

    // 编辑属性分组
    const handleEdit = (row) => {
      Object.assign(attrGroupForm, {
        ...row,
        categoryId: [row.categoryId],
        searchable: row.searchable === 1, // 转换为布尔值
        status: row.status === 1, // 转换为布尔值
      });
      activeTab.value = "add";
    };

    // 删除属性分组
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm("确定要删除该属性分组吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });

        await axios.delete(`/api/attr-group/${row.id}`);
        ElMessage.success("删除成功");
        getAttrGroupList();
      } catch (error) {
        if (error !== "cancel") {
          ElMessage.error("删除失败");
        }
      }
    };

    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.size = size;
      getAttrGroupList();
    };

    // 当前页变化
    const handleCurrentChange = (current) => {
      pagination.current = current;
      getAttrGroupList();
    };

    // 提交表单
    const handleSubmit = async () => {
      if (!attrGroupFormRef.value) return;

      await attrGroupFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const formData = {
              ...attrGroupForm,
              categoryId:
                attrGroupForm.categoryId[attrGroupForm.categoryId.length - 1],
              searchable: attrGroupForm.searchable ? 1 : 0, // 转换为整数
              status: attrGroupForm.status ? 1 : 0, // 转换为整数
            };

            if (attrGroupForm.id) {
              await axios.put("/api/attr-group", formData);
              ElMessage.success("修改成功");
            } else {
              await axios.post("/api/attr-group", formData);
              ElMessage.success("添加成功");
            }
            handleReset();
            activeTab.value = "list";
            getAttrGroupList();
          } catch (error) {
            ElMessage.error("操作失败");
          }
        }
      });
    };

    // 重置表单
    const handleReset = () => {
      if (attrGroupFormRef.value) {
        attrGroupFormRef.value.resetFields();
      }
      Object.assign(attrGroupForm, {
        groupName: "",
        categoryId: [],
        searchable: true,
        status: true,
      });
    };

    onMounted(() => {
      getCategoryTree();
      getAttrGroupList();
    });

    return {
      activeTab,
      loading,
      attrGroupList,
      categoryTree,
      categoryOptions,
      currentCategory,
      selectedCategoryId,
      attrGroupFormRef,
      searchForm,
      attrGroupForm,
      attrGroupRules,
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
      handleReset,
    };
  },
};
</script>

<style scoped>
.main-content {
  display: flex;
  height: 100vh;
  background-color: #fff;
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
