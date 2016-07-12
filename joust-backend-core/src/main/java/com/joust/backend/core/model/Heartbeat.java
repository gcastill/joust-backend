package com.joust.backend.core.model;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Heartbeat {

	private String message;
	private String version;
	private String buildLabel;

}
