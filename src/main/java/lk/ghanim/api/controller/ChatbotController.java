package lk.ghanim.api.controller;

import lk.ghanim.api.dto.request.ChatRequest;
import lk.ghanim.api.dto.response.ChatResponse;
import lk.ghanim.api.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest request
            ) {
        return ResponseEntity.ok(chatbotService.chat(request));
    }
}
