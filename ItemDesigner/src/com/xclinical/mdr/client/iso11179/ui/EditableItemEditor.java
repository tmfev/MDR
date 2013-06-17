package com.xclinical.mdr.client.iso11179.ui;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.xclinical.mdr.client.GenericEditor;
import com.xclinical.mdr.client.Implications;
import com.xclinical.mdr.client.ItemDesigner;
import com.xclinical.mdr.client.ItemLink;
import com.xclinical.mdr.client.RatingPanel;
import com.xclinical.mdr.client.ViewAsEvent.Mode;
import com.xclinical.mdr.client.io.CommandExecutor;
import com.xclinical.mdr.client.io.OnSuccessHandler;
import com.xclinical.mdr.client.iso11179.model.AddChildCommand;
import com.xclinical.mdr.client.iso11179.model.Context;
import com.xclinical.mdr.client.iso11179.model.EditableItem;
import com.xclinical.mdr.client.iso11179.model.HasItem;
import com.xclinical.mdr.client.iso11179.model.Item;
import com.xclinical.mdr.client.iso11179.model.LanguageIdentification;
import com.xclinical.mdr.client.iso11179.model.LoadEditableItemCommand;
import com.xclinical.mdr.client.iso11179.model.SaveEditableItemCommand;
import com.xclinical.mdr.client.windows.Clipboard;
import com.xclinical.mdr.client.windows.HasClipboardData;
import com.xclinical.mdr.repository.Key;

public abstract class EditableItemEditor<E extends EditableItem, I extends Item>
		extends Composite implements GenericEditor<I>, Editor<E>, HasItem, HasClipboardData {

	@UiField
	RatingPanel rating;

	@UiField
	ItemLink<Context> parent;

	@UiField
	ItemLink<LanguageIdentification> language;

	@UiField
	TextBox designationSign;

	@UiField
	TextArea definitionText;

	protected SaveEditableItemCommand<E> command;

	protected I item;

	private final String path;
	
	public EditableItemEditor(SaveEditableItemCommand<E> value, String path) {
		this.command = value;
		this.path = path;
	}

	public final void open(I obj) {
		item = obj;

		if (item.isNew()) {
			E editable = command.getItem();
			editable.setRating(item.getRating());
			
			LanguageIdentification lang = (LanguageIdentification) Implications.optionalImplication(Key
					.parse(LanguageIdentification.URN));
			if (lang != null) {
				editable.setLanguage(Item.ref(lang));
			}

			Context parent = (Context) Implications.optionalImplication(Key.parse(Context.URN));
			if (parent != null) {
				editable.setParent(Item.ref(parent));
			}
			
			onEdit(editable);
		} else {
			CommandExecutor<E> ex = new CommandExecutor<E>(); 
			ex.setOnResult(new OnSuccessHandler<E>() {
				public void onSuccess(E obj) {
					command.setItem(obj);
					onEdit(obj);
				};
			});
			ex.invoke(LoadEditableItemCommand.PATH, LoadEditableItemCommand.newInstance(obj));						
		}
	}

	protected abstract void onEdit(E obj);
	
	@Override
	public I getValue() {
		return item;
	}

	@Override
	public Item getItem() {
		return getValue();
	}
	
	@Override
	public void save() {
		CommandExecutor<I> executor = new CommandExecutor<I>();
		executor.setOnResult(new OnSuccessHandler<I>() {
			public void onSuccess(I obj) {
				ItemDesigner.get().open(obj, Mode.DEFAULT, false);
			};
		});
		
		command.setItem(flush());
		executor.invoke(path, command);
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
		// TODO: Implement this
	}
	
	protected abstract E flush();
}
