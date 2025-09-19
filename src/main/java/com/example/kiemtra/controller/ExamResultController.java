package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.StudentExamScoreDTO;
import com.example.kiemtra.dto.request.ExamRequest;
import com.example.kiemtra.dto.response.ExamResponse;
import com.example.kiemtra.service.ExamService;
import com.example.kiemtra.util.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/exams-result")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamResultController {
    ExamService examService;
    @GetMapping("/{classId}")
    public ApiResponse<List<StudentExamScoreDTO>> getAllStudentScoresInClass(@PathVariable Long classId) {
        List<StudentExamScoreDTO> results = examService.getAllStudentScoresInClass(classId);
        return ApiResponse.<List<StudentExamScoreDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Student exam scores retrieved successfully")
                .result(results)
                .build();
    }

    @GetMapping("/export/{classId}")
    public void exportStudentScoresToExcel(@PathVariable Long classId, HttpServletResponse response) throws IOException {
        try {
            // Lấy dữ liệu điểm số của học sinh
            List<StudentExamScoreDTO> studentScores = examService.getAllStudentScoresInClass(classId);

            if (studentScores.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                response.getWriter().write("No data found for class " + classId);
                return;
            }

            // Tạo workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Student Scores");

            // Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("STT");
            headerRow.createCell(1).setCellValue("Họ tên");
            headerRow.createCell(2).setCellValue("Số điện thoại");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Lớp học");
            headerRow.createCell(5).setCellValue("Bài kiểm tra");
            headerRow.createCell(6).setCellValue("Điểm số");
            headerRow.createCell(7).setCellValue("Xếp loại");
            headerRow.createCell(8).setCellValue("Số câu đúng");
            headerRow.createCell(9).setCellValue("Tổng số câu");
            headerRow.createCell(10).setCellValue("Trạng thái");

            // Điền dữ liệu vào các hàng
            int rowNum = 1;
            for (StudentExamScoreDTO score : studentScores) {
                Row row = sheet.createRow(rowNum);

                row.createCell(0).setCellValue(rowNum); // STT
                row.createCell(1).setCellValue(score.getFullName() != null ? score.getFullName() : "N/A");
                row.createCell(2).setCellValue(score.getPhoneNumber() != null ? score.getPhoneNumber() : "N/A");
                row.createCell(3).setCellValue(score.getEmail() != null ? score.getEmail() : "N/A");
                row.createCell(4).setCellValue(score.getClassName() != null ? score.getClassName() : "N/A");
                row.createCell(5).setCellValue(score.getExamName() != null ? score.getExamName() : "N/A");

                // Xử lý điểm số
                if (score.getScore() != null) {
                    row.createCell(6).setCellValue(score.getScore());
                } else {
                    row.createCell(6).setCellValue("Chưa thi");
                }

                row.createCell(7).setCellValue(score.getGrade() != null ? score.getGrade() : "N/A");
                row.createCell(8).setCellValue(score.getCorrectAnswers() != null ? score.getCorrectAnswers() : 0);
                row.createCell(9).setCellValue(score.getTotalQuestions() != null ? score.getTotalQuestions() : 0);
                row.createCell(10).setCellValue(score.getExamStatus());

                rowNum++;
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < 11; i++) {
                sheet.autoSizeColumn(i);
            }

            // Đặt Content-Type và Header TRƯỚC KHI ghi dữ liệu
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "ket_qua_thi_lop_" + classId + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // QUAN TRỌNG: Không gọi response.reset() sau khi đã set header

            // Ghi workbook vào output stream
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();

            // Đóng workbook
            workbook.close();
            outputStream.close();

        } catch (Exception e) {
            // Log lỗi và trả về response lỗi
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Failed to export Excel: " + e.getMessage() + "\"}");
        }
    }
}