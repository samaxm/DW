package com.sx.dw.core.network.exception;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/24 9:33
 */
public class UnSuccessfulException extends Exception {
    public UnSuccessfulException(Object response) {
        super(String.valueOf(response));
    }
}
