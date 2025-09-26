<template>
  <div class="main-content">
    <!-- 左侧空白区域 -->
    <!-- <div class="sidebar">
      <div class="tree-container">
        <div class="tree-title">品牌管理</div>
        <p>品牌管理功能区域</p>
      </div>
    </div> -->

    <!-- 右侧品牌列表 -->
    <div class="content">
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="品牌名称">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入品牌名称"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="primary" @click="handleAdd">添加品牌</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 品牌表格 -->
      <div class="table-container">
        <div class="table-header">
          <div class="table-title">品牌管理</div>
        </div>
        <el-table :data="brandList" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="编号" width="80" />
          <el-table-column prop="brandName" label="品牌名称" width="150" />
          <el-table-column prop="brandLogo" label="品牌LOGO" width="120">
            <template #default="scope">
              <el-image
                v-if="scope.row.brandLogo"
                :src="scope.row.brandLogo"
                style="width: 60px; height: 60px"
                fit="cover"
                :preview-src-list="[scope.row.brandLogo]"
              />
              <span v-else>暂无图片</span>
            </template>
          </el-table-column>
          <el-table-column prop="brandDesc" label="品牌描述" width="200" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? "启用" : "禁用" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200">
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
    </div>

    <!-- 添加/编辑品牌弹窗 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        :model="brandForm"
        :rules="brandRules"
        ref="brandFormRef"
        label-width="100px"
      >
        <el-form-item label="品牌名称" prop="brandName">
          <el-input
            v-model="brandForm.brandName"
            placeholder="请输入品牌名称"
          />
        </el-form-item>
        <el-form-item label="品牌LOGO" prop="brandLogo">
          <el-upload
            class="logo-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <el-image
              v-if="brandForm.brandLogo"
              :src="brandForm.brandLogo"
              class="logo-preview"
            />
            <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="品牌描述" prop="brandDesc">
          <el-input
            v-model="brandForm.brandDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入品牌描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="brandForm.status" />
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
import { Plus } from "@element-plus/icons-vue";
import axios from "axios";

export default {
  name: "Brand",
  components: {
    Plus,
  },
  setup() {
    const loading = ref(false);
    const brandList = ref([]);
    const dialogVisible = ref(false);
    const dialogTitle = ref("添加品牌");
    const brandFormRef = ref();
    const uploadUrl = ref("/api/upload");

    const searchForm = reactive({
      keyword: "",
    });

    const brandForm = reactive({
      brandName: "",
      brandLogo: "",
      brandDesc: "",
      status: true,
    });

    const brandRules = {
      brandName: [
        { required: true, message: "请输入品牌名称", trigger: "blur" },
      ],
      brandLogo: [
        { required: true, message: "请上传品牌LOGO", trigger: "change" },
      ],
    };

    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0,
    });

    // 获取品牌列表
    const getBrandList = async () => {
      loading.value = true;
      try {
        const response = await axios.post("/api/brand/page", {
          current: pagination.current,
          size: pagination.size,
          keyword: searchForm.keyword,
        });
        brandList.value = response.data.data.records;
        pagination.total = response.data.data.total;
      } catch (error) {
        ElMessage.error("获取品牌列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 搜索
    const handleSearch = () => {
      pagination.current = 1;
      getBrandList();
    };

    // 添加品牌
    const handleAdd = () => {
      dialogTitle.value = "添加品牌";
      dialogVisible.value = true;
    };

    // 编辑品牌
    const handleEdit = (row) => {
      dialogTitle.value = "编辑品牌";
      Object.assign(brandForm, {
        ...row,
        status: row.status === 1,
      });
      dialogVisible.value = true;
    };

    // 删除品牌
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm("确定要删除该品牌吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });

        const response = await axios.delete(`/api/brand/${row.id}`);
        if (response.data.code === 200) {
          ElMessage.success("删除成功");
          getBrandList();
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

    // 文件上传前验证
    const beforeUpload = (file) => {
      const isJPG = file.type === "image/jpeg" || file.type === "image/png";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        ElMessage.error("上传图片只能是 JPG/PNG 格式!");
        return false;
      }
      if (!isLt2M) {
        ElMessage.error("上传图片大小不能超过 2MB!");
        return false;
      }
      return true;
    };

    // 文件上传成功
    const handleUploadSuccess = (response) => {
      if (response.code === 200) {
        brandForm.brandLogo = response.data;
        ElMessage.success("上传成功");
      } else {
        ElMessage.error("上传失败");
      }
    };

    // 分页大小变化
    const handleSizeChange = (size) => {
      pagination.size = size;
      getBrandList();
    };

    // 当前页变化
    const handleCurrentChange = (current) => {
      pagination.current = current;
      getBrandList();
    };

    // 提交表单
    const handleSubmit = async () => {
      if (!brandFormRef.value) return;

      // 检查是否上传了图片
      if (!brandForm.brandLogo) {
        ElMessage.error("请先上传品牌LOGO");
        return;
      }

      await brandFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            // 转换状态字段为Integer类型
            const submitData = {
              ...brandForm,
              status: brandForm.status ? 1 : 0,
            };

            if (brandForm.id) {
              await axios.put("/api/brand", submitData);
              ElMessage.success("修改成功");
            } else {
              await axios.post("/api/brand", submitData);
              ElMessage.success("添加成功");
            }
            dialogVisible.value = false;
            getBrandList();
          } catch (error) {
            ElMessage.error("操作失败");
          }
        }
      });
    };

    // 弹窗关闭
    const handleDialogClose = () => {
      if (brandFormRef.value) {
        brandFormRef.value.resetFields();
      }
      Object.assign(brandForm, {
        brandName: "",
        brandLogo: "",
        brandDesc: "",
        status: true,
      });
    };

    onMounted(() => {
      getBrandList();
    });

    return {
      loading,
      brandList,
      dialogVisible,
      dialogTitle,
      brandFormRef,
      uploadUrl,
      searchForm,
      brandForm,
      brandRules,
      pagination,
      handleSearch,
      handleAdd,
      handleEdit,
      handleDelete,
      beforeUpload,
      handleUploadSuccess,
      handleSizeChange,
      handleCurrentChange,
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

.logo-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: 0.2s;
}

.logo-uploader:hover {
  border-color: #409eff;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.logo-preview {
  width: 100px;
  height: 100px;
  display: block;
}
</style>
