package com.sx.dw.im.message;

import com.apkfuns.logutils.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sx.dw.core.util.AES;
import com.sx.dw.core.util.MD5;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LikeMsg;
import com.sx.dw.im.entity.WealthAckMsg;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/3 13:47
 */

public class ProtoMsgUtils {

    private static ProtoMsgUtils protoMsgUtils = new ProtoMsgUtils();

    public static ProtoMsgUtils getInstance() {
        return protoMsgUtils;
    }

    public MsgMagicBox getMessage(MessageProtos.Message message) {
        MsgMagicBox magicBox = new MsgMagicBox();
        magicBox.setType(message.getType());
        switch (message.getType()) {
            case CHAT_TEXT:
                magicBox.setChatMsg(getChatMessage(message));
                break;
            case NOTICE_LIKE:
                magicBox.setLikeMsg(getLikeMessage(message));
                break;
            case COMMAND_WEALTH_ACK:
                magicBox.setWealthAckMsg(getWealthAckMessage(message));
                break;
            case LIST:
                getListMessage(message, magicBox);
        }
        return magicBox;
    }

    private void getListMessage(MessageProtos.Message message, MsgMagicBox magicBox) {
        byte[] data = message.getData().toByteArray();
        try {
            MessageProtos.MessageContainer container = MessageProtos.MessageContainer.parseFrom(data);
            for (MessageProtos.Message m : container.getMessagesList()) {
                switch (m.getType()) {
                    case CHAT_TEXT:
                        magicBox.addChatMessage(getChatMessage(m));
                        break;
                    case NOTICE_LIKE:
                        magicBox.addLikeMessage(getLikeMessage(m));
                        break;
                    case COMMAND_WEALTH_ACK:
                        magicBox.addWealthAckMessage(getWealthAckMessage(m));
                        break;
                    case LIST:
//                        TODO 如果嵌套了List，怎么处理？
                        break;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private ChatMsg getChatMessage(MessageProtos.Message message) {
        ChatMsg entity = null;
        try {
            MessageProtos.TextChatMessage chatMessage = MessageProtos.TextChatMessage.parseFrom(message.getData());
            entity = new ChatMsg();
//            数据机械转移
            entity.setStatus(chatMessage.getStatus());
            entity.setTempID(chatMessage.getTempID());
            entity.setFromID(chatMessage.getFromID());
            entity.setToId(chatMessage.getToID());
            entity.setReceiverWealth(chatMessage.getReceiverWealth());
            entity.setMid(message.getMid());
            entity.setTime(message.getTime());
            entity.setRelationType(chatMessage.getRelationType());
            String text = chatMessage.getText();
            entity.setText(text);

//            if (entity.getMessageType() == MessageProtos.ChatMessageType.TEXT) {
//            } else if (entity.getMessageType() == MessageProtos.ChatMessageType.IMAGE) {
//                MessageProtos.ImageChatMessageInfo image = MessageProtos.ImageChatMessageInfo.parseFrom(message.getData());
//                entity.setImageUrl(image.getUrl());
////                FIXME byteString 存储数据库是否可行？
//                entity.setCompress(image.getCompress().toByteArray());
//            } else if (entity.getMessageType() == MessageProtos.ChatMessageType.AUDIO) {
//                MessageProtos.AudioChatMessageInfo audio = MessageProtos.AudioChatMessageInfo.parseFrom(message.getData());
//                entity.setAudioUrl(audio.getUrl());
//                entity.setAudioLength(audio.getLength());
//            }

//            逻辑数据的构建
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private LikeMsg getLikeMessage(MessageProtos.Message message) {
        LikeMsg entity = null;
        try {
            MessageProtos.Notice_Like vedioLikeMessageInfo = MessageProtos.Notice_Like.parseFrom(message.getData());
            entity = new LikeMsg();
            entity.setIcon(vedioLikeMessageInfo.getIcon());
            entity.setName(vedioLikeMessageInfo.getName());
            entity.setBeLikedID(vedioLikeMessageInfo.getBelikeID());
            entity.setSex(vedioLikeMessageInfo.getSex());
            entity.setTime(message.getTime());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private WealthAckMsg getWealthAckMessage(MessageProtos.Message message) {
        WealthAckMsg entity = null;
        try {
            MessageProtos.Command_WealthACK wealthACK = MessageProtos.Command_WealthACK.parseFrom(message.getData());
            entity = new WealthAckMsg();
            entity.setMid(wealthACK.getMid());
            entity.setTempID(wealthACK.getTempID());
            entity.setWealth(wealthACK.getWealth());
            entity.setChargeSuccess(wealthACK.getChargeSuccess());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public MultipartBody toBody(ChatMsg entity) {
        String token = AES.encode(MD5.GetMD5Code(toByte(entity)));
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), toByte(entity));
        MultipartBody.Part part = MultipartBody.Part.createFormData("MSG", "msg", body);
//        MultipartBody multipartBody = new MultipartBody.Builder().addFormDataPart("DW_TOKEN", token)
        MultipartBody multipartBody = new MultipartBody.Builder().addFormDataPart("TOKEN", token)
                .addFormDataPart("TEMP_ID", entity.getTempID())
                .addFormDataPart("USER_ID", entity.getFromID())
                .addPart(part)
                .build();
        return multipartBody;
    }

    private byte[] toByte(ChatMsg entity) {
        MessageProtos.TextChatMessage.Builder chatMessageBuilder = MessageProtos.TextChatMessage.newBuilder();
        LogUtils.v(entity);
        chatMessageBuilder.setText(entity.getText());
//        switch (entity.getMessageType()) {
//            case TEXT:
//                MessageProtos.TextChatMessageInfo chatMessageInfo = MessageProtos.TextChatMessageInfo.newBuilder()
//                        .setText(entity.getText())
//                        .build();
//                chatMessageBuilder.setContent(chatMessageInfo.toByteString())
//                        .setMessageType(MessageProtos.ChatMessageType.TEXT);
//                break;
//            case IMAGE:
//                MessageProtos.ImageChatMessageInfo imageChatMessageInfo = MessageProtos.ImageChatMessageInfo.newBuilder()
////                        TODO 需要搞清楚图片发送，模型也需要更改
//                        .build();
//                chatMessageBuilder.setContent(imageChatMessageInfo.toByteString())
//                        .setMessageType(MessageProtos.ChatMessageType.IMAGE);
//                break;
//            case AUDIO:
//                MessageProtos.AudioChatMessageInfo audioChatMessageInfo = MessageProtos.AudioChatMessageInfo.newBuilder()
////                        TODO 需要搞清楚语音发送，模型也需要更改
//                        .build();
//                chatMessageBuilder.setContent(audioChatMessageInfo.toByteString())
//                        .setMessageType(MessageProtos.ChatMessageType.AUDIO);
//                break;
//            default:
//                throw new NullPointerException("没有设置消息类型！entity.getMessageType() == null");
//
//        }
        MessageProtos.TextChatMessage chatMessage = chatMessageBuilder.setFromID(entity.getFromID())
                .setFromID(entity.getFromID())
                .setToID(entity.getToId())
                .setRelationType(entity.getRelationType())
                .build();

        MessageProtos.Message message = MessageProtos.Message.newBuilder()
                .setType(MessageProtos.Message.MessageType.CHAT_TEXT)
                .setFrom(entity.getFromID())
                .setTo(entity.getToId())
                .setTime(entity.getTime())
                .setMid(entity.getMid())
                .setData(chatMessage.toByteString())
                .build();
        return message.toByteArray();
    }


}
