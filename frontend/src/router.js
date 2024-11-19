
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import RentalRentalManager from "./components/listers/RentalRentalCards"
import RentalRentalDetail from "./components/listers/RentalRentalDetail"

import BookBookManager from "./components/listers/BookBookCards"
import BookBookDetail from "./components/listers/BookBookDetail"

import MemberMemberManager from "./components/listers/MemberMemberCards"
import MemberMemberDetail from "./components/listers/MemberMemberDetail"



export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/rentals/rentals',
                name: 'RentalRentalManager',
                component: RentalRentalManager
            },
            {
                path: '/rentals/rentals/:id',
                name: 'RentalRentalDetail',
                component: RentalRentalDetail
            },

            {
                path: '/books/books',
                name: 'BookBookManager',
                component: BookBookManager
            },
            {
                path: '/books/books/:id',
                name: 'BookBookDetail',
                component: BookBookDetail
            },

            {
                path: '/members/members',
                name: 'MemberMemberManager',
                component: MemberMemberManager
            },
            {
                path: '/members/members/:id',
                name: 'MemberMemberDetail',
                component: MemberMemberDetail
            },




    ]
})
