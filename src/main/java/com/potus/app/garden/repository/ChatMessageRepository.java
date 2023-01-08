package com.potus.app.garden.repository;

import com.potus.app.garden.model.ChatMessage;
import com.potus.app.garden.model.GardenMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
}
