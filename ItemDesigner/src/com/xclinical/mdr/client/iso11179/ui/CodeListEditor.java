package com.xclinical.mdr.client.iso11179.ui;

import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.HasDataEditor;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.xclinical.mdr.client.iso11179.model.CodeListElement;
import com.xclinical.mdr.client.ui.AddButton;
import com.xclinical.mdr.server.io.odm.TranslatedText;

/**
 * An editor for lists of {@link TranslatedText}.
 * 
 * 
 * @author michael@mictale.com
 */
public class CodeListEditor extends Composite implements
		IsEditor<ListEditor<CodeListElement, LeafValueEditor<CodeListElement>>> {

	interface Binder extends UiBinder<Widget, CodeListEditor> {
	}

	@UiField
	@Path("")
	DataGrid<CodeListElement> elements;

	@UiField
	AddButton add;

	final ListEditor<CodeListElement, LeafValueEditor<CodeListElement>> editor;

	private final MultiSelectionModel<CodeListElement> selectionModel = new MultiSelectionModel<CodeListElement>(
			KEY_PROVIDER);

	private static final ProvidesKey<CodeListElement> KEY_PROVIDER = new ProvidesKey<CodeListElement>() {
		@Override
		public Object getKey(CodeListElement item) {
			String id = item.getId();
			return id != null ? id : item;
		}
	};

	public CodeListEditor() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));

		final Column<CodeListElement, String> codeColumn = new Column<CodeListElement, String>(new EditTextCell()) {
			@Override
			public String getValue(CodeListElement object) {
				return object.getCode();
			}
		};
		codeColumn.setFieldUpdater(new FieldUpdater<CodeListElement, String>() {
			@Override
			public void update(int index, CodeListElement object, String value) {
				object.setCode(value);
			}
		});		
		codeColumn.setSortable(true);
		
		elements.addColumn(codeColumn, "Code");

		final Column<CodeListElement, String> textColumn = new Column<CodeListElement, String>(new EditTextCell()) {
			@Override
			public String getValue(CodeListElement object) {
				return object.getText();
			}
		};
		textColumn.setFieldUpdater(new FieldUpdater<CodeListElement, String>() {
			@Override
			public void update(int index, CodeListElement object, String value) {
				object.setText(value);
			}
		});
		textColumn.setSortable(true);

		final ListHandler<CodeListElement> columnSortHandler = new ListHandler<CodeListElement>(null) {
			public void onColumnSort(ColumnSortEvent event) {
				Column<?,?> col = event.getColumn();
				if (col == codeColumn) {
					Collections.sort(editor.getList(), new CodeComparator(event.isSortAscending()));
				}
				else {
					Collections.sort(editor.getList(), new TextComparator(event.isSortAscending()));					
				}
			}
		};

		elements.addColumnSortHandler(columnSortHandler);
		
		// textColumn.setSortable(true);
		elements.addColumn(textColumn, "Text");
		elements.setLoadingIndicator(null);
		elements.setEmptyTableWidget(new Label("No Elements"));

		editor = HasDataEditor.of(elements);
		
	}

	@UiFactory
	public DataGrid<CodeListElement> createGrid() {
		DataGrid<CodeListElement> grid = new DataGrid<CodeListElement>(KEY_PROVIDER);
		grid.setSelectionModel(selectionModel);
		return grid;
	}

	@Override
	public ListEditor<CodeListElement, LeafValueEditor<CodeListElement>> asEditor() {
		return editor;
	}

	@UiHandler("add")
	void onAddClicked(ClickEvent event) {
		editor.getList().add(CodeListElement.newInstance("New Code", "New Text"));
	}

	@UiHandler("remove")
	void onRemoveClicked(ClickEvent event) {
		for (CodeListElement e : selectionModel.getSelectedSet()) {
			if (editor.getList().remove(e))
				;
		}
	}

	private static final class CodeComparator implements Comparator<CodeListElement> {
		private int ascending;
		
		public CodeComparator(boolean ascending) {
			this.ascending = ascending ? 1 : -1;
		}
		
		@Override
		public int compare(CodeListElement o1, CodeListElement o2) {
			if (o1 == o2) {
				return 0;
			}

			if (o1 != null) {
				return (o2 != null) ? ascending * o1.getCode().compareTo(o2.getCode()) : 1;
			}
			return -1;
		}
	}
	
	private static final class TextComparator implements Comparator<CodeListElement> {
		private int ascending;
		
		public TextComparator(boolean ascending) {
			this.ascending = ascending ? 1 : -1;
		}
		
		@Override
		public int compare(CodeListElement o1, CodeListElement o2) {
			if (o1 == o2) {
				return 0;
			}

			if (o1 != null) {
				return (o2 != null) ? ascending * o1.getText().compareTo(o2.getText()) : 1;
			}
			return -1;
		}
	}	
	
}
