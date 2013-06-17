package com.mictale.jsonite.stream;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.mictale.jsonite.JsonValue;

/**
 * Provides transformation utilities.
 * 
 * @author michael@mictale.com
 */
public final class Transformation {

	private Transformation() {
	}

	/**
	 * Copies the contents of the specified {@link Producer} to a specified {@link Consumer}.
	 * 
	 * @param consumer is the consumer of the operation.
	 * @param producer is the producer of the operation.
	 * @throws BrokenStreamException
	 */
	public static void copy(Consumer consumer, Producer producer) throws BrokenStreamException {
		producer.copyTo(consumer);
	}

	/**
	 * Copies the tokens from the specified producer to the specified {@link Consumer}.
	 * 
	 * This implementation simply wraps the specified {@link Reader} into a {@link Producer}
	 * and calls {@link #copy(Consumer, Producer)}.
	 * 
	 * @param consumer is the consumer of the operation.
	 * @param producer is the producer of the operation.
	 * @throws BrokenStreamException
	 */
	public static void copy(Consumer consumer, Reader producer) throws BrokenStreamException {
		copy(consumer, new JsonStreamProducer(producer));
	}
	
	/**
	 * Parses the specified JSON string to the specified {@link Consumer}.
	 * 
	 * This implementation simply wraps the specified {@link Reader} into a {@link Producer}
	 * and calls {@link #copy(Consumer, Producer)}.
	 * 
	 * @param json is the JSON string.
	 * @param producer is the producer of the operation.
	 * @throws BrokenStreamException
	 */
	public static void copy(Consumer consumer, String json) throws BrokenStreamException {
		copy(consumer, new JsonStreamProducer(json));
	}

	public static void copy(Writer consumer, boolean prettify, Producer producer) throws BrokenStreamException {
		copy(new JsonStreamConsumer(consumer, prettify), producer);
	}

	public static String asString(Producer producer, boolean prettify) {
		StringWriter sw = new StringWriter();
		copy(sw, prettify, producer);
		return sw.toString();
	}

	public static JsonValue asValue(Producer producer) {
		JsonValueConsumer c = new JsonValueConsumer();
		copy(c, producer);
		return c.getValue();
	}
	
	public static JsonValue parse(Reader reader) {
		JsonValueConsumer c = new JsonValueConsumer();
		copy(c, reader);
		return c.getValue();
	}

	public static JsonValue parse(String str) {
		JsonValueConsumer c = new JsonValueConsumer();
		copy(c, str);
		return c.getValue();
	}
}
