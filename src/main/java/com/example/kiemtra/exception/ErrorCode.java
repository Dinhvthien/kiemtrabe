package com.example.kiemtra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    STUDENT_NOT_FOUND(404, "Học sinh không tồn tại", HttpStatus.NOT_FOUND),
    STUDENT_NOT_ACTIVE(404, "Bạn cần liên hệ admin để xác minh", HttpStatus.NOT_FOUND),
    CLASS_NOT_FOUND(404, "Lớp học không tồn tại", HttpStatus.NOT_FOUND),
    STUDENT_NOT_ENROLLED_IN_CLASS(400, "Học sinh không đăng ký trong lớp học", HttpStatus.BAD_REQUEST),
    NO_EXAMS_IN_CLASS(404, "Không có bài thi nào trong lớp học", HttpStatus.NOT_FOUND),
    NO_QUESTIONS_IN_EXAM(404, "Bài thi không có câu hỏi nào", HttpStatus.NOT_FOUND),
    EXAM_NOT_FOUND(404, "Bài thi không tồn tại", HttpStatus.NOT_FOUND),
    EXAM_ALREADY_EXISTS(400, "Mã bài thi đã tồn tại", HttpStatus.BAD_REQUEST),
    DUPLICATE_PHONE_IN_REQUEST(400, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL_IN_REQUEST(400, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    OPTION_NOT_FOUND(404, "Lựa chọn không tồn tại", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(404, "Câu hỏi không tồn tại", HttpStatus.NOT_FOUND),
    STUDENT_ALREADY_COMPLETED_EXAM(400, "Bạn đã hoàn thành bài thi cho khóa học này!!", HttpStatus.BAD_REQUEST),
    STUDENT_CLASS_EXISTS(400, "Mối quan hệ học sinh-lớp đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_KEY( 400,"Sai tên error code ở dto validate", HttpStatus.INTERNAL_SERVER_ERROR),
    CLASS_EXAM_ALREADY_EXISTS(400,"ClassExam with this classId and examId already exists", HttpStatus.INTERNAL_SERVER_ERROR),
    CLASS_EXAM_NOT_FOUND(400,"ClassExam not found", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_EXISTED(400,"User không tồn tại", HttpStatus.BAD_REQUEST),
    OPTION_ALREADY_EXISTS(400,"OPTION đã tồn tại", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401,"Unauthorized", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ERROR(500,"Lỗi không xác định", HttpStatus.UNAUTHORIZED),
    QUESTION_ALREADY_EXISTS(400,"Question đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLENAME_SIZE( 400,"Name không được nhỏ hơn 3 và lớn hơn 20 ký tự", HttpStatus.BAD_REQUEST),
    ROLENAME_INVALID( 400,"Name không được để trống", HttpStatus.BAD_REQUEST),
    ROLE_ALREADY( 400,"Name đã tồn tại", HttpStatus.BAD_REQUEST),
    CLASS_CODE_EXISTS(400, "Mã lớp học đã tồn tại", HttpStatus.BAD_REQUEST);
    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}
