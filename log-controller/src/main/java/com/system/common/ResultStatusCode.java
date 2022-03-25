package com.system.common;

/**
 * @author Ming
 * @date 2020/4/26 - 17:31
 * @describe
 */
public enum ResultStatusCode {

    SUCCESS(500000,"请求成功"),
    FAILED(500001, "操作失败,请重试"),
    BAD_REQUEST(400000,"参数错误"),
    UNAUTHORIZED(400001, "暂未登录或token已经过期"),
    FORBIDDEN(400003, "没有相关权限"),
    NOT_FOUND(400004, "找不到数据"),
    URL_TO_LONG(400015,"请求方法错误"),
    REDIRECT(300002,"重定向");



    private Integer code;
    private String message;

    ResultStatusCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
