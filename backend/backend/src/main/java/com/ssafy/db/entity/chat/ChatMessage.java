package com.ssafy.db.entity.chat;
import com.ssafy.api.request.ChatMessagePostReq;
import com.ssafy.db.entity.auth.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 채팅 데이터를 저장할 ChatMessage Entity
 */
@Entity
@Table(name="chat_message", schema = "chat")
@Getter
@Setter
public class ChatMessage extends BaseEntity {

    @Column(name="user_id")
    private String userId;          //채팅을 보낸 사용자 ID

    @ManyToOne
    @JoinColumn(name="room_id", nullable = false)
    private ChatRoom roomId;            //채팅방 ID

    @Column(columnDefinition = "TEXT")
    private String message;         //채팅 Message 데이터

    @Column(columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private LocalDateTime sendTimeAt;    //채팅 보낸 시간

}
