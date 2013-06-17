package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.HasDataEditor;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.xclinical.mdr.client.iso11179.model.DataType;

public class DataTypeListEditor extends Composite implements  
	IsEditor<ListEditor<DataType, LeafValueEditor<DataType>>> {

	interface Binder extends UiBinder<Widget, DataTypeListEditor> {
	}

	@UiField
	CellTable<DataType> table;

	private ListEditor<DataType, LeafValueEditor<DataType>> editor;
	
	private static final ProvidesKey<DataType> KEY_PROVIDER = new ProvidesKey<DataType>() {
		@Override
		public Object getKey(DataType item) {
			return item;
		}
	};

	private final MultiSelectionModel<DataType> selectionModel = new MultiSelectionModel<DataType>();
	
	public DataTypeListEditor() {
		initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));

		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table.setRowCount(0);
		
		editor = HasDataEditor.of(table);
	}

	@Override
	public ListEditor<DataType, LeafValueEditor<DataType>> asEditor() {
		return editor;
	}
	
	@UiFactory
	CellTable<DataType> createTable() {
		CellTable<DataType> table = new CellTable<DataType>(KEY_PROVIDER);

		table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<DataType> createCheckboxManager());
		
		Column<DataType, Boolean> checkColumn = new Column<DataType, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(DataType object) {
				return selectionModel.isSelected(object);
			}
		};
		table.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		table.setColumnWidth(checkColumn, 40, Unit.PX);
		
		final TextInputCell nameCell = new TextInputCell();
		Column<DataType, String> nameColumn = new Column<DataType, String>(nameCell) {
			@Override
			public String getValue(DataType item) {
				return item.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<DataType, String>() {
			@Override
			public void update(int index, DataType object, String value) {
				object.setName(value);
			}
		});
		table.addColumn(nameColumn, "Name");
		
		final TextInputCell descriptionCell = new TextInputCell();
		Column<DataType, String> descriptionColumn = new Column<DataType, String>(descriptionCell) {
			@Override
			public String getValue(DataType item) {
				return item.getDescription();
			}
		};
		descriptionColumn.setFieldUpdater(new FieldUpdater<DataType, String>() {
			@Override
			public void update(int index, DataType object, String value) {
				object.setDescription(value);
			}
		});
		table.addColumn(descriptionColumn, "Description");

		final TextInputCell schemeReferenceCell = new TextInputCell();
		Column<DataType, String> schemeReferenceColumn = new Column<DataType, String>(schemeReferenceCell) {
			@Override
			public String getValue(DataType item) {
				return item.getSchemeReference();
			}
		};
		schemeReferenceColumn.setFieldUpdater(new FieldUpdater<DataType, String>() {
			@Override
			public void update(int index, DataType object, String value) {
				object.setSchemeReference(value);
			}
		});
		table.addColumn(descriptionColumn, "Scheme Reference");
		
		return table;
	}
	
	@UiHandler("add")
	void onAddClicked(ClickEvent event) {
		editor.getList().add(DataType.newInstance());
	}
	
	@UiHandler("delete")
	void onRemoveClicked(ClickEvent event) {
		editor.getList().removeAll(selectionModel.getSelectedSet());		
	}
}
