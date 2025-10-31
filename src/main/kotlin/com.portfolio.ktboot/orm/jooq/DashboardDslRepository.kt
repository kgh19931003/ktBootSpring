package com.portfolio.ktboot.orm.jooq


import com.portfolio.ktboot.form.ListPagination
import com.portfolio.ktboot.form.MemberSearchForm
import com.portfolio.ktboot.jooq.portfolio.tables.references.MEMBER
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Repository
class DashboardDslRepository(
    private val dsl: DSLContext,
) {

    /*
    fun getTodayNewMemberCount(): Int {
        val today = java.time.LocalDate.now()
        val startOfToday = java.sql.Timestamp.valueOf(today.atStartOfDay()).toString()
        val startOfTomorrow = java.sql.Timestamp.valueOf(today.plusDays(1).atStartOfDay()).toString()

        return dsl.selectCount()
                .from(MEMBER)
                .where(MEMBER.CREATED_AT.between(startOfToday, startOfTomorrow))
                .fetchOne(0, Int::class.java) ?: 0
    }



    fun getMonthlyMemberStatsJson(): List<Map<String, Any>> {
        val months = (0 until 12).map { i ->
            LocalDate.now().minusMonths(i.toInt()).withDayOfMonth(1)
        }.reversed() // 과거 → 현재

        val selects: List<Select<Record1<String>>> = months.map { month ->
            DSL.select(DSL.`val`(month.format(DateTimeFormatter.ofPattern("yyyy-MM"))).`as`("month"))
        }

        val union: Select<Record1<String>> = selects.reduce { acc, select -> acc.unionAll(select) }

        val monthTable = union.asTable("month_table")
        val monthField = monthTable.field("month", String::class.java)?.`as`("month")!!

        val formattedCreatedAt = DSL.field("DATE_FORMAT({0}, '%Y-%m')", String::class.java, MEMBER.CREATED_AT)

        val result = dsl
                .select(
                        monthField,
                        DSL.count(MEMBER.IDX).`as`("count")
                )
                .from(monthTable)
                .leftJoin(MEMBER).on(formattedCreatedAt.eq(monthField))
                .groupBy(monthField)
                .orderBy(monthField)
                .fetch()

        return result.map {
            mapOf(
                    "month" to (it.get(monthField) ?: ""),
                    "count" to (it.get("count", Int::class.java) ?: 0)
            )
        }
    }




    fun getMonthlyNewMemberStats(): Pair<Int, Int> {
        val now = java.time.LocalDate.now()

        val firstDayThisMonth = java.sql.Date.valueOf(now.withDayOfMonth(1))
        val firstDayNextMonth = java.sql.Date.valueOf(now.plusMonths(1).withDayOfMonth(1))
        val firstDayLastMonth = java.sql.Date.valueOf(now.minusMonths(1).withDayOfMonth(1))

        val thisMonthCount = dsl.selectCount()
                .from(MEMBER)
                .where(
                        MEMBER.CREATED_AT.ge(firstDayThisMonth.toString())
                                .and(MEMBER.CREATED_AT.lt(firstDayNextMonth.toString()))
                )
                .fetchOne(0, Int::class.java) ?: 0

        val lastMonthCount = dsl.selectCount()
                .from(MEMBER)
                .where(
                        MEMBER.CREATED_AT.ge(firstDayLastMonth.toString())
                                .and(MEMBER.CREATED_AT.lt(firstDayThisMonth.toString()))
                )
                .fetchOne(0, Int::class.java) ?: 0

        println("thisMonthCount : $thisMonthCount")
        println("lastMonthCount : $lastMonthCount")

        return Pair(thisMonthCount, lastMonthCount)
    }

*/


}