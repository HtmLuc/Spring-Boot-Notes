package com.grupo8.ia;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import reactor.core.publisher.Mono;

@Service
public class StudyNotesServiceOpenAi implements StudyNotesService {
  private final WebClient webClient;

  public StudyNotesServiceOpenAi(WebClient.Builder builder, @Value("${openai.api.key}") String apiKey) {
    this.webClient = builder
        .baseUrl("https://api.openai.com/v1/chat/completions")
        .defaultHeader("Authorization", String.format("Bearer %s", apiKey))
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

  @Override
  public Mono<String> createStudyNotes(String topic) {
    ChatGPTRequest request = createChatRequest(topic);

    return webClient.post()
        .bodyValue(request)
        .retrieve()
        .bodyToMono(ChatGPTResponse.class)
        .map(response -> response.choices().get(0).message().content());
  }

  private ChatGPTRequest createChatRequest(String topic) {
    String question = "Quais são os pontos chaves que devo estudar sobre o seguinte assunto: " + topic;

    return new ChatGPTRequest(
        "gpt-4o-mini",
        List.of(new Message("user", question)),
        0.7
    );
  }
}

record ChatGPTRequest(
    String model,
    List<Message> messages,
    @JsonProperty("temperature") Double temperature
) {}

record Message(String role, String content) {}

record ChatGPTResponse(List<Choice> choices) {}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
record Choice(Message message) {}

record MessageResponse(String role, String content) {}