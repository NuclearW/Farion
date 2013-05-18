package com.nuclearw.farion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.pircbotx.PircBotX;

public class Bot extends PircBotX {
	public boolean willfulDisconnect = false;

	protected void clearQueue() {
		try {
			// Reflection time!
			Field queueField = this.outputThread.getClass().getDeclaredField("queue");
			queueField.setAccessible(true);
			Object queueObject = queueField.get(this.outputThread);
			Method clearMethod = queueObject.getClass().getMethod("clear");
			clearMethod.setAccessible(true);
			clearMethod.invoke(queueObject);
			clearMethod.setAccessible(false);
			queueField.setAccessible(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}