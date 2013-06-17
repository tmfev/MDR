/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package com.mictale.jsonite;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;

import com.mictale.jsonite.stream.JsonStreamProducer;
import com.mictale.jsonite.stream.JsonValueConsumer;
import com.mictale.jsonite.stream.JsonValueProducer;
import com.mictale.jsonite.stream.JsonVisitor;
import com.mictale.jsonite.stream.Transformation;

/**
 * The most basic JSON type.
 * <p>
 * Almost everything in JSON is a {@link JsonValue}. Instances of this class can
 * be added to {@link JsonObject}s and {@link JsonArray}s.
 * <p>
 * To create new instances, use {@link #of(Object)} or one of its overloads
 * {@link JsonBoolean#of(Boolean)}, {@link JsonNumber#of(Number)} or
 * {@link JsonString#of(String)}
 * <p>
 * To create an object, use one of the {@link JsonObject} constructors and to
 * create an array, use one of the {@link JsonArray} constructors.
 * <p>
 * To quickly cast a value, use one of the <code>asXXX()</code> methods:
 * {@link #asBoolean()}, {@link #asNumber()}, {@link #asString()},
 * {@link #asArray()} or {@link #asObject()}. These methods will throw an
 * {@link JsonConversionException} if the specified object is not of the target
 * type. If you want to check the type, use one of the <code>isXXX()</code>
 * methods: {@link #isBoolean()}, {@link #isNumber()}, {@link #isString()}
 * {@link #isArray()} and {@link #isObject()}.
 * <p>
 * To convert a JSON primitive back to its Java value, use one of {@link #byteValue()},
 * {@link #shortValue()}, {@link #intValue()}, {@link #longValue()}, {@link #floatValue()},
 * {@link #doubleValue()} or {@link #stringValue()}. 
 *  
 * @author michael@mictale.com
 */
public abstract class JsonValue {

	/**
	 * Creates a value from a specified Java primitive.
	 * 
	 * The method is guaranteed to never return <code>null</code>.
	 * 
	 * The following is a list of supported object types:
	 * 
	 * <table>
	 * <tr>
	 * <th>Java Type</th>
	 * <th>Json Type</th>
	 * </tr>
	 * <tr>
	 * <td>{@link JsonValue}</td>
	 * <td>{@link JsonValue}</td>
	 * </tr>
	 * <tr>
	 * <td><code>null</code></td>
	 * <td>{@link JsonValue#NULL}</td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>{@link JsonValue#TRUE}</td>
	 * </tr>
	 * <tr>
	 * <td><code>{@link java.lang.Boolean#TRUE}</code></td>
	 * <td>{@link JsonValue#TRUE}</td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>{@link JsonValue#FALSE}</td>
	 * </tr>
	 * <tr>
	 * <td><code>{@link java.lang.Boolean#FALSE}</code></td>
	 * <td>{@link JsonValue#FALSE}</td>
	 * </tr>
	 * <tr>
	 * <td><code>{@link java.lang.String}</code></td>
	 * <td>{@link JsonString}</td>
	 * </tr>
	 * <tr>
	 * <td><code>{@link java.lang.Number}</code></td>
	 * <td>{@link JsonNumber}</td>
	 * </tr>
	 * <tr>
	 * <td><code>{@link Collection}</code></td>
	 * <td>{@link JsonArray}</td>
	 * </tr>
	 * </table>
	 * 
	 * @param obj
	 *            is the Java primitive object.
	 * @return the JSON value.
	 * @throws JsonException
	 *             when the object type could not be converted.
	 */
	public static JsonValue of(Object obj) {
		if (obj == null) {
			return JsonValue.NULL;
		} else if (obj instanceof JsonValue) {
			return (JsonValue) obj;
		} else if (obj instanceof HasJson) {
			return ((HasJson)obj).asJson();
		} else if (obj instanceof Boolean) {
			return JsonBoolean.of((Boolean) obj);
		} else if (obj instanceof String) {
			return JsonString.of((String) obj);
		} else if (obj instanceof Character) {
			return JsonString.of(String.valueOf(obj));
		} else if (obj instanceof Number) {
			return JsonNumber.of((Number) obj);
		} else if (obj instanceof Date) {
			return JsonNumber.of(((Date) obj).getTime());
		} else if (obj instanceof Collection<?>) {
			Collection<?> c = (Collection<?>) obj;
			return JsonArray.of(c);
		} else {
			throw new JsonException("No conversion of " + obj.getClass());
		}
	}

	/**
	 * Represents the <code>true</code> literal object.
	 */
	public static final JsonValue TRUE = new JsonBoolean(Boolean.TRUE);

	/**
	 * Represents the <code>false</code> literal object.
	 */
	public static final JsonValue FALSE = new JsonBoolean(Boolean.FALSE);

	/**
	 * Represents the <code>null</code> literal object.
	 */
	public static final JsonValue NULL = new JsonNull();

	/**
	 * Retrieves the <code>byte</code> value of this value.
	 * 
	 * @return the byte value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public byte byteValue() {
		throw createConversionException("byte");
	}

	/**
	 * Retrieves the <code>short</code> value of this value.
	 * 
	 * @return the short value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public short shortValue() {
		throw createConversionException("short");
	}

	/**
	 * Retrieves the <code>int</code> value of this value.
	 * 
	 * @return the int value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public int intValue() {
		throw createConversionException("int");
	}

	/**
	 * Retrieves the <code>long</code> value of this value.
	 * 
	 * @return the long value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public long longValue() {
		throw createConversionException("long");
	}

	/**
	 * Retrieves the <code>float</code> value of this value.
	 * 
	 * @return the float value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public float floatValue() {
		throw createConversionException("float");
	}

	/**
	 * Retrieves the <code>ldouble</code> value of this value.
	 * 
	 * @return the double value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public double doubleValue() {
		throw createConversionException("double");
	}

	/**
	 * Retrieves the {@link String} value of this value.
	 * 
	 * @return the string value.
	 * @throws JsonException
	 *             if no conversion is available.
	 */
	public String stringValue() {
		throw createConversionException("String");
	}

	/**
	 * Checks if the value is a primitive.
	 * 
	 * Primitive values are {@link JsonBoolean}, {@link JsonString},
	 * {@link JsonNumber}, JsonValue#NULL}, {@link JsonValue#TRUE} and
	 * {@link JsonValue#FALSE}.
	 * 
	 * @return <code>true</code> if this value is a primitive.
	 */
	public boolean isPrimitive() {
		return false;
	}

	/**
	 * Checks if this is the {@link JsonValue#NULL} value.
	 * 
	 * @return <code>true</code> if this is the {@link JsonValue#NULL} value.
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * Checks if this is the {@link JsonValue#TRUE} or {@link JsonValue#FALSE}
	 * value.
	 * 
	 * @return <code>true</code> if this is the {@link JsonValue#TRUE} or
	 *         {@link JsonValue#FALSE} value.
	 */
	public boolean isBoolean() {
		return false;
	}

	/**
	 * Converts this value into a {@link JsonBoolean} if a conversion is
	 * available.
	 * 
	 * @return the converted value.
	 */
	public JsonBoolean asBoolean() {
		throw createConversionException(JsonType.BOOLEAN.getTypeName());
	}

	/**
	 * Checks if this is a {@link JsonNumber}.
	 * 
	 * @return <code>true</code> if this is a {@link JsonNumber}.
	 */
	public boolean isNumber() {
		return false;
	}

	/**
	 * Converts this value into a {@link JsonNumber} if a conversion is
	 * available.
	 * 
	 * @return the converted value.
	 */
	public JsonNumber asNumber() {
		throw createConversionException(JsonType.NUMBER.getTypeName());
	}

	/**
	 * Checks if this is a {@link JsonString}.
	 * 
	 * @return <code>true</code> if this is a {@link JsonString}.
	 */
	public boolean isString() {
		return false;
	}

	/**
	 * Converts this value into a {@link JsonString} if a conversion is
	 * available.
	 * 
	 * @return the converted value.
	 */
	public JsonString asString() {
		throw createConversionException(JsonType.STRING.getTypeName());
	}

	/**
	 * Checks if this is a {@link JsonObject}.
	 * 
	 * @return <code>true</code> if this is a {@link JsonObject}.
	 */
	public boolean isObject() {
		return false;
	}

	/**
	 * Converts this value into a {@link JsonObject} if a conversion is
	 * available.
	 * 
	 * @return the converted value.
	 */
	public JsonObject asObject() {
		throw createConversionException(JsonType.OBJECT.getTypeName());
	}

	/**
	 * Checks if this is a {@link JsonArray}.
	 * 
	 * @return <code>true</code> if this is a {@link JsonArray}.
	 */
	public boolean isArray() {
		return false;
	}

	/**
	 * Converts this value into a {@link JsonArray} if a conversion is
	 * available.
	 * 
	 * @return the converted value.
	 */
	public JsonArray asArray() {
		throw createConversionException(JsonType.ARRAY.getTypeName());
	}

	private JsonException createConversionException(String type) {
		return new JsonConversionException("No conversion of " + getType().getTypeName() + " to " + type + " defined");
	}

	/**
	 * Parses a JSON string into an object representation.
	 * 
	 * @param json
	 *            is the string representation.
	 * @return the object representation.
	 * @throws IOException
	 */
	public static JsonValue parse(String json) {
		if (json == null) {
			return JsonValue.NULL;
		}
		else {
			JsonStreamProducer p = new JsonStreamProducer(json);
			JsonValueConsumer c = new JsonValueConsumer();
			Transformation.copy(c, p);
	
			return c.getValue();
		}
	}

	@Override
	public final String toString() {
		return toString(true);
	}

	public final String toString(boolean prettify) {
		StringWriter w = new StringWriter();
		try {
			writeTo(w, prettify);
		} catch (IOException e) {
			// Intentionally ignored.
		}
		return w.toString();		
	}
	
	public abstract JsonType getType();
	
	public abstract void accept(JsonVisitor visitor);
	
	/**
	 * Serializes this object into a JSON string.
	 * 
	 * @param writer
	 *            is the target to write to.
	 * @throws IOException
	 *             if the writer causes an exception.
	 */
	public final void writeTo(Writer writer, boolean prettify) throws IOException {
		Transformation.copy(writer, prettify, new JsonValueProducer(this));
	}
}
