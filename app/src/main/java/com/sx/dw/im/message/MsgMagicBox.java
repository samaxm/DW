package com.sx.dw.im.message;

import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LikeMsg;
import com.sx.dw.im.entity.WealthAckMsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 用来中转消息数据的容器，根据业务需要封装不同的数据及其组合
 * @author: fanjie
 * @date: 2016/10/3 17:54
 */

public class MsgMagicBox implements Serializable {

    private MessageProtos.Message.MessageType type;

    private ChatMsg chatMsg;
    private LikeMsg likeMsg;
    private WealthAckMsg wealthAckMsg;

    private List<ChatMsg> chatMsgs;
    private List<LikeMsg> likeMsgs;
    private List<WealthAckMsg> wealthAckMsgs;

    public void addChatMessage(ChatMsg entity){
        if(getChatMsgs() == null){
            this.chatMsgs = new ArrayList<>();
        }
        getChatMsgs().add(entity);
    }
    public void addLikeMessage(LikeMsg entity){
        if(getLikeMsgs() == null){
            this.likeMsgs = new ArrayList<>();
        }
        getLikeMsgs().add(entity);
    }
    public void addWealthAckMessage(WealthAckMsg entity){
        if(getWealthAckMsgs() == null){
            this.wealthAckMsgs = new ArrayList<>();
        }
        getWealthAckMsgs().add(entity);
    }

    public List<ChatMsg> getChatMsgs() {
        return chatMsgs;
    }

    public List<LikeMsg> getLikeMsgs() {
        return likeMsgs;
    }

    public List<WealthAckMsg> getWealthAckMsgs() {
        return wealthAckMsgs;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public LikeMsg getLikeMsg() {
        return likeMsg;
    }

    public void setLikeMsg(LikeMsg likeMsg) {
        this.likeMsg = likeMsg;
    }

    public WealthAckMsg getWealthAckMsg() {
        return wealthAckMsg;
    }

    public void setWealthAckMsg(WealthAckMsg wealthAckMsg) {
        this.wealthAckMsg = wealthAckMsg;
    }

    public MessageProtos.Message.MessageType getType() {
        return type;
    }

    public void setType(MessageProtos.Message.MessageType type) {
        this.type = type;
    }
}
