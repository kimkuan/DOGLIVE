package com.ssafy.api.response;

import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomGetRes{

    private ChatRoom chatRoom;

    private List<String> userNameList; // 채팅방에 참여중인 회원의 닉네임 목록

    private int unReadCount;

    public ChatRoomGetRes (ChatRoom chatRoom, List<String> userNameList, int unReadCount) {
        setChatRoom(chatRoom);
        setUserNameList(userNameList);
        setUnReadCount(unReadCount);
    }
}
