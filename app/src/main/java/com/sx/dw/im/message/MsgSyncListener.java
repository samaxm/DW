package com.sx.dw.im.message;

import com.sx.dw.im.entity.ChatMsg;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/4 15:54
 */

public interface MsgSyncListener {
    void onMsgGet(ChatMsg msg);
}
