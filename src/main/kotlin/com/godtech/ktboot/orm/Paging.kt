package com.godtech.ktboot.form

import com.godtech.ktboot.form.ListForm
import org.jooq.*
import org.jooq.DSLContext
import org.jooq.SelectLimitStep
import org.springframework.data.domain.Page
import kotlin.collections.map
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class Details(
    val isLast: Boolean,
    val offset: Long,
    val previous: Int,
    val hasPrevious: Boolean,
    val next: Int,
    val hasNext: Boolean,
    val totalPages: Int,
) {
    companion object {
        fun empty() = Details(false, 0, 0, false, 0, false, 0)
    }
}

data class ListPagination<out E>(
    val total: Int,
    val contents: List<E>,
    val details: Details,
) {
    companion object {
        fun <R : Record, E> of(
            dsl: DSLContext,
            query: SelectLimitStep<R>,
            form: ListForm,
            map: (record: R) -> E
        ): ListPagination<E> {
            val total = dsl.fetchCount(query)
            val info = info(total, form.page, form.size)
            val contents = query.limit(info.offset, form.size).filterNotNull().map(map)

            return ListPagination(total, contents, info)
        }

        private fun info(
            total: Int,
            forPage: Int,
            perPage: Int,
        ): Details {
            val totalPages = if (perPage == 0) 1 else ceil(total.toDouble() / perPage).toInt()
            val lastPage = if (total == 0) 1 else totalPages
            val page = forPage.coerceIn(1, lastPage)
            val offset = ((page - 1) * perPage).toLong()

            val previous = max(page - 1, 1)
            val next = min(page + 1, lastPage)

            return Details(
                isLast = page == lastPage,
                offset = offset,
                previous = previous,
                hasPrevious = previous != page,
                next = next,
                hasNext = next != page,
                totalPages = totalPages,
            )
        }
    }

    inline fun <R> map(transform: (E) -> R): ListPagination<R> = ListPagination(total, contents.map(transform), details)
}

fun <T> Page<T>.toListPagination(): ListPagination<T> =
    ListPagination(
        total = totalElements.toInt(),
        contents = content,
        details =
        Details(
            isLast = isLast,
            offset = pageable.offset,
            previous = if (hasPrevious()) number else number + 1,
            hasPrevious = hasPrevious(),
            next = if (hasNext()) number + 2 else number + 1,
            hasNext = hasNext(),
            totalPages = totalPages,
        ),
    )
