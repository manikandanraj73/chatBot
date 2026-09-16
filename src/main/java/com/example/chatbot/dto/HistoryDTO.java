package com.example.chatbot.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HistoryDTO {
	private final List<HistoryItem> items = new ArrayList<>();

	public List<HistoryItem> getItems() {
		return items;
	}

	public static class HistoryItem {
		private final String message;
		private final String response;
		private final Instant sentAt;
		private final long sequence;

		public HistoryItem(String message, String response, Instant sentAt, long sequence) {
			this.message = message;
			this.response = response;
			this.sentAt = sentAt;
			this.sequence = sequence;
		}

		public String getMessage() {
			return message;
		}

		public String getResponse() {
			return response;
		}

		public Instant getSentAt() {
			return sentAt;
		}

		public long getSequence() {
			return sequence;
		}
	}

	public static class HistoryResponseItem {
		private final String message;
		private final String response;
		private final Instant sentAt;

		public HistoryResponseItem(HistoryItem item) {
			this.message = item.getMessage();
			this.response = item.getResponse();
			this.sentAt = item.getSentAt();
		}

		public String getMessage() {
			return message;
		}

		public String getResponse() {
			return response;
		}

		public Instant getSentAt() {
			return sentAt;
		}
	}
}
