package com.grupo8.ia;

import reactor.core.publisher.Mono;

public interface StudyNotesService {
  Mono<String> createStudyNotes(String topic);
}
