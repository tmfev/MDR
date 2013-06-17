package com.xclinical.mdr.client;

import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.ui.ItemBrowser;
import com.xclinical.mdr.client.iso11179.ui.JsonEditor;
import com.xclinical.mdr.client.iso11179.ui.PreviewUnavailable;

public abstract class AbstractGenericEditorFactory<T extends Item> implements GenericEditorFactory<T> {

	@Override
	public final GenericEditor<? super T> createEditor(Mode mode) {
		switch (mode) {
		case BROWSER:
			return createBrowser();

		case SIMPLE:
			GenericEditor<? super T> editor = createSimple();
			if (editor != null) {
				return editor;
			}
			// Fall through.
			
		case ISO11179:
			return createISO11179();

		case PREVIEW:
			return createPreview();
			
		case SOURCE:
			return createSource();

		case BULK:
			return createBulk();
			
		default:
			throw new UnsupportedOperationException();
		}
	}

	protected abstract GenericEditor<T> createISO11179();

	protected GenericEditor<T> createSource() {
		return new JsonEditor<T>();
	}

	protected GenericEditor<? super T> createBrowser() {
		return new ItemBrowser();
	}

	protected GenericEditor<? super T> createSimple() {
		return null;
	}

	protected GenericEditor<? super T> createPreview() {
		return new PreviewUnavailable<T>();
	}
	
	protected GenericEditor<? super T> createBulk() {
		return null;
	}

	public LazyList getChildren(T obj) {
		return null;
	};	
}
