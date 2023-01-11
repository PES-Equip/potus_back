package com.potus.app.garden.repository;

import com.potus.app.garden.model.ChatMessage;
import com.potus.app.garden.model.GardenMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByRoom(String room, Pageable pageable);

    List<ChatMessage> findByDateLessThanEqualAndRoom(Date date, String room, Pageable pageable);
}
