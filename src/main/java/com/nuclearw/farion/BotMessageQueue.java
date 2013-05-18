package com.nuclearw.farion;

import java.util.concurrent.LinkedBlockingQueue;

public class BotMessageQueue implements Runnable {
	private static BotMessageQueue instance;

	private LinkedBlockingQueue<BotMessage> messages = new LinkedBlockingQueue<BotMessage>();

	private BotMessageQueue() { }

	public static BotMessageQueue getInstance() {
		if(instance == null) {
			instance = new BotMessageQueue();
		}
		return instance;
	}

	@Override
	public void run() {
		BotMessage message = messages.poll();
		if(message != null) {
			Farion.bot.sendMessage(message.channel, message.message);
		}
	}

	public void queueMessage(String channel, String message) {
		messages.add(new BotMessage(channel, message));
	}

	private class BotMessage {
		protected final String channel, message;

		protected BotMessage(String channel, String message) {
			this.channel = channel;
			this.message = message;
		}
	}
}
