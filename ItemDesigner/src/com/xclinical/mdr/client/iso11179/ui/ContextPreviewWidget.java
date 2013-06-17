package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.ContextPreview;
import com.xclinical.mdr.client.iso11179.model.DataElementPreview;
import com.xclinical.mdr.client.res.MdrResources;

/**
 * A widget to render a preview of a context, its child contexts and data elements.
 * 
 * @author ms@xclinical.com
 */
public class ContextPreviewWidget extends Composite {

	static {
		MdrResources.INSTANCE.css().ensureInjected();
	}
	
	interface Binder extends UiBinder<Widget, ContextPreviewWidget> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Style extends CssResource {
		String title();
	}
	
	@UiField
	FlexTable table;
	
	@UiField
	HTML title;
	
	public ContextPreviewWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setValue(ContextPreview value) {
		table.removeAllRows();
		
		title.setText(value.getDesignationSign());
		title.setTitle(value.getDefinitionText());
		
		for (ContextPreview context : value.getChildren()) {
			ContextPreviewWidget c = new ContextPreviewWidget();
			int cnt = table.getRowCount();
			table.setWidget(cnt, 0, c);
			table.getFlexCellFormatter().setColSpan(cnt, 0, 3);
			c.setValue(context);
		}

		for (DataElementPreview dataElement : value.getDataElements()) {
			DataElementPreviewWidget.addRow(table, dataElement);
		}		
	}	
}
