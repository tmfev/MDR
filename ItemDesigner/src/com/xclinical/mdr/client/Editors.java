package com.xclinical.mdr.client;

import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.reflect.Reflection;
import com.xclinical.mdr.repository.HasKey;
import com.xclinical.mdr.repository.Key;

/**
 * Instantiates new {@link ItemEditor}s.
 * 
 * @author ms@xclinical.com
 */
public final class Editors {

	private Editors() {}
	
	public static <T> GenericEditor<? super T> createEditor(HasKey hasKey, Mode mode) {
		if (hasKey == null) throw new NullPointerException("No key");
		Key key = hasKey.getKey();		
		if (key == null) throw new NullPointerException(hasKey + " provided null key");
		
		GenericEditorFactory<T> factory = Reflection.newInstance(key.getName());
		GenericEditor<? super T> editor = factory.createEditor(mode);		
		return editor;
	}
	
}
