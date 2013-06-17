package com.xclinical.mdr.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.xclinical.mdr.client.res.StandardImageBundle;

public class RatingWidget extends Composite implements HasValueChangeHandlers<Integer>, HasClickHandlers {

	private HorizontalPanel panel;
	
	private static ImageResource ratedImage = StandardImageBundle.INSTANCE.star();

	private static ImageResource halfRatedImage = StandardImageBundle.INSTANCE.starHalf();
	
	private static ImageResource unratedImage = StandardImageBundle.INSTANCE.starHollow();

	private float rating;
	
	public RatingWidget() {
		panel = new HorizontalPanel();		
		initWidget(panel);		
		setMaxRating(5);
		sinkEvents(Event.ONCLICK);
	}

	public void setMaxRating(int max) {
		panel.clear();
		
		for (int i = 0; i < max; i++) {
			Image img = new Image(unratedImage);

			Handlers h = new Handlers(i + 1);
			img.addHandler(h, MouseOverEvent.getType());
			img.addHandler(h, MouseOutEvent.getType());
			img.addHandler(h, ClickEvent.getType());
			panel.add(img);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
	    return addHandler(handler, ValueChangeEvent.getType());
	}	
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
	    return addHandler(handler, ClickEvent.getType());
	}	
	
	public float getValue() {
		return rating;
	}

	public void setValue(float value) {
		rating = value;
		
		for (int i = 0; i < panel.getWidgetCount(); i++) {
			Image img = (Image)panel.getWidget(i);
			final ImageResource r;
			
			if (value > 0.25) {
				if (value < 0.75) {
					r = halfRatedImage;
				}
				else {
					r = ratedImage;
				}
			}
			else {
				r = unratedImage;
			}
			img.setResource(r);
			
			value--;
		}
	}

	private class Handlers implements MouseOverHandler, MouseOutHandler, ClickHandler {
		private final int rating;
		
		Handlers(int rating) {
			this.rating = rating;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			ValueChangeEvent.fire(RatingWidget.this, rating);
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
		}		
	}
}
