<template>
  <div class="main-content">
    <!-- 左侧空白区域 -->
    <!-- <div class="sidebar">
      <div class="tree-container">
        <div class="tree-title">发布商品</div>
        <p>发布商品功能区域</p>
      </div>
    </div> -->

    <!-- 右侧发布商品表单 -->
    <div class="content">
      <div class="search-form">
        <h2>{{ isEdit ? "编辑商品" : "发布商品" }}</h2>
        <el-form
          :model="goodsForm"
          :rules="goodsRules"
          ref="goodsFormRef"
          label-width="120px"
        >
          <el-form-item label="商品名称" prop="spuName">
            <el-input
              v-model="goodsForm.spuName"
              placeholder="请输入商品名称"
            />
          </el-form-item>
          <el-form-item label="商品描述" prop="spuDesc">
            <el-input
              v-model="goodsForm.spuDesc"
              type="textarea"
              :rows="4"
              placeholder="请输入商品描述"
            />
          </el-form-item>
          <el-form-item label="品牌" prop="brandId">
            <el-select v-model="goodsForm.brandId" placeholder="请选择品牌">
              <el-option
                v-for="brand in brandOptions"
                :key="brand.id"
                :label="brand.brandName"
                :value="brand.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="类别" prop="categoryId">
            <el-cascader
              v-model="goodsForm.categoryId"
              :options="categoryOptions"
              :props="cascaderProps"
              placeholder="请选择类别"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="商品图片" prop="spuImages">
            <el-upload
              class="image-uploader"
              :action="uploadUrl"
              :file-list="fileList"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :before-upload="beforeUpload"
              multiple
              list-type="picture-card"
            >
              <el-icon class="image-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">
              支持上传多张图片，建议尺寸：800x800px，格式：JPG/PNG，大小不超过2MB
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">{{
              isEdit ? "保存修改" : "发布商品"
            }}</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import axios from "axios";

export default {
  name: "PublishGoods",
  components: {
    Plus,
  },
  setup() {
    const goodsFormRef = ref();
    const brandOptions = ref([]);
    const categoryOptions = ref([]);
    const fileList = ref([]);
    const uploadUrl = ref("/api/upload");
    const imageUrls = ref([]);
    const isEdit = ref(false);
    const goodsId = ref(null);

    const goodsForm = reactive({
      spuName: "",
      spuDesc: "",
      brandId: "",
      categoryId: [],
      spuImages: "",
    });

    const goodsRules = {
      spuName: [{ required: true, message: "请输入商品名称", trigger: "blur" }],
      spuDesc: [{ required: true, message: "请输入商品描述", trigger: "blur" }],
      brandId: [{ required: true, message: "请选择品牌", trigger: "change" }],
      categoryId: [
        { required: true, message: "请选择类别", trigger: "change" },
      ],
      spuImages: [
        { required: true, message: "请上传商品图片", trigger: "change" },
      ],
    };

    const cascaderProps = {
      value: "id",
      label: "categoryName",
      children: "children",
      checkStrictly: true,
    };

    // 获取品牌列表
    const getBrandList = async () => {
      try {
        const response = await axios.get("/api/brand/list");
        brandOptions.value = response.data.data;
      } catch (error) {
        ElMessage.error("获取品牌列表失败");
      }
    };

    // 获取类别列表
    const getCategoryList = async () => {
      try {
        const response = await axios.get("/api/category/tree");
        categoryOptions.value = response.data.data;
      } catch (error) {
        ElMessage.error("获取类别列表失败");
      }
    };

    // 获取商品详情
    const getGoodsDetail = async (id) => {
      try {
        const response = await axios.get(`/api/goods/${id}`);
        const goods = response.data.data;
        if (goods) {
          goodsForm.spuName = goods.spuName;
          goodsForm.spuDesc = goods.spuDesc;
          goodsForm.brandId = goods.brandId;

          // 根据分类层级设置categoryId
          const categoryPath = findCategoryPath(
            goods.categoryId,
            categoryOptions.value
          );
          goodsForm.categoryId = categoryPath || [goods.categoryId];

          goodsForm.spuImages = goods.spuImages || "";

          // 处理图片
          if (goods.spuImages) {
            const images = goods.spuImages
              .split(",")
              .filter((img) => img.trim());
            imageUrls.value = images;
            fileList.value = images.map((url, index) => ({
              name: `image${index + 1}`,
              url: url,
              response: { data: url },
            }));
          }
        }
      } catch (error) {
        ElMessage.error("获取商品详情失败");
      }
    };

    // 查找分类路径
    const findCategoryPath = (categoryId, categories) => {
      for (const category of categories) {
        if (category.id === categoryId) {
          return [categoryId];
        }
        if (category.children && category.children.length > 0) {
          const childPath = findCategoryPath(categoryId, category.children);
          if (childPath) {
            return [category.id, ...childPath];
          }
        }
      }
      return null;
    };

    // 初始化编辑模式
    const initEditMode = async () => {
      const urlParams = new URLSearchParams(window.location.search);
      const id = urlParams.get("id");
      if (id) {
        isEdit.value = true;
        goodsId.value = id;
        // 确保分类数据加载完成后再获取商品详情
        await getCategoryList();
        await getGoodsDetail(id);
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
    const handleUploadSuccess = (response, file) => {
      if (response.code === 200) {
        imageUrls.value.push(response.data);
        goodsForm.spuImages = imageUrls.value.join(",");
        ElMessage.success("上传成功");
      } else {
        ElMessage.error("上传失败");
      }
    };

    // 文件移除
    const handleRemove = (file) => {
      const url = file.response?.data || file.url;
      const index = imageUrls.value.findIndex((img) => img === url);
      if (index > -1) {
        imageUrls.value.splice(index, 1);
        goodsForm.spuImages = imageUrls.value.join(",");
      }
    };

    // 提交表单
    const handleSubmit = async () => {
      if (!goodsFormRef.value) return;

      await goodsFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const formData = {
              ...goodsForm,
              categoryId: goodsForm.categoryId[goodsForm.categoryId.length - 1],
              status: 0, // 默认未发布状态
            };

            if (isEdit.value) {
              formData.id = goodsId.value;
              await axios.put("/api/goods", formData);
              ElMessage.success("修改成功");
            } else {
              await axios.post("/api/goods", formData);
              ElMessage.success("发布成功");
            }
            handleReset();
          } catch (error) {
            ElMessage.error(isEdit.value ? "修改失败" : "发布失败");
          }
        }
      });
    };

    // 重置表单
    const handleReset = () => {
      if (goodsFormRef.value) {
        goodsFormRef.value.resetFields();
      }
      Object.assign(goodsForm, {
        spuName: "",
        spuDesc: "",
        brandId: "",
        categoryId: [],
        spuImages: "",
      });
      fileList.value = [];
      imageUrls.value = [];
    };

    // 取消
    const handleCancel = () => {
      window.history.back();
    };

    onMounted(async () => {
      await getBrandList();
      await getCategoryList();
      await initEditMode();
    });

    return {
      goodsFormRef,
      brandOptions,
      categoryOptions,
      fileList,
      uploadUrl,
      goodsForm,
      goodsRules,
      cascaderProps,
      isEdit,
      beforeUpload,
      handleUploadSuccess,
      handleRemove,
      handleSubmit,
      handleReset,
      handleCancel,
    };
  },
};
</script>

<style scoped>
.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: 0.2s;
}

.image-uploader:hover {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>
