package com.example.chatbot.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.chatbot.dto.HistoryDTO;
import com.example.chatbot.dto.HistoryDTO.HistoryItem;

@Service
public class ChatHistoryService {
	private final Clock clock;
	private final AtomicLong sequence = new AtomicLong();
	private final HistoryDTO history = new HistoryDTO();

	public ChatHistoryService() {
		this(Clock.systemUTC());
	}

	ChatHistoryService(Clock clock) {
		this.clock = clock;
	}

	public RequestMetadata captureRequest() {
		return new RequestMetadata(Instant.now(clock), sequence.getAndIncrement());
	}

	public synchronized void recordSuccessfulExchange(
			String message, String response, RequestMetadata requestMetadata) {
		history.getItems().add(new HistoryItem(
				message,
				response,
				requestMetadata.sentAt(),
				requestMetadata.sequence()));
	}

	public synchronized List<HistoryItem> getHistory() {
		return history.getItems().stream()
				.sorted(Comparator.comparing(HistoryItem::getSentAt)
						.thenComparingLong(HistoryItem::getSequence))
				.toList();
	}

	public record RequestMetadata(Instant sentAt, long sequence) {
	}
}
