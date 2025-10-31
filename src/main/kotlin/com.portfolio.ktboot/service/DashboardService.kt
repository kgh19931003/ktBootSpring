package com.portfolio.ktboot.service



import com.portfolio.ktboot.form.ListPagination
import com.portfolio.ktboot.form.MemberList
import com.portfolio.ktboot.form.MemberSearchForm
import com.portfolio.ktboot.orm.jooq.DashboardDslRepository
import com.portfolio.ktboot.orm.jooq.MemberDslRepository
import com.portfolio.ktboot.orm.jpa.MemberEntity
import com.portfolio.ktboot.orm.jpa.MemberRepository
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class DashboardService(
    private val dashboardDslRepository: DashboardDslRepository
) {

    /*
    /**
     * 저번달과 비교한 신규회원자수
     */
    fun todayNewMemberCount(): Int {
        return try {
            dashboardDslRepository.getTodayNewMemberCount()
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     * 저번달과 비교한 신규회원자수 퍼센테이지
     */
    fun monthlyNewMemberStats(): Int {
        return try {
            dashboardDslRepository.getMonthlyNewMemberStats().let{
                (thisMonth, lastMonth) ->
                if (lastMonth == 0) 100 else ((thisMonth - lastMonth).toDouble() / lastMonth * 100).roundToInt()
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     * 신규가입자 월별 차트
     */
    fun monthlyMemberStatsJson(): List<Map<String, Any>> {
        return try {
            dashboardDslRepository.getMonthlyMemberStatsJson()
        } catch (e: Exception) {
            throw Exception(e)
        }
    }
*/

}