package com.sx.dw.wealth;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;

import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/25 16:30
 */

public class TokenUtil {
    private static final String SP_FILE_TOKEN = "token";
    private static TokenUtil util;

    private SharedPreferences sharedPreferences;
    private WealthApi wealthApi;

    public static TokenUtil getInstance() {
        if (util == null) {
            util = new TokenUtil();
        }
        return util;
    }

    public TokenUtil() {
        sharedPreferences = DWApplication.getInstance().getSharedPreferences(SP_FILE_TOKEN, 0);
        wealthApi = DwRetrofit.getInstance().createApi(WealthApi.class);
    }


    public void updateToken() {
        updateToken(null);
    }

    public void updateToken(final UpdateTokenCallback callback) {
        if (accountInfo == null) {
            return;
        }
        if (getToken() != null) {
            if (!TextUtils.isEmpty(getToken().getKey()) && !getToken().isTokenTooOld()) {
                return;
            } else if (getToken().getPublicKey() != null) {
                pushAES(getToken(),callback);
                return;
            }
        }
        wealthApi.getRSA(accountInfo.getId()).enqueue(new DwCallback<EntityHead<String>>() {
            @Override
            public void getBody(Call call, EntityHead<String> body) {
                if (body.isSuccess()) {
                    TokenEntity entity = new TokenEntity();
                    entity.setPublicKey(body.getData());
                    pushAES(entity,callback);
                } else {
                    LogUtils.d(body.getMsg());
                }
            }
        });
    }

    private void pushAES(final TokenEntity entity, final UpdateTokenCallback callback) {
        entity.setKey(getNewKey());
        String privateKey = makePrivateKey(entity.getPublicKey(), entity.getKey());
        LogUtils.d(entity);
        wealthApi.uploadAES(accountInfo.getId(), AES.encode(accountInfo.getPassword()), privateKey)
                .enqueue(new DwCallback<EntityHead>() {
                    @Override
                    public void getBody(Call call, EntityHead body) {
                        if (body.isSuccess()) {
                            entity.setTime(System.currentTimeMillis());
                            saveToken(entity);
                            if(callback!=null){
                                callback.onSuccess();
                            }
                        } else {
                            LogUtils.d(body.getMsg());
                        }
                    }
                });
    }

    public void clearToken() {
        saveToken(new TokenEntity());
    }

    /**
     * @param entity 为null时表示删除数据
     */
    private void saveToken(@NonNull TokenEntity entity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TokenEntity.DW_TOKEN, entity.getKey());
        editor.putLong(TokenEntity.TOKEN_TIME, entity.getTime());
        editor.putString(TokenEntity.PUBLIC_KEY, entity.getPublicKey());
        editor.commit();
    }


    public TokenEntity getToken() {
        TokenEntity entity = new TokenEntity();
        entity.setKey(sharedPreferences.getString(TokenEntity.DW_TOKEN, null));
        entity.setTime(sharedPreferences.getLong(TokenEntity.TOKEN_TIME, 0));
        entity.setPublicKey(sharedPreferences.getString(TokenEntity.PUBLIC_KEY, null));
        if (TextUtils.isEmpty(entity.getPublicKey())) {
            return null;
        } else {
            return entity;
        }
    }

    public String getKey() {
        if (getToken() != null) {
            return getToken().getKey();
        } else {
            return null;
        }
    }

    /**
     * @return 生成随机的key
     */
    private String getNewKey() {
        String body = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int num = random.nextInt(62);
            buf.append(body.charAt(num));
        }
        return buf.toString();
    }

    private String makePrivateKey(String publicRSA, String key) {
//        开始加密
        try {
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            PublicKey publicKey = getPublicKey(publicRSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String data = Base64.encode(cipher.doFinal(key.getBytes()));
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private RSAPublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    interface UpdateTokenCallback {
        void onSuccess();
    }


}
