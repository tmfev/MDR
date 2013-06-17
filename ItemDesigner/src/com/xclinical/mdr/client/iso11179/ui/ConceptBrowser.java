package com.xclinical.mdr.client.iso11179.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.xclinical.mdr.client.Editors;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.HistoryItemProvider;
import com.xclinical.mdr.client.ItemCell;
import com.xclinical.mdr.client.ItemDesigner;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.Concept;
import com.xclinical.mdr.client.iso11179.model.FindFirstCommand;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.LoadListCommand;
import com.xclinical.mdr.client.iso11179.model.Relation;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.windows.HasWindow;
import com.xclinical.mdr.client.windows.Window;

/**
 * Browses {@link Concept}s by {@link Relation} hierarchies.
 * 
 * @author ms@xclinical.com
 */
public class ConceptBrowser extends Composite implements GenericEditor<Item>, HasWindow, HasItem {

	interface Binder extends UiBinder<Widget, ConceptBrowser> {
		Binder B = GWT.create(Binder.class);
	}

	interface Style extends CssResource {
		String grow();
	}

	@UiField
	Style style;

	@UiField
	SimplePanel panel;

	@UiField
	SimplePanel editor;
	
	CellBrowser browser;

	private Item initialValue;
	
	private Window window;

	private TreeViewModel model;

	private Item lastItem;
	
	private static final ProvidesKey<Item> KEY_PROVIDER = new ProvidesKey<Item>() {		
		@Override
		public Object getKey(Item item) {
			if (item.getId() == null) {
				return item;
			}
			else {
				return item.getId();
			}
		}
	};

	private final SingleSelectionModel<Item> selectionModel;
	
	private final ItemCell cell = new ItemCell(false) {
		protected void onItemClicked(Item entity) {
			ConceptBrowser.this.onItemClicked(entity);
		};

		protected void onDeleteClicked(Item entity) {
		};
	};
	
	public ConceptBrowser() {
		selectionModel = new SingleSelectionModel<Item>(KEY_PROVIDER);
		
		initWidget(Binder.B.createAndBindUi(this));
	}

	private void onItemClicked(final Item entity) {
		FindFirstCommand command = FindFirstCommand.newInstance(entity.getId());
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {					
			@Override
			public void onSuccess(Item obj) {
				HistoryItemProvider.get().add(obj);
				
				lastItem = obj;
				
				GenericEditor<? super Item> itemEditor = Editors.createEditor(obj, ItemDesigner.get().getDefaultMode());
				editor.setWidget(itemEditor);			
				itemEditor.open(obj);
				
				window.setTitle(initialValue.getDisplayName() + ": " + obj.getDisplayName());
			}
		});
		executor.invoke(FindFirstCommand.PATH, command);											
	}

	@Override
	public void setWindow(Window wnd) {
		this.window = wnd;		
	}
	
	@Override
	public void open(Item obj) {
		initialValue = obj;
		
		browser = new CellBrowser(model = new Model(), initialValue);
		browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		browser.setSize("100%", "100%");
		panel.setWidget(browser);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void save() {
		GenericEditor<Item> itemEditor = (GenericEditor<Item>)editor.getWidget();
		if (itemEditor != null) {
			itemEditor.save();
		}
	}

	@Override
	public Item getValue() {
		return getItem();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Item getItem() {
		GenericEditor<Item> itemEditor = (GenericEditor<Item>)editor.getWidget();
		if (itemEditor == null) {
			return initialValue;
		}
		else {
			return itemEditor.getValue();
		}
	}
	
	private class Model implements TreeViewModel {
		@Override
		public <N> NodeInfo<?> getNodeInfo(N value) {
			Item item = (Item) value;
			
			AbstractDataProvider<Item> p;
						
			if (ConceptRelation.isConceptRelation(item)) {
				ConceptRelation cr = item.cast();
				p = new LazyListDataProvider(LazyList.of(Concept.RELATED, Item.ref(cr.getConcept()), Item.ref(cr)));								
			}
			else {
				p = new ConceptRelationDataProvider((Concept)item);								
			}
			
			return new DefaultNodeInfo<Item>(p, cell, selectionModel, null);
		}

		@Override
		public boolean isLeaf(Object value) {
			return false;
		}
	}

	private static final class ConceptRelation extends Relation {
		protected ConceptRelation() {
		}

		public final native void setConceptRelation() /*-{
			this.__conceptRelation = true;
		}-*/;
		
		public static final native boolean isConceptRelation(JavaScriptObject obj) /*-{
			return obj.__conceptRelation || false;
		}-*/;
		
		public final native void setConcept(Concept concept) /*-{
			this.concept = concept;
		}-*/;

		public final native Concept getConcept() /*-{
			return this.concept;
		}-*/;
		
		public static ConceptRelation newInstance(Concept concept, Relation relation) {
			ConceptRelation r = relation.cast();
			r.setConcept(concept);
			r.setConceptRelation();
			return r;
		}
	}
	
	private static class ConceptRelationDataProvider extends LazyListDataProvider {

		private final Concept concept;
		
		public ConceptRelationDataProvider(Concept concept) {
			super(LazyList.of(Concept.RELATION_ROLES, Item.ref(concept)));
			this.concept = concept;
		}

		@Override
		protected List<? extends Item> buildList(ResultList loaded) {
			List<ConceptRelation> result = new ArrayList<ConceptRelation>();
			
			for (Item i : loaded.getElementList()) {
				result.add(ConceptRelation.newInstance(concept, (Relation)i));
			}
			return result;
		}
	}
	
	private static class LazyListDataProvider extends AsyncDataProvider<Item> {

		private final LazyList list;
		
		public LazyListDataProvider(LazyList list) {
			this.list = list;		
		}
		
		protected List<? extends Item> buildList(ResultList loaded) {
			return loaded.getElementList();
		}
		
		@Override
		protected void onRangeChanged(final HasData<Item> display) {
			if (list == null) {
				display.setRowCount(0);
			}
			else {
				final Range range = display.getVisibleRange();
				final int start = range.getStart();
				final int length = range.getLength();
				
				CommandExecutor<ResultList> ex = new CommandExecutor<ResultList>();
				ex.setOnResult(new OnSuccessHandler<ResultList>() {
					@Override
					public void onSuccess(ResultList obj) {
						List<? extends Item> itemList = buildList(obj);
						onDataChanged(display, start, obj.getLength(), itemList);
					}
				});
				
				ex.invoke(LoadListCommand.PATH, LoadListCommand.newInstance(list, start, length));
			}
		}
		
		protected void onDataChanged(HasData<Item> display, int start, int size, List<? extends Item> items) {
			display.setRowData(start, items);
			
			if (start == 0) {
				display.setRowCount(size, true);
			}		
		}
	}	
}
