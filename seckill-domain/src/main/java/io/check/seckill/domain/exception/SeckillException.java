package io.check.seckill.domain.exception;

import io.check.seckill.domain.code.HttpCode;

public class SeckillException extends RuntimeException{

    private Integer code;

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(HttpCode errorCode){
        this(errorCode.getCode(), errorCode.getMesaage());
    }

    public SeckillException(Integer code, String messgae){
        super(messgae);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
