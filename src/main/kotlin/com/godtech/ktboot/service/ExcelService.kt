package com.godtech.ktboot.service

import com.godtech.ktboot.form.*
import com.godtech.ktboot.orm.jpa.MemberRepository
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class ExcelService() {

    fun responseSetting(response: HttpServletResponse, fileName: String): HttpServletResponse {
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        // val fileName = URLEncoder.encode(fileName+".xlsx", "UTF-8")
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''${URLEncoder.encode(fileName+".xlsx", "UTF-8")}")
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition")

        return response
    }

    fun memberExcelDownload(data: ListPagination<MemberList>, response: HttpServletResponse, fileName: String) {

        // ✔ 매 요청마다 새로운 워크북 생성
        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

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
                row.createCell(3).setCellValue(value.createdAt)
            }

            // 응답 헤더 설정
            responseSetting(response, fileName)

            // 배열 쓰기 → 스트림 flush → close 자동 실행
            workbook.write(response.outputStream)
            // `use` 블록 종료 시 자동 close()
        }

    }



    fun productExcelDownload(data: ListPagination<ProductList>, response: HttpServletResponse, fileName: String) {

        // ✔ 매 요청마다 새로운 워크북 생성
        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

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
                row.createCell(3).setCellValue(value.createdAt)
            }

            // 응답 헤더 설정
            responseSetting(response, fileName)

            // 배열 쓰기 → 스트림 flush → close 자동 실행
            workbook.write(response.outputStream)
            // `use` 블록 종료 시 자동 close()
        }

    }


    fun performanceExcelDownload(data: ListPagination<PerformanceList>, response: HttpServletResponse, fileName: String) {

        // ✔ 매 요청마다 새로운 워크북 생성
        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

            // 헤더
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("번호")
            header.createCell(1).setCellValue("카테고리")
            header.createCell(2).setCellValue("제목")
            header.createCell(3).setCellValue("부제목")
            header.createCell(4).setCellValue("컨텐츠")
            header.createCell(4).setCellValue("등록일자")

            // 데이터 채우기
            data.contents.forEachIndexed { index, value ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(value.idx.toString())
                row.createCell(1).setCellValue(value.category)
                row.createCell(2).setCellValue(value.title)
                row.createCell(3).setCellValue(value.subtitle)
                row.createCell(4).setCellValue(value.content)
                row.createCell(5).setCellValue(value.createdAt)
            }

            // 응답 헤더 설정
            responseSetting(response, fileName)

            // 배열 쓰기 → 스트림 flush → close 자동 실행
            workbook.write(response.outputStream)
            // `use` 블록 종료 시 자동 close()
        }

    }


    fun alloyExcelDownload(data: ListPagination<AlloyList>, response: HttpServletResponse, fileName: String) {

        // ✔ 매 요청마다 새로운 워크북 생성
        XSSFWorkbook().use { workbook ->
            val sheet = workbook.createSheet(fileName)

            // 헤더
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("번호")
            header.createCell(1).setCellValue("타입")
            header.createCell(2).setCellValue("제목")
            header.createCell(3).setCellValue("부제목")
            header.createCell(4).setCellValue("컨텐츠")
            header.createCell(4).setCellValue("등록일자")

            // 데이터 채우기
            data.contents.forEachIndexed { index, value ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(value.idx.toString())
                row.createCell(1).setCellValue(value.type)
                row.createCell(2).setCellValue(value.title)
                row.createCell(3).setCellValue(value.subtitle)
                row.createCell(4).setCellValue(value.content)
                row.createCell(5).setCellValue(value.createdAt)
            }

            // 응답 헤더 설정
            responseSetting(response, fileName)

            // 배열 쓰기 → 스트림 flush → close 자동 실행
            workbook.write(response.outputStream)
            // `use` 블록 종료 시 자동 close()
        }

    }


}