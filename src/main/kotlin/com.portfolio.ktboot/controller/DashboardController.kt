package com.portfolio.ktboot.controller


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.MemberEntity
import com.portfolio.ktboot.orm.jpa.MemberRepository
import com.portfolio.ktboot.service.DashboardService
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.MemberService
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.apache.poi.ss.formula.functions.T
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/dashboard") // API 요청을 위한 기본 경로
class DashboardController (
    private val dashboardService: DashboardService
){

    /*
    @GetMapping("/mem-new")
    fun momNew(): Int {
        return dashboardService.todayNewMemberCount()
    }

    @GetMapping("/mem-mom-stats")
    fun momNewMemberStats(): Int {
        return dashboardService.monthlyNewMemberStats()
    }

    @GetMapping("/mem-mom-stat-json")
    fun momNewMemberStatsJson(): List<Map<String, Any>> {
        return dashboardService.monthlyMemberStatsJson()
    }
    */

}
