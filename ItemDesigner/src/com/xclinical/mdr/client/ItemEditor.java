package com.xclinical.mdr.client;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.Composite;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.AddChildCommand;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.SaveItemCommand;
import com.xclinical.mdr.client.windows.Clipboard;
import com.xclinical.mdr.client.windows.HasClipboardData;

/**
 * An editor for {@link Item}s.
 * 
 * Create new instances of this class using {@link Editors#createEditor(Item, EditorHost)}.
 * 
 * @author ms@xclinical.com
 */
public abstract class ItemEditor<T extends Item> extends Composite implements Editor<T>, GenericEditor<T>, HasItem, HasClipboardData {

	private T item;
	
	protected SimpleBeanEditorDriver<T, ?> driver;
	
	public ItemEditor() {
	}

	@Override
	public String getTitle() {
		return item.getDisplayName();
	}
	
	@Override
	public T getValue() {
		return item;
	}

	@Override
	public Item getItem() {
		return item;
	}
	
	@SuppressWarnings("unchecked")
	protected void setDriver(SimpleBeanEditorDriver<T, ?> driver) {
		if (driver == null) throw new NullPointerException();
		
		this.driver = driver;
		
		@SuppressWarnings("rawtypes")
		SimpleBeanEditorDriver d = driver;
		d.initialize(this);		
	}
	
	public void open(T entity) {
		this.item = entity;
		onOpen(entity);
	}
	
	public void save() {
		T item = driver.flush();
		CommandExecutor<T> ex = new CommandExecutor<T>();
		ex.setOnResult(new OnSuccessHandler<T>() {
			public void onSuccess(T obj) {
				open(obj);
			};
		});
		SaveItemCommand<T> cmd = SaveItemCommand.newInstance(item);
		ex.invoke(SaveItemCommand.PATH, cmd);
	}

	protected void onOpen(T entity) {		
		if (driver != null) {
			driver.edit(entity);
		}
	}
	
	@Override
	public void onCopy(Clipboard clipboard) {
		clipboard.setData(getValue());
	}

	@Override
	public void onPaste(Clipboard clipboard) {
		CommandExecutor<Item> ex = new CommandExecutor<Item>();
		ex.setOnResult(new OnSuccessHandler<Item>() {
			@Override
			public void onSuccess(Item obj) {
			}
		});
		ex.invoke(AddChildCommand.PATH, AddChildCommand.newInstance(getValue(), (Item)clipboard.getData()));
	}
	
	@Override
	public void onRemove(Clipboard clipboard) {
		// TODO: Implement this.
	}
	
}
