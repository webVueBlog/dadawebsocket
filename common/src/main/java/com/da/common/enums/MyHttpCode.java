package com.da.common.enums;

/**
 * 自定义返回状态码和消息
 *
 */
public enum MyHttpCode {
    /**
     * 状态码
     */
    OK(200, "操作成功！"),
    ERROR(400, "操作失败！"),
    UNAUTHENTICATED(401, "客户端未鉴权！");

    private Integer code;
    private String msg;

    MyHttpCode() {
    }

    MyHttpCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
