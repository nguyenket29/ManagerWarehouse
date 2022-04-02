package com.hau.warehouse.constant;

import org.springframework.http.HttpStatus;

public enum ErrorEnum {
    SUCCESS(HttpStatus.OK, "200", "Thành công"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "Lỗi Server"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TK-4001", "Token không hợp lệ"),
    ACCESS_TOKEN_EXPIRE(HttpStatus.UNAUTHORIZED, "TK-4002", "Token hết hạn truy cập"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "US-4003", "Người dùng không tồn tại"),
    USERNAME_INVALID(HttpStatus.BAD_REQUEST, "USN-4004", "Username không hợp lệ"),
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST, "EM-4005", "Email đã tồn tại"),
    USER_EXISTED(HttpStatus.BAD_REQUEST, "US-4006", "User đã tồn tại"),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "NF-400", "Dữ liệu không tồn tại"),
    NOT_USER_PERMISSION(HttpStatus.BAD_REQUEST, "LMS-4007", "User không có quyền"),
    NOT_RESPONDING(HttpStatus.GATEWAY_TIMEOUT, "GW-504", "Lỗi phản hồi "),
    REFRESH_TOKEN(HttpStatus.FORBIDDEN, "RETK-4008", "Lỗi cập nhật lại token !");

    private final HttpStatus value;
    private String code;
    private String message;

    public HttpStatus getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ErrorEnum(HttpStatus value, String code, String message) {
        this.value = value;
        this.code = code;
        this.message = message;
    }
}
