package com.sx.dw.core.network.exception;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/24 9:32
 */
public class NullBodyException extends Exception {

    public NullBodyException(Object o) {
        super(String.valueOf(o));
    }
}
