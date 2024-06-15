package io.check.seckill.common.exception;

import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(SeckillException.class)
    public ResponseMessage<String> handleSeckillException(SeckillException e) {
        logger.error("服务器抛出了异常：{}", e);
        return ResponseMessageBuilder.build(e.getCode(), e.getMessage());
    }

    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(Exception.class)
    public ResponseMessage<String> handleException(Exception e) {
        logger.error("服务器抛出了异常：{}", e);
        return ResponseMessageBuilder.build(ErrorCode.SERVER_EXCEPTION.getCode(), e.getMessage());
    }


}
