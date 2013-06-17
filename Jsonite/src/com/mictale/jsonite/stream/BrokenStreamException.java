package com.mictale.jsonite.stream;

import com.mictale.jsonite.JsonException;

/**
 * @author michael@mictale.com
 */
public class BrokenStreamException extends JsonException {

	private static final long serialVersionUID = -8499395581017262026L;

	public BrokenStreamException(String message) {
		super(message);
	}

	public BrokenStreamException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BrokenStreamException(Throwable cause) {
		super(cause);
	}
}
