package com.ssafy.db.repository.chat;
import com.ssafy.db.entity.chat.ChatMessage;
import com.ssafy.db.entity.chat.ChatRoom;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<List<ChatMessage>> findAllByRoomId(ChatRoom roomId);

}
