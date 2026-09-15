package io.patchforge.simpleaichat.repository;

import io.patchforge.simpleaichat.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Integer> {


}
