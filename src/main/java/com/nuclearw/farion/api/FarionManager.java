package com.nuclearw.farion.api;

import java.util.List;

public interface FarionManager {
	public boolean messageChannel(String message);

	public boolean messageNickname(String message, String nickname);

	public List<String> getNicknames();
}