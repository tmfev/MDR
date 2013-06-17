package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.RateCommand;
import com.xclinical.mdr.client.iso11179.model.Rating;
import com.xclinical.mdr.client.res.MdrResources;

public class RatingPopupPanel extends PopupPanel {
	
	interface Binder extends UiBinder<Widget, RatingPopupPanel> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	Label summary;
	
	@UiField
	RatingWidget myRating;

	@UiField
	PushButton submit;
	
	private final Rating rating;
	
	private final RatingPanel panel;
	
	public RatingPopupPanel(RatingPanel panel, Rating rating) {
		super(true);
		
		this.rating = rating;
		this.panel = panel;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		myRating.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				myRating.setValue(event.getValue());
			}
		});
		
		myRating.setValue(rating.getValue());
		summary.setText(rating.getAverageRating() + " from " + rating.getVotes() + " votes");
		
		addStyleName(MdrResources.INSTANCE.css().popup());		
	}
	
	@UiHandler("submit")
	public void onSubmit(ClickEvent event) {
		Rating r = Rating.rate(rating.getTarget(), (int)myRating.getValue());		
		RateCommand command = RateCommand.newInstance(r);
		CommandExecutor<Rating> executor = new CommandExecutor<Rating>();
		executor.setOnResult(new OnSuccessHandler<Rating>() {					
			@Override
			public void onSuccess(Rating obj) {
				panel.setValue(obj);						
			}
		});
		executor.invoke(RateCommand.PATH, command);						
		hide();
	}
}
