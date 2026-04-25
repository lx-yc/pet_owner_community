import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/post/:id', // 动态路由参数
        name: 'PostDetail',
        component: () => import('../views/Post.vue'),
    },
    {
        path: '/submit',
        name: 'SubmitPost',
        component: () => import('../views/SubmitPost.vue'),
    },
    {
        path: '/',
        component: () => import('../views/Home.vue'),
        redirect: '/community',
        children: [
            {
                path: 'community',
                name: 'Community',
                component: () => import('../views/CommunityPosts.vue'),
            },
            {
                path: 'authority',
                name: 'Authority',
                component: () => import('../views/Authority.vue'),
            },
            {
                path: 'pets',
                name: 'Pets',
                component: () => import('../views/Pets.vue'),
            },

        ],
    },
    {
        path: '/user/:id',
        component: () => import('../views/PersonalHome.vue'),
        redirect: (to) => `/user/${to.params.id}/posts`,
        children: [
            {
                path: 'posts',
                name: 'UserPosts',
                component: () => import('../views/personal/PersonalPosts.vue'),
            },
            {
                path: 'likes',
                name: 'UserLikes',
                component: () => import('../views/personal/PersonalLikes.vue'),
            },
            {
                path: 'favorites',
                name: 'UserFavorites',
                component: () => import('../views/personal/PersonalFavorite.vue'),
            },
            {
                path: 'following',
                name: 'UserFollowing',
                component: () => import('../views/personal/UserList.vue'),
                props: { listType: 'following' }
            },
            {
                path: 'followers',
                name: 'UserFollowers',
                component: () => import('../views/personal/UserList.vue'),
                props: { listType: 'followers' }
            },
        ]
    },
    {
        path: '/personalHome',
        component: () => import('../views/PersonalHome.vue'),
        redirect: '/personalHome/personalPosts',
        children: [
            {
                path: 'personalPosts',
                name: 'PersonalPosts',
                component: () => import('../views/personal/PersonalPosts.vue'),
            },
            {
                path: 'personalLikes',
                name: 'PersonalLikes',
                component: () => import('../views/personal/PersonalLikes.vue'),
            },
            {
                path: 'personalFavorite',
                name: 'PersonalFavorite',
                component: () => import('../views/personal/PersonalFavorite.vue'),
            },
            {
                path: 'following',
                name: 'MyFollowing',
                component: () => import('../views/personal/UserList.vue'),
                props: { listType: 'following' }
            },
            {
                path: 'followers',
                name: 'MyFollowers',
                component: () => import('../views/personal/UserList.vue'),
                props: { listType: 'followers' }
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router