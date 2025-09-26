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

    <!-- 右侧用户列表 -->
    <div class="content">
      <!-- 子导航 -->
      <el-tabs v-model="activeTab" class="content-tabs">
        <el-tab-pane label="用户列表" name="list">
          <!-- 搜索表单 -->
          <div class="search-form">
            <el-form :model="searchForm" inline>
              <el-form-item label="用户名称">
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="请输入用户名称"
                  style="width: 200px"
                />
              </el-form-item>

              <el-form-item label="角色">
                <el-select
                  v-model="searchForm.roleId"
                  placeholder="请选择角色"
                  style="width: 200px"
                >
                  <el-option label="总经理" value="1" />
                  <el-option label="部门主管" value="2" />
                  <el-option label="普通员工" value="3" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button type="primary" @click="handleAdd">添加</el-button>
                <el-button type="danger" @click="handleBatchDelete"
                  >批量删除</el-button
                >
              </el-form-item>
            </el-form>
          </div>

          <!-- 用户表格 -->
          <div class="table-container">
            <div class="table-header">
              <div class="table-title">用户列表</div>
            </div>
            <el-table
              :data="userList"
              v-loading="loading"
              @selection-change="handleSelectionChange"
              style="width: 100%"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="id" label="编号" width="80" />
              <el-table-column prop="account" label="帐号" width="120" />
              <el-table-column prop="nickname" label="昵称" width="120" />
              <el-table-column prop="phone" label="电话" width="140" />
              <el-table-column prop="gender" label="性别" width="80" />
              <el-table-column prop="employeeId" label="工号" width="120" />
              <el-table-column prop="deptName" label="部门" width="120" />
              <el-table-column prop="roleName" label="角色" width="120" />
              <el-table-column prop="createTime" label="创建日期" width="120" />
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
        <el-tab-pane label="添加用户" name="add">
          <!-- 添加用户表单 -->
          <div class="search-form">
            <el-form
              :model="userForm"
              :rules="userRules"
              ref="userFormRef"
              label-width="100px"
            >
              <el-form-item label="账号" prop="account">
                <el-input v-model="userForm.account" placeholder="请输入账号" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="userForm.password"
                  type="password"
                  placeholder="请输入密码"
                />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input
                  v-model="userForm.nickname"
                  placeholder="请输入昵称"
                />
              </el-form-item>
              <el-form-item label="电话" prop="phone">
                <el-input v-model="userForm.phone" placeholder="请输入电话" />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-select v-model="userForm.gender" placeholder="请选择性别">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                </el-select>
              </el-form-item>
              <el-form-item label="工号" prop="employeeId">
                <el-input
                  v-model="userForm.employeeId"
                  placeholder="请输入工号"
                />
              </el-form-item>
              <el-form-item label="部门" prop="deptId">
                <el-select v-model="userForm.deptId" placeholder="请选择部门">
                  <el-option
                    v-for="dept in deptOptions"
                    :key="dept.id"
                    :label="dept.deptName"
                    :value="dept.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="角色" prop="roleId">
                <el-select v-model="userForm.roleId" placeholder="请选择角色">
                  <el-option label="总经理" :value="1" />
                  <el-option label="部门主管" :value="2" />
                  <el-option label="普通员工" :value="3" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSubmit">提交</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入用户"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="import-container">
        <!-- 文件上传区域 -->
        <div class="upload-section">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            :file-list="fileList"
            accept=".xlsx,.xls,.txt"
            drag
            multiple
            :limit="1"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 Excel 文件(.xlsx/.xls) 或文本文件(.txt)，且不超过 10MB
              </div>
            </template>
          </el-upload>
        </div>

        <!-- 导入进度 -->
        <div v-if="importProgress.show" class="progress-section">
          <el-progress
            :percentage="importProgress.percentage"
            :status="importProgress.status"
            :stroke-width="8"
          />
          <div class="progress-text">{{ importProgress.text }}</div>
        </div>

        <!-- 导入结果 -->
        <div v-if="importResult.show" class="result-section">
          <el-alert
            :title="importResult.title"
            :type="importResult.type"
            :description="importResult.description"
            show-icon
            :closable="false"
          />
          <div
            v-if="importResult.errors && importResult.errors.length > 0"
            class="error-details"
          >
            <h4>错误详情：</h4>
            <ul>
              <li v-for="(error, index) in importResult.errors" :key="index">
                第{{ error.row }}行：{{ error.message }}
              </li>
            </ul>
          </div>
        </div>

        <!-- 模板下载 -->
        <div class="template-section">
          <el-button type="info" @click="downloadTemplate">
            <el-icon><download /></el-icon>
            下载导入模板
          </el-button>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleImportCancel">
            {{ importResult.show ? "关闭" : "取消" }}
          </el-button>
          <el-button
            v-if="!importResult.show"
            type="primary"
            @click="handleImportSubmit"
            :disabled="!selectedFile || importProgress.show"
            :loading="importProgress.show"
          >
            开始导入
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { UploadFilled, Download } from "@element-plus/icons-vue";
import axios from "axios";

export default {
  name: "User",
  setup() {
    const activeTab = ref("list");
    const loading = ref(false);
    const userList = ref([]);
    const deptTree = ref([]);
    const deptOptions = ref([]);
    const selectedRows = ref([]);
    const userFormRef = ref();

    // 批量导入相关
    const importDialogVisible = ref(false);
    const uploadRef = ref();
    const fileList = ref([]);
    const selectedFile = ref(null);
    const importProgress = reactive({
      show: false,
      percentage: 0,
      status: "",
      text: "",
    });
    const importResult = reactive({
      show: false,
      title: "",
      type: "success",
      description: "",
      errors: [],
    });

    const searchForm = reactive({
      keyword: "",
      keyword2: "",
      roleId: "",
      deptId: null,
    });

    const userForm = reactive({
      account: "",
      password: "",
      nickname: "",
      phone: "",
      gender: "",
      employeeId: "",
      deptId: "",
      roleId: "",
    });

    const userRules = {
      account: [{ required: true, message: "请输入账号", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
      phone: [{ required: true, message: "请输入电话", trigger: "blur" }],
      gender: [{ required: true, message: "请选择性别", trigger: "change" }],
      employeeId: [{ required: true, message: "请输入工号", trigger: "blur" }],
      deptId: [{ required: true, message: "请选择部门", trigger: "change" }],
      roleId: [{ required: true, message: "请选择角色", trigger: "change" }],
    };

    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0,
    });

    const treeProps = {
      children: "children",
      label: "deptName",
    };

    // 获取部门树形结构
    const getDeptTree = async () => {
      try {
        const response = await axios.get("/api/dept/tree");
        deptTree.value = response.data.data;
        // 扁平化部门数据用于下拉选择
        deptOptions.value = flattenTree(response.data.data);
      } catch (error) {
        ElMessage.error("获取部门列表失败");
      }
    };

    // 扁平化树形数据
    const flattenTree = (tree) => {
      let result = [];
      tree.forEach((node) => {
        result.push(node);
        if (node.children && node.children.length > 0) {
          result = result.concat(flattenTree(node.children));
        }
      });
      return result;
    };

    // 获取用户列表
    const getUserList = async () => {
      loading.value = true;
      try {
        const response = await axios.post("/api/user/page", {
          current: pagination.current,
          size: pagination.size,
          keyword: searchForm.keyword,
          deptId: searchForm.deptId,
        });
        const users = response.data.data.records;
        // 为每个用户添加部门名称和角色名称
        userList.value = users.map((user) => {
          const dept = deptOptions.value.find((d) => d.id === user.deptId);
          const roleNames = ["", "总经理", "部门主管", "普通员工"];
          return {
            ...user,
            deptName: dept ? dept.deptName : "未知部门",
            roleName: roleNames[user.roleId] || "未知角色",
          };
        });
        pagination.total = response.data.data.total;
      } catch (error) {
        ElMessage.error("获取用户列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 部门点击事件
    const handleDeptClick = (data) => {
      console.log("选中部门:", data);
      // 根据部门筛选用户
      searchForm.deptId = data.id;
      pagination.current = 1;
      getUserList();
    };

    // 搜索
    const handleSearch = () => {
      pagination.current = 1;
      getUserList();
    };

    // 添加用户
    const handleAdd = () => {
      activeTab.value = "add";
    };

    // 编辑用户
    const handleEdit = (row) => {
      Object.assign(userForm, row);
      activeTab.value = "add";
    };

    // 删除用户
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm("确定要删除该用户吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });

        await axios.delete(`/api/user/${row.id}`);
        ElMessage.success("删除成功");
        getUserList();
      } catch (error) {
        if (error !== "cancel") {
          ElMessage.error("删除失败");
        }
      }
    };

    // 批量删除
    const handleBatchDelete = async () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning("请选择要删除的用户");
        return;
      }

      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${selectedRows.value.length} 个用户吗？`,
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        );

        const ids = selectedRows.value.map((row) => row.id);
        await axios.delete("/api/user/batch", { data: ids });
        ElMessage.success("批量删除成功");
        getUserList();
      } catch (error) {
        if (error !== "cancel") {
          ElMessage.error("批量删除失败");
        }
      }
    };

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      selectedRows.value = selection;
    };

    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.size = size;
      getUserList();
    };

    // 当前页变化
    const handleCurrentChange = (current) => {
      pagination.current = current;
      getUserList();
    };

    // 提交表单
    const handleSubmit = async () => {
      if (!userFormRef.value) return;

      await userFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            if (userForm.id) {
              await axios.put("/api/user", userForm);
              ElMessage.success("修改成功");
            } else {
              await axios.post("/api/user", userForm);
              ElMessage.success("添加成功");
            }
            handleReset();
            activeTab.value = "list";
            getUserList();
          } catch (error) {
            ElMessage.error("操作失败");
          }
        }
      });
    };

    // 重置表单
    const handleReset = () => {
      if (userFormRef.value) {
        userFormRef.value.resetFields();
      }
      Object.keys(userForm).forEach((key) => {
        userForm[key] = "";
      });
    };

    // 批量导入相关函数
    const handleBatchImport = () => {
      importDialogVisible.value = true;
      resetImportState();
    };

    const resetImportState = () => {
      fileList.value = [];
      selectedFile.value = null;
      importProgress.show = false;
      importResult.show = false;
      importProgress.percentage = 0;
      importProgress.status = "";
      importProgress.text = "";
      importResult.errors = [];
    };

    const handleFileChange = (file, fileList) => {
      selectedFile.value = file.raw;
      fileList.value = [file];
    };

    const beforeUpload = (file) => {
      const isValidType =
        file.type ===
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ||
        file.type === "application/vnd.ms-excel" ||
        file.type === "text/plain";
      const isValidSize = file.size / 1024 / 1024 < 10;

      if (!isValidType) {
        ElMessage.error("只能上传 Excel 文件(.xlsx/.xls) 或文本文件(.txt)");
        return false;
      }
      if (!isValidSize) {
        ElMessage.error("文件大小不能超过 10MB");
        return false;
      }
      return false; // 阻止自动上传
    };

    const handleImportSubmit = async () => {
      if (!selectedFile.value) {
        ElMessage.warning("请选择要导入的文件");
        return;
      }

      const formData = new FormData();
      formData.append("file", selectedFile.value);

      try {
        importProgress.show = true;
        importProgress.percentage = 0;
        importProgress.status = "";
        importProgress.text = "正在上传文件...";

        // 模拟进度
        const progressInterval = setInterval(() => {
          if (importProgress.percentage < 90) {
            importProgress.percentage += 10;
          }
        }, 200);

        const response = await axios.post("/api/user/import", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            importProgress.percentage = percentCompleted;
          },
        });

        clearInterval(progressInterval);
        importProgress.percentage = 100;
        importProgress.text = "导入完成";

        // 显示导入结果
        const result = response.data.data;
        importResult.show = true;
        importResult.title = `导入完成`;
        importResult.type =
          result.errors && result.errors.length > 0 ? "warning" : "success";
        importResult.description = `成功导入 ${result.successCount} 条记录${
          result.errors && result.errors.length > 0
            ? `，${result.errors.length} 条记录导入失败`
            : ""
        }`;
        importResult.errors = result.errors || [];

        // 刷新用户列表
        if (result.successCount > 0) {
          getUserList();
        }

        // 延迟关闭弹窗（如果有成功导入的记录）
        if (result.successCount > 0) {
          setTimeout(() => {
            importDialogVisible.value = false;
            resetImportState();
          }, 3000);
        }
      } catch (error) {
        importProgress.show = false;
        importResult.show = true;
        importResult.title = "导入失败";
        importResult.type = "error";
        importResult.description =
          error.response?.data?.message || "导入过程中发生错误";
        ElMessage.error("导入失败");
      }
    };

    const handleImportCancel = () => {
      importDialogVisible.value = false;
      resetImportState();
    };

    const downloadTemplate = () => {
      // 创建模板数据
      const templateData = [
        ["账号", "密码", "昵称", "电话", "性别", "工号", "部门ID", "角色ID"],
        ["user001", "123456", "张三", "13800138001", "男", "E001", "1", "1"],
        ["user002", "123456", "李四", "13800138002", "女", "E002", "2", "2"],
      ];

      // 转换为CSV格式
      const csvContent = templateData.map((row) => row.join(",")).join("\n");

      // 创建下载链接
      const blob = new Blob(["\ufeff" + csvContent], {
        type: "text/csv;charset=utf-8;",
      });
      const link = document.createElement("a");
      const url = URL.createObjectURL(blob);
      link.setAttribute("href", url);
      link.setAttribute("download", "用户导入模板.csv");
      link.style.visibility = "hidden";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      ElMessage.success("模板下载成功");
    };

    onMounted(() => {
      getDeptTree();
      getUserList();
    });

    return {
      activeTab,
      loading,
      userList,
      deptTree,
      deptOptions,
      selectedRows,
      userFormRef,
      searchForm,
      userForm,
      userRules,
      pagination,
      treeProps,
      // 批量导入相关
      importDialogVisible,
      uploadRef,
      fileList,
      selectedFile,
      importProgress,
      importResult,
      handleDeptClick,
      handleSearch,
      handleAdd,
      handleEdit,
      handleDelete,
      handleBatchDelete,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      handleSubmit,
      handleReset,
      // 批量导入函数
      handleBatchImport,
      handleFileChange,
      beforeUpload,
      handleImportSubmit,
      handleImportCancel,
      downloadTemplate,
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

/* 批量导入样式 */
.import-container {
  padding: 20px 0;
}

.upload-section {
  margin-bottom: 20px;
}

.progress-section {
  margin: 20px 0;
  text-align: center;
}

.progress-text {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.result-section {
  margin: 20px 0;
}

.error-details {
  margin-top: 15px;
  padding: 15px;
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
}

.error-details h4 {
  margin: 0 0 10px 0;
  color: #f56c6c;
  font-size: 14px;
}

.error-details ul {
  margin: 0;
  padding-left: 20px;
}

.error-details li {
  margin: 5px 0;
  color: #f56c6c;
  font-size: 13px;
}

.template-section {
  margin-top: 20px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.dialog-footer {
  text-align: right;
}
</style>
