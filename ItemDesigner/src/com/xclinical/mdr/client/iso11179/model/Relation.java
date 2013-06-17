package com.xclinical.mdr.client.iso11179.model;


public class Relation extends Concept {

	public static final String URN = "urn:mdr:Relation";

	public static final String ROLES = "Relation.roles";

	public static final String LINKS = "Relation.links";

	protected Relation() {
	}

	public final LazyList getRoleList() {
		return LazyList.of(ROLES, this);
	}

	public final LazyList getLinkList() {
		return LazyList.of(LINKS, this);
	}
}
