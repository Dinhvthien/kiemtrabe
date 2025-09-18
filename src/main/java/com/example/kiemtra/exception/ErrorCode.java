package com.example.kiemtra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    STUDENT_NOT_FOUND(404, "Học sinh không tồn tại", HttpStatus.NOT_FOUND),
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
    EXAM_ALREADY_SUBMITTED(400, "Bài thi đã được nộp", HttpStatus.BAD_REQUEST),

    // New error codes for getRandomExamByClass
    INVALID_PHONE_NUMBER(400, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_CLASS_CODE(400, "Mã lớp học không hợp lệ", HttpStatus.BAD_REQUEST),
    NO_OPTIONS_IN_QUESTION(404, "Câu hỏi không có lựa chọn nào", HttpStatus.NOT_FOUND),
    INACTIVE_CLASS(400, "Lớp học không hoạt động", HttpStatus.BAD_REQUEST),
    EXPIRED_EXAM(400, "Bài thi đã hết hạn", HttpStatus.BAD_REQUEST),

    INVALID_EMAIL(400, "Email không hợp lệ", HttpStatus.BAD_REQUEST),

    INVALID_USERNAME(400, "Tên học sinh không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(400, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    INVALID_PAGINATION(400, "Phân trang không hợp lệ", HttpStatus.BAD_REQUEST),

    STUDENT_PHONE_EXISTS(400, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    STUDENT_EMAIL_EXISTS(400, "Đã tồn tại email của học sinh", HttpStatus.BAD_REQUEST),
    STUDENT_CLASS_EXISTS(400, "Mối quan hệ học sinh-lớp đã tồn tại", HttpStatus.BAD_REQUEST),
    STUDENT_CLASS_NOT_FOUND(404, "Bạn cần tham gia lớp học để kiểm tra", HttpStatus.NOT_FOUND),

    AUTHENTICATION_FAILED(401, "Xác thực thất bại", HttpStatus.UNAUTHORIZED),
    INVALID_KEY( 400,"Sai tên error code ở dto validate", HttpStatus.INTERNAL_SERVER_ERROR),
    CLASS_EXAM_ALREADY_EXISTS(400,"ClassExam with this classId and examId already exists", HttpStatus.INTERNAL_SERVER_ERROR),
    CLASS_EXAM_NOT_FOUND(400,"ClassExam not found", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_ALREADY(400,"User đã tồn tại", HttpStatus.BAD_REQUEST),
    CLASS_ALREADY_EXISTS(400,"Class đã tồn tại", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(400,"User không tồn tại", HttpStatus.BAD_REQUEST),
    OPTION_ALREADY_EXISTS(400,"OPTION đã tồn tại", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(401,"Unauthorized", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ERROR(500,"Lỗi không xác định", HttpStatus.UNAUTHORIZED),
    QUESTION_ALREADY_EXISTS(400,"Question đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLENAME_SIZE( 400,"Name không được nhỏ hơn 3 và lớn hơn 20 ký tự", HttpStatus.BAD_REQUEST),
    ROLENAME_INVALID( 400,"Name không được để trống", HttpStatus.BAD_REQUEST),
    ROLE_ALREADY( 400,"Name đã tồn tại", HttpStatus.BAD_REQUEST),
    CLASS_CODE_EXISTS(400, "Mã lớp học đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_UNALREADY( 400,"Role id không tồn tại", HttpStatus.BAD_REQUEST),
    USER_UNALREADY( 400,"User id không tồn tại", HttpStatus.BAD_REQUEST),
    COURSE_ALREADY( 400,"khóa học đã tồn tại", HttpStatus.BAD_REQUEST),
    NAME_EMPTY( 400,"Không tìm thấy!", HttpStatus.BAD_REQUEST),
    COURSETYPE_NAME_EMPTY( 400,"Tên không được để trống", HttpStatus.BAD_REQUEST),
    COURSETYPE_NAME_ALREADY( 400,"Tên đã được sử dụng", HttpStatus.BAD_REQUEST),
    COURSETYPE_NOT_FOUND( 400,"Không có dữ liệu !", HttpStatus.BAD_REQUEST),

    COURSETYPE_COURSE_EMPTY( 400,"Bạn cần truyền dữ liệu không được để trống", HttpStatus.BAD_REQUEST),
    COURSETYPE_COURSE_COURSEID_NOT_FOUND( 400,"Không tìm thấy id Course", HttpStatus.BAD_REQUEST),
    COURSETYPE_COURSE_COURSETYPEID_NOT_FOUND( 400,"Không tìm thấy id Course TYPE", HttpStatus.BAD_REQUEST),
    COURSETYPE_COURSE_ALREADY( 400,"Đã tồn tại", HttpStatus.BAD_REQUEST),
    ITEM_ALREADY_EXISTS( 400,"Đã tồn tại", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND( 400,"giỏ hàng không tồn tại", HttpStatus.BAD_REQUEST),
    CARTITEM_NOT_FOUND( 400,"Không tìm thấy ", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND( 400,"Không tìm thấy hóa đơn", HttpStatus.BAD_REQUEST),
    NOT_FOUND(404,"Không tìm thấy", HttpStatus.BAD_REQUEST), COURSESECTION_NOT_FOUND(400,"Không tìm thấy Chương học",HttpStatus.BAD_REQUEST),
    LESSON_NOT_FOUND(404,"Không tìm thấy bài học" ,HttpStatus.BAD_REQUEST ),
    DOCUMENT_NOT_FOUND(404,"Không tìm thấy Tài liệu" ,HttpStatus.BAD_REQUEST ),
    DOCUMENT_ALREADY_EXISTS(400,"Tài liệu đã tồn tại" ,HttpStatus.BAD_REQUEST ),
    VIDEO_ALREADY_EXISTS(400,"video đã tồn tại" ,HttpStatus.BAD_REQUEST ),
    VIDEO_NOT_FOUND(404,"Không tìm thấy video" ,HttpStatus.BAD_REQUEST ),
    VIDEO_TYPE_ALREADY_EXISTS(404,"Loại video đã tồn tại" ,HttpStatus.BAD_REQUEST ),
    VIDEO_TYPE_NOT_FOUND(404,"Không tìm thấy loại video" ,HttpStatus.BAD_REQUEST ),
    DOCUMENT_TYPE_ALREADY_EXISTS(404,"Loại document đã tồn tại" ,HttpStatus.BAD_REQUEST ),
    DOCUMENT_TYPE_NOT_FOUND(404,"Không tìm thấy loại documen" ,HttpStatus.BAD_REQUEST ),
    USER_ALREADY_EXISTS(404,"User đã tồn tại" ,HttpStatus.BAD_REQUEST ),
    INVALID_INPUT(404,"Username cannot be null or empty" ,HttpStatus.BAD_REQUEST ),
    EXAM_ALREADY_COMPLETED(400,"EXAM đã hoàn thành", HttpStatus.BAD_REQUEST),;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}
