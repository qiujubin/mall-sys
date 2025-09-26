import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'

const routes = [{
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/',
        component: Layout,
        redirect: '/user',
        meta: { requiresAuth: true },
        children: [{
                path: '/user',
                name: 'User',
                component: () =>
                    import ('@/views/User.vue')
            },
            {
                path: '/dept',
                name: 'Dept',
                component: () =>
                    import ('@/views/Dept.vue')
            },
            {
                path: '/category',
                name: 'Category',
                component: () =>
                    import ('@/views/Category.vue')
            },
            {
                path: '/brand',
                name: 'Brand',
                component: () =>
                    import ('@/views/Brand.vue')
            },
            {
                path: '/attr-group',
                name: 'AttrGroup',
                component: () =>
                    import ('@/views/AttrGroup.vue')
            },
            {
                path: '/attr',
                name: 'Attr',
                component: () =>
                    import ('@/views/Attr.vue')
            },
            {
                path: '/sale-attr',
                name: 'SaleAttr',
                component: () =>
                    import ('@/views/SaleAttr.vue')
            },
            {
                path: '/goods',
                name: 'Goods',
                component: () =>
                    import ('@/views/Goods.vue')
            },
            {
                path: '/publish-goods',
                name: 'PublishGoods',
                component: () =>
                    import ('@/views/PublishGoods.vue')
            },

        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const user = localStorage.getItem('user')

    if (to.meta.requiresAuth && !user) {
        // 需要登录但未登录，跳转到登录页
        next('/login')
    } else if (to.path === '/login' && user) {
        // 已登录但访问登录页，跳转到首页
        next('/')
    } else {
        next()
    }
})

export default router
