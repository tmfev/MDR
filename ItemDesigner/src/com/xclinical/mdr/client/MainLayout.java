package com.xclinical.mdr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.AddFavouriteCommand;
import com.xclinical.mdr.client.iso11179.model.Assertion;
import com.xclinical.mdr.client.iso11179.model.Characteristic;
import com.xclinical.mdr.client.iso11179.model.ClearFavouritesCommand;
import com.xclinical.mdr.client.iso11179.model.ConceptSystem;
import com.xclinical.mdr.client.iso11179.model.ConceptualDomain;
import com.xclinical.mdr.client.iso11179.model.Context;
import com.xclinical.mdr.client.iso11179.model.DataElement;
import com.xclinical.mdr.client.iso11179.model.DataElementConcept;
import com.xclinical.mdr.client.iso11179.model.DataType;
import com.xclinical.mdr.client.iso11179.model.Definition;
import com.xclinical.mdr.client.iso11179.model.Designation;
import com.xclinical.mdr.client.iso11179.model.ExportDocumentCommand;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LanguageIdentification;
import com.xclinical.mdr.client.iso11179.model.LazyList;
import com.xclinical.mdr.client.iso11179.model.LoadRootCommand;
import com.xclinical.mdr.client.iso11179.model.Namespace;
import com.xclinical.mdr.client.iso11179.model.ObjectClass;
import com.xclinical.mdr.client.iso11179.model.PermissibleValue;
import com.xclinical.mdr.client.iso11179.model.ReferenceDocument;
import com.xclinical.mdr.client.iso11179.model.Relation;
import com.xclinical.mdr.client.iso11179.model.RelationRole;
import com.xclinical.mdr.client.iso11179.model.ResultList;
import com.xclinical.mdr.client.iso11179.model.SearchCommand;
import com.xclinical.mdr.client.iso11179.model.Slot;
import com.xclinical.mdr.client.iso11179.model.UnitOfMeasure;
import com.xclinical.mdr.client.iso11179.model.User;
import com.xclinical.mdr.client.iso11179.model.UserGroup;
import com.xclinical.mdr.client.iso11179.model.ValueDomain;
import com.xclinical.mdr.client.iso11179.model.ValueMeaning;
import com.xclinical.mdr.client.iso11179.ui.DocumentUploader;
import com.xclinical.mdr.client.res.MdrResources;
import com.xclinical.mdr.client.ui.MessageBox;
import com.xclinical.mdr.client.ui.Platform;
import com.xclinical.mdr.client.ui.PopupDialog;
import com.xclinical.mdr.client.util.JsList;
import com.xclinical.mdr.client.util.LoginUtils;
import com.xclinical.mdr.client.windows.Clipboard;
import com.xclinical.mdr.client.windows.Window;
import com.xclinical.mdr.client.windows.WindowManager;
import com.xclinical.mdr.client.windows.WindowPresenter;
import com.xclinical.mdr.repository.Key;

public class MainLayout extends Composite {

	interface MyUiBinder extends UiBinder<Widget, MainLayout> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	protected PushButton newIso11179;
	
	@UiField
	protected TextBox searchTerm;

	@UiField
	protected ListBox searchType;

	@UiField
	protected ListBox searchAlgorithm;
	
	@UiField
	protected SearchList searchList;

	@UiField
	protected TextArea query;

	@UiField
	protected QueryList queryList;
		
	@UiField
	protected WindowPresenter canvas;

	@UiField
	protected WindowPresenter dock;
	
	@UiField(provided=true)
	LazyItemList favouritesList;

	private final SearchItemProvider searchProvider = new SearchItemProvider();

	private final QueryItemProvider queryProvider = new QueryItemProvider();

	private final LazyListDataProvider favouritesProvider = new LazyListDataProvider(LazyList.of(User.FAVOURITES, LoginUtils.getCurrentLoginInfo().getUser()));

	MenuItem saveItem;

	private Clipboard clipboard;

	public MainLayout() {
	
		favouritesList = new LazyItemList(true, true) {
			@Override
			protected void onDrop(Item item) {
				CommandExecutor<ResultList> ex = new CommandExecutor<ResultList>();
				ex.setOnResult(new OnSuccessHandler<ResultList>() {
					@Override
					public void onSuccess(ResultList obj) {
						JsList<Item> itemList = (JsList<Item>)obj.getElementList();
						favouritesProvider.updateRowData(0, itemList);
					}
				});
				
				ex.invoke(AddFavouriteCommand.PATH, AddFavouriteCommand.newInstance(item));			
			}
		};

		HistoryItemProvider.get().load();
		
		initWidget(uiBinder.createAndBindUi(this));
		addStyleName(MdrResources.INSTANCE.css().nosel());
	
		favouritesList.addProvider(favouritesProvider);
		
		
		searchList.setProvider(searchProvider);
		queryList.setProvider(queryProvider);
		
		searchType.addItem("(All)", "");

		searchType.addItem("Data Item", DataElement.URN.toString());
		searchType.addItem("Data Group", Context.URN.toString());

		searchType.addItem("--------------", "");
		
		for (String urn : Item.ALL_URNS) {
			Key key = Key.parse(urn);
			searchType.addItem(key.getSimpleName(), key.toString());
		}

		searchType.addItem("--------------", "");
		searchType.addItem("User", User.URN.toString());
		searchType.addItem("User Group", UserGroup.URN.toString());
		
		searchAlgorithm.addItem("Rating", Item.SEARCH_BY_RATING);
		searchAlgorithm.addItem("References", Item.SEARCH_BY_REFERENCE);
		searchAlgorithm.addItem("Clicks", Item.SEARCH_BY_CLICKS);
		searchAlgorithm.addItem("Updates", Item.SEARCH_BY_UPDATES);
		
		WindowManager wm = WindowManager.get();
		wm.setDock(dock);
		wm.setCanvas(canvas);
	
		clipboard = wm.createClipboard();		
	}
	
	private void newClicked(String typeUrn, Mode mode) {
		Key k = Key.parse(typeUrn);
		Item item = Item.newInstance(typeUrn);
		item.setDisplayName(k.getSimpleName());
		ItemDesigner.get().open(item, mode, true);		
	}
	
	@UiHandler("newDataItem")
	protected void onNewDataItemClicked(ClickEvent event) {
		newClicked(DataElement.URN, Mode.SIMPLE);
	}

	@UiHandler("newDataGroup")
	protected void onNewDataItemGroupClicked(ClickEvent event) {
		newClicked(Context.URN, Mode.SIMPLE);
	}

	@UiHandler("newValueDomain")
	protected void onNewValueDomainClicked(ClickEvent event) {
		newClicked(ValueDomain.URN, Mode.SIMPLE);
	}

	@UiHandler("newUnitOfMeasureList")
	protected void onNewUnitOfMeasureListDomainClicked(ClickEvent event) {
		newClicked(UnitOfMeasure.URN, Mode.BULK);
	}

	@UiHandler("newDataTypeList")
	protected void onNewDataTypeListDomainClicked(ClickEvent event) {
		newClicked(DataType.URN, Mode.BULK);
	}

	@UiHandler("newUserGroup")
	protected void onNewUserGroupClicked(ClickEvent event) {
		newClicked(UserGroup.URN, Mode.SIMPLE);
	}
	
	@UiHandler("newImport")
	protected void onNewImportClicked(ClickEvent event) {
		ItemDesigner.get().open(new DocumentUploader(), "Upload", true);
	}

	@UiHandler("newExport")
	protected void onNewExportClicked(ClickEvent event) {
		WindowManager wm = WindowManager.get();
		Window active = wm.getActiveWindow();
		IsWidget body = active.getBody();
		if (body instanceof HasItem) {
			Item item = ((HasItem)body).getItem();
			ExportDocumentCommand command = ExportDocumentCommand.newInstance(item);
			CommandExecutor<Item> executor = new CommandExecutor<Item>();
			executor.setOnResult(new OnSuccessHandler<Item>() {					
				@Override
				public void onSuccess(Item obj) {
					ItemDesigner.get().open(obj, Mode.DEFAULT, false);
				}
			});
			executor.invoke(ExportDocumentCommand.PATH, command);								
		}
		else {
			Platform.getWorkbench().showPopup(new MessageBox("This element does not support export"));
		}		
	}
	
	private void add(MenuBar menu, final String typeUrn) {
		Key k = Key.parse(typeUrn);
		add(menu, k.getSimpleName(), typeUrn, Mode.ISO11179);
	}

	private void add(final MenuBar menu, final String title, final String typeName, final Mode mode) {
		menu.addItem(title, new Command() {
			@Override
			public void execute() {
				((PopupPanel) menu.getParent()).hide();
				Item item = Item.newInstance(typeName);
				item.setDisplayName(title);
				ItemDesigner.get().open(item, mode, true);
			}
		});
	}
	
	@UiHandler("newIso11179")
	protected void onNewIso11179Clicked(ClickEvent event) {
		PopupPanel panel = new PopupPanel(true);

		MenuBar iso = new MenuBar(true);
		
		add(iso, Assertion.URN);
		add(iso, Characteristic.URN);
		add(iso, ConceptSystem.URN);
		add(iso, ConceptualDomain.URN);
		add(iso, Context.URN);
		add(iso, DataElement.URN);
		add(iso, DataType.URN);
		add(iso, DataElementConcept.URN);
		add(iso, Designation.URN);
		add(iso, Definition.URN);
		add(iso, LanguageIdentification.URN);
		add(iso, Namespace.URN);
		add(iso, ObjectClass.URN);
		add(iso, PermissibleValue.URN);
		add(iso, ReferenceDocument.URN);
		add(iso, Relation.URN);
		add(iso, RelationRole.URN);
		add(iso, Slot.URN);
		add(iso, UnitOfMeasure.URN);
		add(iso, ValueDomain.URN);
		add(iso, ValueMeaning.URN);
		
		panel.add(iso);
		panel.showRelativeTo(newIso11179);
	}
	
	@UiHandler("viewSimple")
	protected void onViewSimpleClicked(ClickEvent event) {
		ItemDesigner.get().getEventBus().fireEvent(new ViewAsEvent(Mode.SIMPLE));				
	}

	@UiHandler("viewIso11179")
	protected void onViewIso11179Clicked(ClickEvent event) {
		ItemDesigner.get().getEventBus().fireEvent(new ViewAsEvent(Mode.ISO11179));				
	}
	
	@UiHandler("viewBrowser")
	protected void onViewBrowserClicked(ClickEvent event) {
		ItemDesigner.get().getEventBus().fireEvent(new ViewAsEvent(Mode.BROWSER));				
	}
	
	@UiHandler("viewPreview")
	protected void onViewPreviewClicked(ClickEvent event) {
		ItemDesigner.get().getEventBus().fireEvent(new ViewAsEvent(ViewAsEvent.Mode.PREVIEW));				
	}
	
	@UiHandler("viewSource")
	protected void onViewSourceClicked(ClickEvent event) {
		ItemDesigner.get().getEventBus().fireEvent(new ViewAsEvent(ViewAsEvent.Mode.SOURCE));				
	}
	
	@UiHandler({"save", "save2"})
	protected void onSaveClicked(ClickEvent event) {
		ItemDesigner.get().getEventBus().fireEvent(new SaveEvent());
	}
	
	@UiHandler("copy")
	protected void onCopyClicked(ClickEvent event) {
		clipboard.copy();
	}

	@UiHandler("paste")
	protected void onPasteClicked(ClickEvent event) {
		clipboard.paste();
	}

	@UiHandler("remove")
	protected void onRemoveClicked(ClickEvent event) {
		clipboard.remove();
	}
	
	@UiHandler("home")
	protected void onHomeClicked(ClickEvent event) {
		LoadRootCommand command = LoadRootCommand.newInstance();
		CommandExecutor<Item> executor = new CommandExecutor<Item>();
		executor.setOnResult(new OnSuccessHandler<Item>() {					
			@Override
			public void onSuccess(Item obj) {
				ItemDesigner.get().open(obj, Mode.DEFAULT, true);
			}
		});
		executor.invoke(LoadRootCommand.PATH, command);				
	}

	@UiHandler("version")
	protected void onVersionClicked(ClickEvent event) {
		InfoPanel info = new InfoPanel();
		Platform.getWorkbench().showPopup(new PopupDialog<InfoPanel>(info, true));
	}
	
	@UiHandler("clearHistory")
	protected void onClearHistoryClicked(ClickEvent event) {
		HistoryItemProvider.get().clear();
	}

	@UiHandler("clearFavorites")
	protected void onClearFavoritesClicked(ClickEvent event) {
		CommandExecutor<JavaScriptObject> ex = new CommandExecutor<JavaScriptObject>();
		ex.setOnResult(new OnSuccessHandler<JavaScriptObject>() {
			@Override
			public void onSuccess(JavaScriptObject obj) {
				favouritesProvider.updateRowCount(0, true);
			}
		});
		
		ex.invoke(ClearFavouritesCommand.PATH, ClearFavouritesCommand.newInstance());			
	}

	private void requery() {
		String term = searchTerm.getText();
		String type = searchType.getValue(searchType.getSelectedIndex());		
		String algorithm = searchAlgorithm.getValue(searchAlgorithm.getSelectedIndex());
		searchProvider.setTerm(term, type.length() == 0 ? null : Key.parse(type), algorithm);		
	}
	
	@UiHandler("searchTerm")
	protected void onSearchTermKeyUp(KeyUpEvent event) {
		requery();
	}

	@UiHandler({"searchType", "searchAlgorithm"})
	protected void onSearchTypeChanged(ChangeEvent event) {
		requery();
	}

	@UiHandler("query")
	protected void onQueryKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && !event.isShiftKeyDown()) {
			event.preventDefault();
			String q = query.getText();
			queryProvider.setQuery(q);		
		}
	}	
}
