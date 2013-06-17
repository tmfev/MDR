package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.Implications;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.ItemList;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.Context;
import com.xclinical.mdr.client.iso11179.model.EditableItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LanguageIdentification;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasure;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasureListCommand;
import com.xclinical.mdr.repository.Key;

public class UnitOfMeasureListEditor extends Composite implements GenericEditor<Item>, Editor<UnitOfMeasureListCommand> {

	interface Binder extends UiBinder<Widget, UnitOfMeasureListEditor> {
	}

	interface Driver extends SimpleBeanEditorDriver<UnitOfMeasureListCommand, UnitOfMeasureListEditor> {		
	}
	
	private static Binder uiBinder = GWT.create(Binder.class);

	@UiField
	Element editorTable;
	
	@UiField
	ItemLink<Context> parent;

	@UiField
	ItemLink<LanguageIdentification> language;
	
	@UiField
	@Ignore
	CellTable<EditableItem> table;

	@UiField
	@Ignore
	ItemList result;
	
	@Path("elementList")
	ListEditor<EditableItem, LeafValueEditor<EditableItem>> editor;
	
	private final Driver driver;
	
	private UnitOfMeasureListCommand command;
	
	private static final ProvidesKey<EditableItem> KEY_PROVIDER = new ProvidesKey<EditableItem>() {
		@Override
		public Object getKey(EditableItem item) {
			return item;
		}
	};

	private final MultiSelectionModel<EditableItem> selectionModel = new MultiSelectionModel<EditableItem>();
	
	public UnitOfMeasureListEditor() {
		initWidget(uiBinder.createAndBindUi(this));

		command = UnitOfMeasureListCommand.newInstance();
		
		editor = HasDataEditor.of(table);
		driver = GWT.create(Driver.class);
		driver.initialize(this);

		table.setRowCount(0);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);		
	}

	@UiFactory
	CellTable<EditableItem> createTable() {
		CellTable<EditableItem> table = new CellTable<EditableItem>(KEY_PROVIDER);

		table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<EditableItem> createCheckboxManager());
		
		Column<EditableItem, Boolean> checkColumn = new Column<EditableItem, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(EditableItem object) {
				return selectionModel.isSelected(object);
			}
		};
		table.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		table.setColumnWidth(checkColumn, 40, Unit.PX);
		
		final TextInputCell designationCell = new TextInputCell();
		Column<EditableItem, String> designationColumn = new Column<EditableItem, String>(designationCell) {
			@Override
			public String getValue(EditableItem item) {
				return item.getDesignationSign();
			}
		};
		designationColumn.setFieldUpdater(new FieldUpdater<EditableItem, String>() {
			@Override
			public void update(int index, EditableItem object, String value) {
				object.setDesignationSign(value);
			}
		});
		table.addColumn(designationColumn, "Designation");
		
		final TextInputCell definitionCell = new TextInputCell();
		Column<EditableItem, String> definitionColumn = new Column<EditableItem, String>(definitionCell) {
			@Override
			public String getValue(EditableItem item) {
				return item.getDefinitionText();
			}
		};
		definitionColumn.setFieldUpdater(new FieldUpdater<EditableItem, String>() {
			@Override
			public void update(int index, EditableItem object, String value) {
				object.setDefinitionText(value);
			}
		});		
		table.addColumn(definitionColumn, "Definition");

		return table;
	}

	@Override
	public void open(Item obj) {		
		LanguageIdentification lang = (LanguageIdentification) Implications.optionalImplication(Key
				.parse(LanguageIdentification.URN));
		if (lang != null) {
			command.setLanguage(Item.ref(lang));
		}

		Context parent = (Context) Implications.optionalImplication(Key.parse(Context.URN));
		if (parent != null) {
			command.setParent(Item.ref(parent));
		}
		
		driver.edit(command);		
	}

	@Override
	public Item getValue() {
		return null;
	}
	
	@Override
	public void save() {
		UnitOfMeasureListCommand command = driver.flush();
		
		CommandExecutor<ResultList> executor = new CommandExecutor<ResultList>();
		executor.setOnResult(new OnSuccessHandler<ResultList>() {
			@Override
			public void onSuccess(ResultList obj) {
				UIObject.setVisible(editorTable, false);
				result.setVisible(true);
				result.setValue(obj.getElements());
			}
		});
		executor.invoke(UnitOfMeasureListCommand.PATH, command);		
	}	

	@UiHandler("add")
	void onAddClicked(ClickEvent event) {
		editor.getList().add((EditableItem) EditableItem.newInstance(UnitOfMeasure.URN));
	}
	
	@UiHandler("delete")
	void onRemoveClicked(ClickEvent event) {
		editor.getList().removeAll(selectionModel.getSelectedSet());		
	}
}
