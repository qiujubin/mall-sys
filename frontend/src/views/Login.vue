<template>
  <div class="login-container">
    <div class="login-form">
      <h2 class="login-title">商品品牌管理系统</h2>
      <el-form
        :model="loginForm"
        :rules="rules"
        ref="loginFormRef"
        label-width="80px"
      >
        <el-form-item label="账号" prop="account" class="form-item">
          <el-input v-model="loginForm.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" class="form-item">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item class="form-item">
          <el-button
            type="primary"
            @click="handleLogin"
            class="login-button"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

export default {
  name: "Login",
  setup() {
    const router = useRouter();
    const loginFormRef = ref();
    const loading = ref(false);

    const loginForm = reactive({
      account: "",
      password: "",
    });

    const rules = {
      account: [{ required: true, message: "请输入账号", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    };

    const handleLogin = async () => {
      if (!loginFormRef.value) return;

      await loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            const response = await fetch("/api/auth/login", {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify({
                account: loginForm.account,
                password: loginForm.password,
              }),
            });

            const result = await response.json();

            if (result.code === 200) {
              ElMessage.success("登录成功");
              localStorage.setItem("user", JSON.stringify(result.data.user));
              router.push("/");
            } else {
              ElMessage.error(result.message || "登录失败");
            }
          } catch (error) {
            console.error("登录错误:", error);
            ElMessage.error("登录失败，请检查网络连接");
          } finally {
            loading.value = false;
          }
        }
      });
    };

    return {
      loginFormRef,
      loginForm,
      rules,
      loading,
      handleLogin,
    };
  },
};
</script>

<style>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  background-image: url(../utils/img/0.jpg);
  /* background-image: url(https://cdn.seovx.com/?mom=302); */
  /* background-image: url(https://api.btstu.cn/sjbz/api.php?lx=fengjing&format=images); */
  /* background-image: url(https://www.dmoe.cc/random.php); */
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  overflow: hidden;
}
</style>
