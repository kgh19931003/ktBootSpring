package com.portfolio.ktboot.cron

import com.portfolio.ktboot.orm.jpa.BlogRepository
import com.portfolio.ktboot.service.BlogService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ScheduledTasks(
        private val blogService: BlogService,
        private val blogRepository: BlogRepository
) {

    // 매 5분마다 실행
    @Scheduled(cron = "0 0 0 * * *")
    fun arrangeEditorImage() {
        println("크론잡 실행됨: ${LocalDateTime.now()}")
    }

}
