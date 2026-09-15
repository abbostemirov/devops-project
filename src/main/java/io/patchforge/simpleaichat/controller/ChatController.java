package io.patchforge.simpleaichat.controller;


import io.patchforge.simpleaichat.entity.ChatHistory;
import io.patchforge.simpleaichat.repository.ChatHistoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ChatController {


    private final ChatClient chatClient;
    private final ChatHistoryRepository chatHistoryRepository;

    public ChatController(ChatClient.Builder builder, ChatHistoryRepository chatHistoryRepository) {
        this.chatClient = builder.build();
        this.chatHistoryRepository = chatHistoryRepository;
    }


    @GetMapping(value = "/api/chat")
    public String chat(@RequestParam String message) {


        String aiResponse = chatClient.prompt()
                .user(message)
                .call()
                .content();

        ChatHistory build = ChatHistory.builder()
                .senderMessage(message)
                .aiMessage(aiResponse)
                .build();

        chatHistoryRepository.save(build);

        return aiResponse;
    }

    @GetMapping("/api/history")
    public List<HistoryDto> history() {

        return chatHistoryRepository.findAll()
                .stream()
                .map(chatHistory -> new HistoryDto(chatHistory.getId(),chatHistory.getSenderMessage(),chatHistory.getAiMessage()))
                .toList();
    }

    record HistoryDto(Integer id, String senderMessage, String aiResponse){}
}
