package com.xclinical.mdr.client.iso11179.model;

import com.xclinical.mdr.client.io.Command;

public abstract class SearchCommand extends Command {

	public static final String PATH = "search";
	
	public static final String TERM = "term";

	public static final String TYPE = "type";

	public static final String QUERY = "query";
	
	public static final String START = "start";

	public static final String LENGTH = "length";

	protected SearchCommand() {
	}

	public final native String getTerm() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.SearchCommand::TERM];
	}-*/;

	public final native String getType() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.SearchCommand::TYPE];
	}-*/;
	
	public final native int getStart() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.SearchCommand::START];
	}-*/;

	public final native int getLength() /*-{
		return this[@com.xclinical.mdr.client.iso11179.model.SearchCommand::LENGTH];
	}-*/;

	public static final native SearchCommand fromTerm(String term, String type, String query, int start, int length) /*-{
		return {
			term : term,
			type : type,
			query: query,
			start : start,
			length : length
		};
	}-*/;
}
