package io.check.seckill.infrastructure.handler.exception;


import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.response.ResponseMessage;
import io.check.seckill.domain.response.ResponseMessageBuilder;
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
        return ResponseMessageBuilder.build(HttpCode.SERVER_EXCEPTION.getCode(), e.getMessage());
    }


}