package com.portfolio.ktboot.form

open class ListForm {
    var page: Int = 1
    var size: Int = 10
    var sortDesc: String = ""
    var sortBy: String = ""

    companion object {
        const val DEFAULT_PAGE = 1
        const val MAX_SIZE = 30
        const val MIN_SIZE = 1
    }
}
