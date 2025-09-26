<template>
  <div class="main-content">
    <!-- 左侧部门树形菜单 -->
    <div class="sidebar">
      <div class="tree-container">
        <div class="tree-title">请选择部门</div>
        <el-tree
          :data="deptTree"
          :props="treeProps"
          node-key="id"
          :default-expanded-keys="[1]"
          @node-click="handleDeptClick"
          class="dept-tree"
        />
      </div>
    </div>

    <!-- 右侧部门管理 -->
    <div class="content">
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="部门名称">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入部门名称"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="primary" @click="handleAdd">添加部门</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 部门表格 -->
      <div class="table-container">
        <div class="table-header">
          <div class="table-title">部门管理</div>
        </div>
        <el-table
          :data="deptTree"
          v-loading="loading"
          style="width: 100%"
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          :default-expand-all="true"
        >
          <el-table-column prop="id" label="编号" width="80" />
          <el-table-column prop="deptName" label="部门名称" width="200" />
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
          <el-table-column label="操作" width="300">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(scope.row)"
                >编辑</el-button
              >
              <el-button
                v-if="scope.row.level < 4"
                type="success"
                size="small"
                @click="handleAddChild(scope.row)"
              >
                添加子部门
              </el-button>
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

    <!-- 添加/编辑部门弹窗 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        :model="deptForm"
        :rules="deptRules"
        ref="deptFormRef"
        label-width="100px"
      >
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="deptForm.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="父部门" prop="parentId">
          <el-tree-select
            v-model="deptForm.parentId"
            :data="deptTree"
            :props="treeProps"
            placeholder="请选择父部门"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="deptForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="deptForm.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

export default {
  name: "Dept",
  setup() {
    const loading = ref(false);
    const deptTree = ref([]);
    const dialogVisible = ref(false);
    const dialogTitle = ref("添加部门");
    const deptFormRef = ref();

    const searchForm = reactive({
      keyword: "",
    });

    const deptForm = reactive({
      deptName: "",
      parentId: 0,
      sortOrder: 0,
      status: 1,
    });

    const deptRules = {
      deptName: [
        { required: true, message: "请输入部门名称", trigger: "blur" },
      ],
    };

    const treeProps = {
      children: "children",
      label: "deptName",
      value: "id",
    };

    // 获取部门树形结构
    const getDeptTree = async () => {
      loading.value = true;
      try {
        const response = await axios.get("/api/dept/tree");
        originalDeptTree.value = response.data.data;
        deptTree.value = response.data.data;
      } catch (error) {
        ElMessage.error("获取部门列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 部门点击事件
    const handleDeptClick = (data) => {
      console.log("选中部门:", data);
      // 点击名称==>编辑当前部门
      handleEdit(data);
    };

    // 判断是否有子节点
    const hasChildren = (node) => {
      return node.children && node.children.length > 0;
    };

    // 原始部门树数据
    const originalDeptTree = ref([]);

    // 搜索
    const handleSearch = () => {
      if (searchForm.keyword.trim()) {
        deptTree.value = searchDeptTree(
          originalDeptTree.value,
          searchForm.keyword
        );
      } else {
        deptTree.value = originalDeptTree.value;
      }
    };

    // 递归搜索部门树
    const searchDeptTree = (tree, keyword) => {
      if (!keyword) return tree;

      const result = [];
      for (const node of tree) {
        if (node.deptName.includes(keyword)) {
          result.push(node);
        } else if (node.children && node.children.length > 0) {
          const children = searchDeptTree(node.children, keyword);
          if (children.length > 0) {
            result.push({
              ...node,
              children,
            });
          }
        }
      }
      return result;
    };

    // 添加部门
    const handleAdd = () => {
      dialogTitle.value = "添加部门";
      dialogVisible.value = true;
    };

    // 添加子部门
    const handleAddChild = (row) => {
      dialogTitle.value = "添加子部门";
      deptForm.parentId = row.id;
      dialogVisible.value = true;
    };

    // 编辑部门
    const handleEdit = (row) => {
      dialogTitle.value = "编辑部门";
      Object.assign(deptForm, row);
      dialogVisible.value = true;
    };

    // 删除部门
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm("确定要删除该部门吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });

        const response = await axios.delete(`/api/dept/${row.id}`);
        if (response.data.code === 200) {
          ElMessage.success("删除成功");
          getDeptTree();
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

    // 提交表单
    const handleSubmit = async () => {
      if (!deptFormRef.value) return;

      await deptFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            if (deptForm.id) {
              const response = await axios.put("/api/dept", deptForm);
              if (response.data.code === 200) {
                ElMessage.success("修改成功");
                dialogVisible.value = false;
                getDeptTree();
              } else {
                ElMessage.error(response.data.message || "修改失败");
              }
            } else {
              const response = await axios.post("/api/dept", deptForm);
              if (response.data.code === 200) {
                ElMessage.success("添加成功");
                dialogVisible.value = false;
                getDeptTree();
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

    // 弹窗关闭
    const handleDialogClose = () => {
      if (deptFormRef.value) {
        deptFormRef.value.resetFields();
      }
      Object.assign(deptForm, {
        deptName: "",
        parentId: 0,
        sortOrder: 0,
        status: 1,
      });
    };

    onMounted(() => {
      getDeptTree();
    });

    return {
      loading,
      deptTree,
      originalDeptTree,
      dialogVisible,
      dialogTitle,
      deptFormRef,
      searchForm,
      deptForm,
      deptRules,
      treeProps,
      handleDeptClick,
      hasChildren,
      handleSearch,
      handleAdd,
      handleAddChild,
      handleEdit,
      handleDelete,
      handleSubmit,
      handleDialogClose,
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

.dept-tree {
  background: transparent;
}

.dept-tree :deep(.el-tree-node__content) {
  height: 40px;
  line-height: 40px;
  padding: 0 10px;
  border-radius: 4px;
  margin-bottom: 2px;
}

.dept-tree :deep(.el-tree-node__content:hover) {
  background-color: #e6f7ff;
}

.dept-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
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
