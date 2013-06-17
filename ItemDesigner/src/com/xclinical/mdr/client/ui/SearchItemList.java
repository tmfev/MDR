/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package com.xclinical.mdr.client.ui;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchItemList extends Composite {

	private final FlexTable resultTable;

	public SearchItemList() {		
		VerticalPanel panel = new VerticalPanel();

		final TextBox term = new TextBox();
		term.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String t = term.getText();
				if ("".equals(t)) {
					showEmpty();
				}
				else {
					searchItems(t);
				}
			}
		});

		term.setWidth("100%");
		panel.add(term);
		
		resultTable = new FlexTable();
		panel.add(resultTable);
		initWidget(panel);

		showEmpty();		
	}

	private void searchItems(String term) {
		/*
		Filter filter = FilterFactory.create(ISO11179.TERM_SEARCH, new StringItemField(term));
		itemService.query(filter, new Range(0, 100), new AbstractAsyncCallback<QueryResult>() {
			@Override
			public void onSuccess(QueryResult result) {
				fillList(result);
			}
		});
		*/
	}

	private void showEmpty() {
		resultTable.removeAllRows();
		resultTable.setText(0, 0, "Enter search term");		
	}
	
	/*
	private void fillList(QueryResult result) {
		resultTable.removeAllRows();

		if (result.getCount() == 0) {
			resultTable.setText(0, 0, "No results found");
		} else {
			for (Item item : result.getItems()) {
				final Item i = item;
				ItemLink link = new ItemLink();
				link.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						ItemSource source = new ItemSource(i);
						Platform.getWorkbench().openView(source);
					}
				});

				link.setTarget(item);
				resultTable.setWidget(resultTable.getRowCount(), 0, link);
			}
		}
	}
	*/
}
