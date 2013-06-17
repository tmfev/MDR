package com.xclinical.mdr.client.iso11179.model;


public abstract class AbstractDesingatableItem extends Item implements DesignatableItem {

	public static final String DESIGNATIONS = "Item.designations";

	public static final String DEFINITIONS = "Item.definitions";

	public static final String CLASSIFIER = "Item.classifier";

	public static final String REMOVE_CLASSIFIERS = "Item.removeClassifiers";
	
	protected AbstractDesingatableItem() {}
	
	public final LazyList getDesignationList() {
		return LazyList.of(DESIGNATIONS, this);
	}

	public final LazyList getDefinitionList() {
		return LazyList.of(DEFINITIONS, this);
	}
	
	public final LazyList getClassifierList() {
		return LazyList.of(CLASSIFIER, this).setAppender(APPEND_CLASSIFIER).setRemover(REMOVE_CLASSIFIER);
	}
}
