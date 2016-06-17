package br.com.customfield;

/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;


public class MaskedFieldTableCell<S,T> extends TableCell<S,T> {

	public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn(String mask) {
		return forTableColumn(mask,new DefaultStringConverter());
	}

	public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(String mask,  final StringConverter<T> converter) {
		return new Callback<TableColumn<S,T>, TableCell<S,T>>() {
			@Override public TableCell<S,T> call(TableColumn<S,T> list) {
				return new MaskedFieldTableCell<S,T>(converter,mask);
			}
		};
	}
	
	public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(String mask,  final StringConverter<T> converter, EventHandler<KeyEvent> keyEvent) {
		return new Callback<TableColumn<S,T>, TableCell<S,T>>() {
			@Override public TableCell<S,T> call(TableColumn<S,T> list) {
				return new MaskedFieldTableCell<S,T>(converter,mask,keyEvent);
			}
		};
	}


	private MaskedField textField;
	private EventHandler<KeyEvent> keyEvent;
	private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this, "converter");
	private String mask = "";

	//Constructors
	public MaskedFieldTableCell(String mask) { 
		this(null,mask);
	} 

	public MaskedFieldTableCell(StringConverter<T> converter, String mask) {
		this(converter,mask, null);
	}
	
	public MaskedFieldTableCell(StringConverter<T> converter, String mask, EventHandler<KeyEvent> keyEvent) {
		this.getStyleClass().add("text-field-table-cell");
		setConverter(converter);
		setMask(mask);
		setKeyEvent(keyEvent);
	}



	//Getters and setters
	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public final ObjectProperty<StringConverter<T>> converterProperty() { 
		return converter; 
	}

	public final void setConverter(StringConverter<T> value) { 
		converterProperty().set(value); 
	}

	public final StringConverter<T> getConverter() { 
		return converterProperty().get(); 
	}  

	public EventHandler<KeyEvent> getKeyEvent() {
		return keyEvent;
	}

	public void setKeyEvent(EventHandler<KeyEvent> keyEvent) {
		this.keyEvent = keyEvent;
	}
	
	

	@Override public void startEdit() {
		if (! isEditable() 
				|| ! getTableView().isEditable() 
				|| ! getTableColumn().isEditable()) {
			return;
		}
		super.startEdit();

		if (isEditing()) {
			if (textField == null) {
				textField = CellUtils.createMaskedField(this, getConverter(),getMask(),getKeyEvent());
			}

			CellUtils.startEdit(this, getConverter(), null, null, textField);
		}
	}

	@Override public void cancelEdit() {
		super.cancelEdit();
		CellUtils.cancelEdit(this, getConverter(), null);
	}

	@Override public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		CellUtils.updateItem(this, getConverter(), null, null, textField);
	}


}
