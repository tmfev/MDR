package com.xclinical.mdr.client.iso11179.model;


public interface DesignatableItem extends ClassifiedItem {

	LazyList getDesignationList();

	LazyList getDefinitionList();
}
