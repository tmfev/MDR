package com.mictale.jsonite;

public enum JsonType {
	OBJECT("Object"),
	ARRAY("Array"),
	STRING("String"),
	NUMBER("Number"),
	BOOLEAN("Boolean"),
	NULL("Null");
	
	private String typeName;
	
	private JsonType(String typeName) {
		this.typeName = typeName;
	}
	
	public String getTypeName() {
		return typeName;
	}
}
