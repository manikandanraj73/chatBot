package com.example.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.chatbot.dto.HistoryDTO.HistoryItem;

class ChatHistoryServiceTest {
	private ChatHistoryService service;

	@BeforeEach
	void setUp() {
		service = new ChatHistoryService(
				Clock.fixed(Instant.parse("2026-09-16T10:30:00Z"), ZoneOffset.UTC));
	}

	@Test
	void recordsCompletedExchangeWithUtcTimestamp() {
		ChatHistoryService.RequestMetadata metadata = service.captureRequest();
		service.recordSuccessfulExchange("Hello", "Hi there", metadata);

		List<HistoryItem> history = service.getHistory();
		assertEquals(1, history.size());
		assertEquals("Hello", history.get(0).getMessage());
		assertEquals("Hi there", history.get(0).getResponse());
		assertEquals(Instant.parse("2026-09-16T10:30:00Z"), history.get(0).getSentAt());
	}

	@Test
	void ordersEntriesByTimestampThenReceiptSequence() {
		ChatHistoryService.RequestMetadata first = service.captureRequest();
		ChatHistoryService.RequestMetadata second = service.captureRequest();
		service.recordSuccessfulExchange("second", "response-2",
				new ChatHistoryService.RequestMetadata(
						Instant.parse("2026-09-16T10:31:00Z"), second.sequence()));
		service.recordSuccessfulExchange("first", "response-1", first);

		assertEquals(List.of("first", "second"),
				service.getHistory().stream().map(HistoryItem::getMessage).toList());
	}

	@Test
	void returnsNewServiceWithEmptyHistory() {
		service.recordSuccessfulExchange("Hello", "Hi there", service.captureRequest());

		ChatHistoryService restartedService = new ChatHistoryService(
				Clock.fixed(Instant.parse("2026-09-16T10:30:00Z"), ZoneOffset.UTC));

		assertEquals(0, restartedService.getHistory().size());
	}
}
