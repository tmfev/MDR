package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.res.ItemResources;
import com.xclinical.mdr.client.res.StandardImageBundle;

public class ItemRenderer extends AbstractSafeHtmlRenderer<Item> {

	interface Template extends SafeHtmlTemplates {

		@Template("<div class='{0}'></div>")
		SafeHtml removeButton(String clazz);
		
		@Template("<div class='{0}' style='white-space: nowrap;overflow:hidden'>{1}{2}<sub>{3}</sub>{4}</div>")
		SafeHtml nameIconAndId(String clazz, SafeHtml stars, SafeHtml value, String id, SafeHtml removeButton);

		@Template("<div class='{0}' style='white-space: nowrap;overflow:hidden'>{1}{2}{3}</div>")
		SafeHtml nameAndIcon(String clazz, SafeHtml icon, SafeHtml value, SafeHtml removeButton);
		
		@Template("(no name)")
		SafeHtml noName();

		@Template("(no value)")
		SafeHtml noValue();
	}

	private static final Template TEMPLATE = GWT.create(Template.class);
	
	private boolean showId;

	private boolean showRating;
	
	private boolean showRemove;
	
	public ItemRenderer() {
		this(true, false);
	}
	
	public ItemRenderer(boolean showId, boolean showRating) {
		ItemResources.INSTANCE.style().ensureInjected();
		this.showId = showId;
		this.showRating = showRating;
	}
	
	public void showRemove(boolean show) {
		showRemove = show;
	}
	
	@Override
	public SafeHtml render(Item object) {
		final SafeHtml result;
		if (object != null) {
			SafeHtml stars;
			
			if (showRating) {
				int rating = Math.round(object.getRating().getAverageRating());
				ImageResource res;
				switch(rating) {
				case 1:
					res = StandardImageBundle.INSTANCE.star1();
					break;
				case 2:
					res = StandardImageBundle.INSTANCE.star2();
					break;
				case 3:
					res = StandardImageBundle.INSTANCE.star3();
					break;
				case 4:
					res = StandardImageBundle.INSTANCE.star4();
					break;
				case 5:
					res = StandardImageBundle.INSTANCE.star();
					break;
				default:
					res = StandardImageBundle.INSTANCE.starHollow();
					break;
				}
	
				ImageResourceRenderer imageRenderer = new ImageResourceRenderer();
				stars = imageRenderer.render(res);
			}
			else {
				stars = SafeHtmlUtils.EMPTY_SAFE_HTML;
			}
			
			String displayName = object.getDisplayName();
			
			final SafeHtml d;
			if (displayName == null) {
				d = TEMPLATE.noName();
			}
			else {
				d = SafeHtmlUtils.fromString(displayName);
			}

			SafeHtml removeButton;
			
			if (showRemove) {
				removeButton = TEMPLATE.removeButton(ItemResources.INSTANCE.style().removeButton());
			}
			else {
				removeButton = SafeHtmlUtils.EMPTY_SAFE_HTML;
			}
			
			if (showId) {
				String id = object.getId();
				if (id == null) id = "(no id)";
				result = TEMPLATE.nameIconAndId(ItemResources.INSTANCE.style().item(), stars, d, id, removeButton);
			}
			else {
				result = TEMPLATE.nameAndIcon(ItemResources.INSTANCE.style().item(), stars, d, removeButton);				
			}
		} else {
			result = TEMPLATE.noValue();
		}

		return result;
	}

}
