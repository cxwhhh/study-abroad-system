package com.study.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cxw
 * @since 2025-01-16
 *        通用返回结果类
 *        用于统一接口返回格式
 * 
 * @param <T> 返回数据的类型
 */
@Data
public class Result<T> {
    /** 状态码 */
    private Integer code;
    /** 返回信息 */
    private String message;
    /** 返回数据 */
    private T data;
    private Map<String, Object> extraData = new HashMap<>();

    /**
     * 成功返回结果
     *
     * @param data 返回的数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功返回结果
     *
     * @param data 返回的数据
     * @param message 返回信息
     * @return Result对象
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     *
     * @param message 错误信息
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败返回结果
     *
     * @param code 错误码
     * @param message 错误信息
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public Result<T> addData(String key, Object value) {
        this.extraData.put(key, value);
        return this;
    }
}