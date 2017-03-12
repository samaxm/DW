package com.sx.dw.im.entity;

import android.text.format.DateFormat;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.sx.dw.im.message.MessageProtos;

import java.io.Serializable;
import java.util.Calendar;

@Table(name = "ChatMsgs")
public class ChatMsg extends Model implements Serializable, Comparable<ChatMsg> {

    @Column(name = "status")
    private MessageProtos.ChargeStatus status;
    @Column(name = "tempID")
    private String tempID;
    @Column(name = "toId")
    private String toId;
    @Column(name = "fromID")
    private String fromID;
    @Column(name = "receiverWealth")
    private String receiverWealth;
    @Column(name = "mid")
    private long mid;
    @Column(name = "time")
    private long time;
    @Column(name = "relationType")
    private MessageProtos.FriendType relationType;
//    @Column(name = "messageType")
//    private MessageProtos.ChatMessageType messageType;
    //文本消息属性
    @Column(name = "text")
    private String text;
//    //图片消息属性
//    @Column(name = "imageUrl")
//    private String imageUrl;//图片的url
//    @Column(name = "compress")
//    private byte[] compress;//略缩图
//    //语音消息属性
//    @Column(name = "audioUrl")
//    private String audioUrl;//Audio的url
//    @Column(name = "audioLength")
//    private int audioLength;

    //    外键，会话
    @Column(name = "chat")
    private Chat chat;

    @Column(name = "textMsgType")
    private TextMsgType textMsgType;

    //是否为未读消息
    @Column(name = "unreadMsg")
    private boolean unreadMsg;
    //    消息状态
    @Column(name = "msgState")
    private MsgState msgState;

    //    标记在列表中的位置，不存db
    private int listPosition;
//    是否显示时间，在聊天界面中
    private boolean showTime;

    public ChatMsg() {
        // FIXME 暂时为陌生人
        this.relationType = MessageProtos.FriendType.STRANGER;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public TextMsgType getTextMsgType() {
        return textMsgType;
    }

    public void setTextMsgType(TextMsgType textMsgType) {
        this.textMsgType = textMsgType;
    }

    public MessageProtos.ChargeStatus getStatus() {
        return status;
    }

    public void setStatus(MessageProtos.ChargeStatus status) {
        this.status = status;
    }

    public String getTempID() {
        return tempID;
    }

    public void setTempID(String tempID) {
        this.tempID = tempID;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getReceiverWealth() {
        return receiverWealth;
    }

    public void setReceiverWealth(String receiverWealth) {
        this.receiverWealth = receiverWealth;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MessageProtos.FriendType getRelationType() {
        return relationType;
    }

    public void setRelationType(MessageProtos.FriendType relationType) {
        this.relationType = relationType;
    }

//    public MessageProtos.ChatMessageType getMessageType() {
//        return messageType;
//    }
//
//    public void setMessageType(MessageProtos.ChatMessageType messageType) {
//        this.messageType = messageType;
//    }

//    public String getAudioUrl() {
//        return audioUrl;
//    }
//
//    public void setAudioUrl(String audioUrl) {
//        this.audioUrl = audioUrl;
//    }
//
//    public int getAudioLength() {
//        return audioLength;
//    }
//
//    public void setAudioLength(int audioLength) {
//        this.audioLength = audioLength;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public byte[] getCompress() {
//        return compress;
//    }
//
//    public void setCompress(byte[] compress) {
//        this.compress = compress;
//    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public boolean isUnreadMsg() {
        return unreadMsg;
    }

    public void setUnreadMsg(boolean unreadMsg) {
        this.unreadMsg = unreadMsg;
    }

    public MsgState getMsgState() {
        if (msgState == null) {
            msgState = MsgState.SENDING;
        }
        return msgState;
    }

    public void setMsgState(MsgState msgState) {
        this.msgState = msgState;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public String getTimeToShow() {
        Calendar msgTime = Calendar.getInstance();
        msgTime.setTimeInMillis(getTime());
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        String timeToShow;
        String year = DateFormat.format("yyyy-", getTime()).toString();
        String monthDay = DateFormat.format("MM-dd", getTime()).toString();
        String hourMinute = DateFormat.format("HH:mm", getTime()).toString();
        switch (now.get(Calendar.DAY_OF_YEAR) - msgTime.get(Calendar.DAY_OF_YEAR)) {
            case 0: {
                timeToShow = hourMinute;
                break;
            }
            case 1: {
                timeToShow = "昨天 " + hourMinute;
                break;
            }
            case 2: {
                timeToShow = "前天 " + hourMinute;
                break;
            }
            default: {
                if (now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)) {
                    timeToShow = monthDay+" "+hourMinute;
                }else {
                    timeToShow = year+monthDay+" "+hourMinute;
                }
            }
        }
        return timeToShow;
    }

    @Override
    public String toString() {
        return "ChatMsg{" +
                "status=" + status +
                ", tempID='" + tempID + '\'' +
                ", toId='" + toId + '\'' +
                ", fromID='" + fromID + '\'' +
                ", receiverWealth='" + receiverWealth + '\'' +
                ", mid=" + mid +
                ", time=" + time +
                ", relationType=" + relationType +
//                ", messageType=" + messageType +
                ", text='" + text + '\'' +
//                ", imageUrl='" + imageUrl + '\'' +
//                ", compress=" + Arrays.toString(compress) +
//                ", audioUrl='" + audioUrl + '\'' +
//                ", audioLength=" + audioLength +
                ", textMsgType=" + textMsgType +
                ", unreadMsg=" + unreadMsg +
                ", msgState=" + msgState +
                ", listPosition=" + listPosition +
                ", showTime=" + showTime +
                '}';
    }

    @Override
    public int compareTo(ChatMsg chatMsg) {
       if(time-chatMsg.time>0){
           return 1;
       }else if(time-chatMsg.time<0){
           return -1;
       }else{
           return 0;
       }
    }

    public String getTicker() {
//        String ticker = "";
//        switch (getMessageType()){
//            case IMAGE:
//                ticker = "[图片]";
//                break;
//            case AUDIO:
//                ticker = "[语音]";
//                break;
//            case TEXT:
//                ticker = getText();
//                break;
//        }
//        LogUtils.d(ticker);
//        return ticker;
        return getText();
    }

    public enum TextMsgType {
        LIKE, GET, SEND,NOTICE
    }

    public enum MsgState {
        SENDING, SUCCESSED, FAILURE
    }

}