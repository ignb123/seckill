package io.check.seckill.common.utils.uuid;

import java.util.UUID;

/**
 * @author check
 * @version 1.0.0
 * @description UUID工具类
 */
public class UUIDUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
