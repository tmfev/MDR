package com.xclinical.mdr.client.iso11179.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.CodeListElement;
import com.xclinical.mdr.client.iso11179.model.DataElementPreview;
import com.xclinical.mdr.client.res.MdrResources;

public class DataElementPreviewWidget extends Composite {

	interface Binder extends UiBinder<Widget, DataElementPreviewWidget> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Template extends SafeHtmlTemplates {
		@Template("{0}: {1}")
		SafeHtml checkbox(String code, String text);
	}

	private static final Template TEMPLATE = GWT.create(Template.class);
	
	@UiField
	Label designation;
	
	@UiField
	TextBox textBox;

	@UiField
	FlowPanel values;
	
	@UiField
	Label unit;
	
	private DataElementPreview obj;
	
	public DataElementPreviewWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setValue(DataElementPreview value) {
		obj = value;
		
		designation.setText(obj.getDesignationSign());
		designation.setTitle(obj.getDefinitionText());
		
		List<CodeListElement> codeList = obj.getCodeList();
		if (codeList.size() > 0) {
			textBox.setVisible(false);
			
			for (CodeListElement elm : codeList) {
				values.add(new SimplePanel(new RadioButton("group", TEMPLATE.checkbox(elm.getCode(), elm.getText()))));
			}
		}
		
		unit.setText(obj.getUnitOfMeasure());		
	}
	
	public static void addRow(FlexTable table, DataElementPreview dataElement) {
		int row = table.getRowCount();
		
		Label designation = new Label(dataElement.getDesignationSign() + ":");
		designation.setTitle(dataElement.getDefinitionText());
		designation.addStyleName(MdrResources.INSTANCE.css().previewLabel());
		
		table.setWidget(row, 0, designation);
		table.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
		table.getColumnFormatter().setWidth(0, "50%");
		
		List<CodeListElement> codeList = dataElement.getCodeList();
		if (codeList.size() == 0) {
			FlowPanel p = new FlowPanel();
			p.add(new TextBox());
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			String format = dataElement.getFormat();
			if (format != null) {
				sb.appendEscaped(format);
			}
			
			if (dataElement.getMaximumCharacterQuantity() > 0) {
				sb.appendEscaped(" (" + dataElement.getMaximumCharacterQuantity() + ")");								
			}
			
			SafeHtml html = sb.toSafeHtml();
			if (html.asString().length() > 0) {				
				p.add(new HTML(SafeHtmlUtils.fromTrustedString("<i>" + html.asString() + "</i>")));								
			}
			table.setWidget(row, 1, p);
		}
		else {
			FlowPanel values = new FlowPanel();
			
			for (CodeListElement elm : codeList) {
				String code = elm.getCode();
				String text = elm.getText();
				values.add(new SimplePanel(new RadioButton("group", TEMPLATE.checkbox(code == null ? "(empty)" : code, text == null ? "(empty)" : text))));
			}
			
			table.setWidget(row, 1, values);			
		}
		
		table.setText(row, 2, dataElement.getUnitOfMeasure());				
	}
}
