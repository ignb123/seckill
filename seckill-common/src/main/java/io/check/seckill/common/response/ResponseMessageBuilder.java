package io.check.seckill.common.response;

/**
 * @author check
 * @version 1.0.0
 * @description 响应消息构建类
 */
public class ResponseMessageBuilder {

    public static <T> ResponseMessage<T> build(Integer code, T body){
        return new ResponseMessage<T>(code, body);
    }

    public static <T> ResponseMessage<T> build(Integer code){
        return new ResponseMessage<T>(code);
    }
}