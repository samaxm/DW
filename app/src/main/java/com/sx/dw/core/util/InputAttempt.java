package com.sx.dw.core.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * @ClassName: ${CLASS_NAME}
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/9/23
 */

public class InputAttempt {

    public static boolean attemptInput(EditText editText,CheckRule rule) {
        EditText[] editTexts = {editText};
        CheckRule[] rules = {rule};
        return attemptInput(editTexts,rules);
    }


    public static boolean attemptInput(EditText[] editTexts,CheckRule[] rules) {
        if (editTexts.length != rules.length) {
            return false;
        }

        //标记是否有误
        boolean cancel = false;
        //声明一个需要获取焦点的view
        View focusView = null;

        for (int i = 0; i < editTexts.length; i++) {
            EditText input = editTexts[i];
            input.setError(null);
            String error = rules[i].checkInput(input.getText().toString());
            if (error != null) {
                focusView = editTexts[i];
                input.setError(error);
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }





    public static CheckRule getPhoneRule() {
        InputAttempt.CheckRule phoneRule = new InputAttempt.CheckRule() {
            @Override
            public String checkInput(String input) {
                if (TextUtils.isEmpty(input)) {
                    return "请输入手机号码";
                } else if (input.length()!=11 && !input.substring(0, 1).equals("1")) {
                    return "请输入正确的手机号码";
                }
                return null;
            }
        };
        return phoneRule;
    }

    public static CheckRule getPasswordRule() {
        InputAttempt.CheckRule passwordRule = new InputAttempt.CheckRule() {
            @Override
            public String checkInput(String input) {
                if(TextUtils.isEmpty(input)){
                    return "输入不能为空";
                }else if(input.length()<6){
                    return "密码的长度应该为6至20位";
                }
                return null;
            }
        };
        return passwordRule;
    }

    public static CheckRule getPasswordAgainRule(final EditText etInputPassword) {
        InputAttempt.CheckRule passwordAgainRule = new InputAttempt.CheckRule() {
            @Override
            public String checkInput(String input) {
                if(TextUtils.isEmpty(input)){
                    return "输入不能为空";
                }else if(!input.equals(etInputPassword.getText().toString())){
                    return "两次输入不一致";
                }
                return null;
            }
        };
        return passwordAgainRule;
    }

    public static CheckRule getPayPsdRule() {
        InputAttempt.CheckRule passwordRule = new InputAttempt.CheckRule() {
            @Override
            public String checkInput(String input) {
                if(TextUtils.isEmpty(input)){
                    return "输入不能为空";
                }else if(input.length() != 6){
                    return "密码应该为6位纯数字";
                }
                return null;
            }
        };
        return passwordRule;
    }

    public static CheckRule getPayPsdAgainRule(final EditText etInputPassword) {
        InputAttempt.CheckRule passwordAgainRule = new InputAttempt.CheckRule() {
            @Override
            public String checkInput(String input) {
                if(TextUtils.isEmpty(input)){
                    return "输入不能为空";
                }else if(!input.equals(etInputPassword.getText().toString())){
                    return "两次输入不一致";
                }
                return null;
            }
        };
        return passwordAgainRule;
    }

    public static CheckRule getCodeRule() {
        InputAttempt.CheckRule codeRule = new InputAttempt.CheckRule() {
            @Override
            public String checkInput(String input) {
                if (TextUtils.isEmpty(input)) {
                    return "请输入验证码";
                } else if (input.length()!= 4) {
                    return "请输入四位数字的验证码";
                }
                return null;
            }
        };
        return codeRule;
    }

    public interface CheckRule {
        String checkInput(String input);
    }

}
