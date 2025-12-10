package com.portfolio.ktboot.service

import com.portfolio.ktboot.form.*
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class ExcelService() {

    fun responseSetting(response: HttpServletResponse, fileName: String): HttpServletResponse {
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

        val encoded = java.net.URLEncoder.encode(fileName, "UTF-8")
                .replace("+", "%20") // 중요: 스페이스를 +가 아니라 %20 으로 변경

        response.setHeader(
                "Content-Disposition",
                "attachment; filename*=UTF-8''${encoded}.xlsx"
        )
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition")

        return response
    }

    // ⭐ 날짜 스타일 생성 함수
    private fun XSSFWorkbook.createDateCellStyle(format: String = "yyyy-MM-dd HH:mm:ss"): CellStyle {
        val createHelper = this.creationHelper
        val style = this.createCellStyle()
        style.dataFormat = createHelper.createDataFormat().getFormat(format)
        return style
    }

    // ⭐ 날짜 셀 처리 함수
    private fun org.apache.poi.ss.usermodel.Cell.setDateValue(dateValue: Any?, dateCellStyle: CellStyle) {
        when (dateValue) {
            is LocalDateTime -> {
                // LocalDateTime -> Date 변환
                val date = Date.from(dateValue.atZone(ZoneId.systemDefault()).toInstant())
                this.setCellValue(date)
                this.cellStyle = dateCellStyle
            }
            is String -> {
                // 문자열인 경우 그대로 출력
                this.setCellValue(dateValue)
            }
            else -> {
                // 기타 타입은 toString()
                this.setCellValue(dateValue?.toString() ?: "")
            }
        }
    }

    fun memberExcelDownload(data: ListPagination<MemberList>, response: HttpServletResponse, fileName: String) {

        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

            // ⭐ 날짜 스타일 생성
            val dateCellStyle = workbook.createDateCellStyle()

            // 헤더
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("ID")
            header.createCell(1).setCellValue("이름")
            header.createCell(2).setCellValue("성별")
            header.createCell(3).setCellValue("가입일자")

            // 데이터 채우기
            data.contents.forEachIndexed { index, value ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(value.id)
                row.createCell(1).setCellValue(value.name)
                row.createCell(2).setCellValue(value.gender.toString())

                // ⭐ 날짜 셀 처리 (함수 사용)
                row.createCell(3).setDateValue(value.createdAt, dateCellStyle)
            }

            // 컬럼 너비 자동 조정
            for (i in 0..3) {
                sheet.autoSizeColumn(i)
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512)
            }

            responseSetting(response, fileName)
            workbook.write(response.outputStream)
        }

    }

    fun productExcelDownload(data: ListPagination<ProductList>, response: HttpServletResponse, fileName: String) {

        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

            // ⭐ 날짜 스타일 생성
            val dateCellStyle = workbook.createDateCellStyle()

            // 헤더
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("상품번호")
            header.createCell(1).setCellValue("상품이름")
            header.createCell(2).setCellValue("상품가격")
            header.createCell(3).setCellValue("등록일자")

            // 데이터 채우기
            data.contents.forEachIndexed { index, value ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(value.idx.toString())
                row.createCell(1).setCellValue(value.name)
                row.createCell(2).setCellValue(value.price.toString())

                // ⭐ 날짜 셀 처리 (함수 사용)
                row.createCell(3).setDateValue(value.createdAt, dateCellStyle)
            }

            // 컬럼 너비 자동 조정
            for (i in 0..3) {
                sheet.autoSizeColumn(i)
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512)
            }

            responseSetting(response, fileName)
            workbook.write(response.outputStream)
        }

    }

    fun postExcelDownload(data: ListPagination<PostList>, response: HttpServletResponse, fileName: String) {

        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

            // ⭐ 날짜 스타일 생성
            val dateCellStyle = workbook.createDateCellStyle()

            // 헤더
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("번호")
            header.createCell(1).setCellValue("카테고리")
            header.createCell(2).setCellValue("제목")
            header.createCell(3).setCellValue("부제목")
            header.createCell(4).setCellValue("컨텐츠")
            header.createCell(5).setCellValue("등록일자")

            // 데이터 채우기
            data.contents.forEachIndexed { index, value ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(value.idx.toString())
                row.createCell(1).setCellValue(value.category)
                row.createCell(2).setCellValue(value.title)
                row.createCell(3).setCellValue(value.subtitle)
                row.createCell(4).setCellValue(value.content)

                // ⭐ 날짜 셀 처리 (함수 사용)
                row.createCell(5).setDateValue(value.createdAt, dateCellStyle)
            }

            // 컬럼 너비 자동 조정
            for (i in 0..5) {
                sheet.autoSizeColumn(i)
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512)
            }

            responseSetting(response, fileName)
            workbook.write(response.outputStream)
        }

    }

    fun alloyExcelDownload(data: ListPagination<AlloyList>, response: HttpServletResponse, fileName: String) {

        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

            // ⭐ 날짜 스타일 생성
            val dateCellStyle = workbook.createDateCellStyle()

            // 헤더
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("번호")
            header.createCell(1).setCellValue("타입")
            header.createCell(2).setCellValue("제목")
            header.createCell(3).setCellValue("부제목")
            header.createCell(4).setCellValue("컨텐츠")
            header.createCell(5).setCellValue("등록일자")

            // 데이터 채우기
            data.contents.forEachIndexed { index, value ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(value.idx.toString())
                row.createCell(1).setCellValue(value.type)
                row.createCell(2).setCellValue(value.title)
                row.createCell(3).setCellValue(value.subtitle)
                row.createCell(4).setCellValue(value.content)

                // ⭐ 날짜 셀 처리 (함수 사용)
                row.createCell(5).setDateValue(value.createdAt, dateCellStyle)
            }

            // 컬럼 너비 자동 조정
            for (i in 0..5) {
                sheet.autoSizeColumn(i)
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512)
            }

            responseSetting(response, fileName)
            workbook.write(response.outputStream)
        }

    }

}