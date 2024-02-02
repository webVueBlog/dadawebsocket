package com.da.common.vo;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.Serializable;

public class ResultData<V> implements Serializable {
    // 序列化版本号
    private static final long serialVersionUID = 6674999278660577990L;
    // 状态码
    private int code = HttpResponseStatus.OK.code();
    private String msg = "success";
    private V data;

    public ResultData() {
    }

    public ResultData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultData(V data) {// 返回数据
        this.data = data;
    }

    public ResultData(int code, String msg, V data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static long getSerialVersionUID() {// 返回序列化版本号
        return serialVersionUID;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    public static Builder builder() {// 返回建造者
        return new Builder();// 建造者模式
    }

    /**
     * 建造者模式
     * @param <V>
     */
    public static class Builder<V> {

        private ResultData resultData;// 建造者持有的建造目标对象

        public Builder() {// 初始化建造目标对象
            resultData = new ResultData();// 建造者模式
        }

        public Builder code(int code) {
            resultData.code = code;// 建造者模式
            return this;
        }

        public Builder msg(String msg) {
            resultData.msg = msg;// 建造者模式
            return this;
        }

        public Builder data(V data) {
            resultData.data = data;// 建造者模式
            return this;
        }

        public ResultData builded() {// 建造完成后返回建造目标对象
            return resultData;// 建造者模式
        }
    }

}
