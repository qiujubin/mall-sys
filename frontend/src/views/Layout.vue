<template>
  <div class="page-container">
    <el-container class="layout-container">
      <!-- 左侧侧边栏 -->
      <el-aside class="sidebar" :width="isCollapsed ? '64px' : '200px'">
        <!-- 折叠按钮 -->
        <div
          class="toggle-btn"
          :class="{ 'toggle-btn-collapsed': isCollapsed }"
          @click="toggleCollapse"
        >
          <span class="title"> Mall-Sys </span>
        </div>
        <el-menu
          :default-active="activeMenu"
          @select="handleMenuSelect"
          :collapse="isCollapsed"
          class="sidebar-menu"
          :collapse-transition="true"
          unique-opened
        >
          <el-menu-item index="/login" :width="isCollapsed ? '64px' : '200px'">
            <el-icon><Avatar /></el-icon>
            <span>登录页</span>
          </el-menu-item>
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/dept">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="/category">
            <el-icon><Van /></el-icon>
            <span>商品类别</span>
          </el-menu-item>
          <el-menu-item index="/brand">
            <el-icon><ShoppingBag /></el-icon>
            <span>品牌管理</span>
          </el-menu-item>
          <el-menu-item index="/attr-group">
            <el-icon><SetUp /></el-icon>
            <span>属性分组</span>
          </el-menu-item>
          <el-menu-item index="/attr">
            <el-icon><List /></el-icon>
            <span>规格参数</span>
          </el-menu-item>
          <el-menu-item index="/sale-attr">
            <el-icon><PriceTag /></el-icon>
            <span>销售属性</span>
          </el-menu-item>
          <el-menu-item index="/goods">
            <el-icon><ShoppingCart /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/publish-goods">
            <el-icon><Edit /></el-icon>
            <span>发布商品</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  Fold,
  Expand,
  Van,
  Avatar,
  User,
  OfficeBuilding,
  ShoppingBag,
  SetUp,
  List,
  PriceTag,
  ShoppingCart,
  Edit,
} from "@element-plus/icons-vue";

export default {
  name: "Layout",
  components: {
    Fold,
    Expand,
    Van,
    Avatar,
    User,
    OfficeBuilding,
    ShoppingBag,
    SetUp,
    List,
    PriceTag,
    ShoppingCart,
    Edit,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const activeMenu = ref(route.path);
    const isCollapsed = ref(false);

    const handleMenuSelect = (index) => {
      if (index === "/login") {
        // 清除所有登录信息
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        // 跳转到登录页
        router.push("/login");
      } else {
        router.push(index);
      }
    };

    const toggleCollapse = () => {
      isCollapsed.value = !isCollapsed.value;
    };

    watch(
      () => route.path,
      (newPath) => {
        activeMenu.value = newPath;
      }
    );

    return {
      activeMenu,
      isCollapsed,
      handleMenuSelect,
      toggleCollapse,
    };
  },
};
</script>

<style scoped>
.title {
  color: darkblue;
  font-size: 26px;
}
.page-container {
  height: 100vh;
}

.layout-container {
  height: 100%;
}

.sidebar {
  width: auto;
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  transition: width 0.3s;
}

.toggle-btn {
  height: 60px;
  width: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #e6e6e6;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
}

.toggle-btn-collapsed {
  justify-content: center;
}

.toggle-btn:hover {
  background-color: #f5f7fa;
}

.sidebar-menu {
  width: 160px;
  min-width: a !important;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
}

.main-content {
  padding: 5px 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
