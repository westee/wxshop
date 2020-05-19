package com.westee.wxshop.entity;

// 作为响应 只有getter方法就可以了。
public class Response<T> {
    private String message;
    private T data;

    public static <T> Response<T> of(T object) {
        return new Response<T>(null, object);
    }

    public static <T> Response<T> of(String message, T object) {
        return new Response<T>(message, object);
    }

    public Response(){}

    public Response(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
