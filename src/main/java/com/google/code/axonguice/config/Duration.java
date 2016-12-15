package com.google.code.axonguice.config;

import java.util.concurrent.TimeUnit;

public class Duration {

	final long time;
	final TimeUnit unit;

	public Duration(long time, TimeUnit unit) {
		super();
		this.time = time;
		this.unit = unit;
	}

	public long getTime() {
		return time;
	}

	public TimeUnit getUnit() {
		return unit;
	}

}
