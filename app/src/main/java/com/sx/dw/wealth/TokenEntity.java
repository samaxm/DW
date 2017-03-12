package com.sx.dw.wealth;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.util.AES;

import java.util.Calendar;
import java.util.Random;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/25 16:00
 */

public class TokenEntity {
    public static final String DW_TOKEN = "dw_token";
    public static final String TOKEN_TIME = "token_time";
    public static final String PUBLIC_KEY = "publicKey";
    private String key;
    private String publicKey;
    private long time;

    public String getKey() {
        LogUtils.d(key);
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isTokenTooOld(){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(getTime());
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        return now.get(Calendar.MONTH) - date.get(Calendar.MONTH) > 0;
    }

    public String getTokenStr(){
        String body = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int num = random.nextInt(62);
            buf.append(body.charAt(num));
        }
        String head = buf.toString();
        buf.append(AES.encode(head,getKey()));
        LogUtils.d(buf.toString());
        return buf.toString();
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
                "key='" + key + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", time=" + time +
                '}';
    }
}
