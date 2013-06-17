package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.iso11179.model.Rating;

public class RatingPanel extends Composite implements HasValue<Rating>, IsEditor<Editor<Rating>> {

	interface Binder extends UiBinder<Widget, RatingPanel> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField
	RatingWidget averageRating;

	private Rating rating;

	public RatingPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		averageRating.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RatingPopupPanel panel = new RatingPopupPanel(RatingPanel.this, rating);
				panel.showRelativeTo(RatingPanel.this);
			}
		});		
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Rating> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Rating getValue() {
		return rating;
	}

	@Override
	public void setValue(Rating value) {
		rating = value;
		averageRating.setValue(rating.getAverageRating());
	}

	@Override
	public void setValue(Rating value, boolean fireEvents) {
		setValue(value);

		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public Editor<Rating> asEditor() {
		return TakesValueEditor.of(this);
	}	
}
