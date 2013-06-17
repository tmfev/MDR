package com.xclinical.mdr.client.iso11179.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

/**
 * The common super class for all ISO entities.
 * 
 * @author ms@xclinical.com
 */
public abstract class Item extends JavaScriptObject implements HasItem, HasKey {

	public static final String ADD_TYPE = ".AndType";
	
	public static final String SEARCH_BY_RATING = "Item.searchByRating";

	public static final String SEARCH_BY_RATING_AND_TYPE = SEARCH_BY_RATING + ADD_TYPE;

	public static final String SEARCH_BY_REFERENCE = "Item.searchByReference";

	public static final String SEARCH_BY_REFERENCE_AND_TYPE = SEARCH_BY_REFERENCE + ADD_TYPE;
	
	public static final String SEARCH_BY_CLICKS = "Item.searchByClicks";
	
	public static final String SEARCH_BY_CLICKS_AND_TYPE = SEARCH_BY_CLICKS + ADD_TYPE;

	public static final String SEARCH_BY_UPDATES = "Item.searchByUpdates";
	
	public static final String SEARCH_BY_UPDATES_AND_TYPE = SEARCH_BY_UPDATES + ADD_TYPE;
	
	public static final String ID = "id";
	
	public static final String DISPLAY_NAME = "displayName";

	public static final String RATING = "rating";

	public static final RemoteMethod APPEND_CLASSIFIER = RemoteMethod.of("net.xclinical.iso11179.Item.appendClassifier");

	public static final RemoteMethod REMOVE_CLASSIFIER = RemoteMethod.of("net.xclinical.iso11179.Item.removeClassifier");
		
	public static final String[] ALL_URNS = {
		Assertion.URN, Characteristic.URN, Concept.URN, ConceptSystem.URN, ConceptualDomain.URN, Context.URN, DataElement.URN, DataElementConcept.URN,
		DataType.URN, Definition.URN, Designation.URN, Dimensionality.URN, Document.URN, LanguageIdentification.URN,
		Namespace.URN, ObjectClass.URN, PermissibleValue.URN, ReferenceDocument.URN, Relation.URN, RelationRole.URN, Slot.URN,
		UnitOfMeasure.URN, ValueDomain.URN, ValueMeaning.URN, Link.URN, LinkEnd.URN
	};
		
	protected Item() {}
	
	public final native String getId() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.Item::ID];
	}-*/;

	/**
	 * Returns <code>true</code> if this object has never been stored.
	 * @return
	 */
	public final boolean isNew() {
		Key k = Key.parse(getId());
		return k.getValue() == null;
	}
	
	public final native String getDisplayName() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.Item::DISPLAY_NAME];
	}-*/;

	public final native void setDisplayName(String displayName) /*-{
		this[@com.xclinical.mdr.client.iso11179.model.Item::DISPLAY_NAME] = displayName;
	}-*/;
	
	public final native Rating getRating() /*-{
		if (this[@com.xclinical.mdr.client.iso11179.model.Item::RATING] == undefined) {
			this[@com.xclinical.mdr.client.iso11179.model.Item::RATING] = {};
		}
		return this[@com.xclinical.mdr.client.iso11179.model.Item::RATING];
	}-*/;

	public final String getJson() {
		return new JSONObject(this).toString();
	}
	
	@Override
	public final Item getItem() {
		return this;
	}

	@Override
	public final Key getKey() {
		String id = getId();
		if (id == null) {
			return null;
		}
		else {
			return Key.parse(id);
		}
	}

	public static final native <T extends Item> T ref(T t) /*-{
		if (t == null) return null;
		
		return {
			id : t.id,
			displayName : t.displayName
		};
	}-*/;
	
	public static native <T extends Item> T newInstance(String urn) /*-{
		return {id:urn};
	}-*/;

	public static native <T extends Item> JsArray<T> newArray() /*-{
		return [];
	}-*/;
}
