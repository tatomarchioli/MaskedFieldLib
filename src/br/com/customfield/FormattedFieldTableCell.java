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

public class FormattedFieldTableCell<S,T> extends TableCell<S,T> {
	
   
	public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn(FormatBuilder formatter) {
		return forTableColumn(new DefaultStringConverter(),formatter);
	}

	public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn(FormatBuilder formatter, EventHandler<KeyEvent> keyEvent) {
		return forTableColumn(new DefaultStringConverter(),formatter, keyEvent);
	}
	
	public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn( final StringConverter<T> converter, FormatBuilder formatter) {
		return forTableColumn(converter,formatter, null);
	}
	
	public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn( final StringConverter<T> converter, FormatBuilder formatter, EventHandler<KeyEvent> keyEvent) {
		return new Callback<TableColumn<S,T>, TableCell<S,T>>() {
			@Override public TableCell<S,T> call(TableColumn<S,T> list) {
				return new FormattedFieldTableCell<S,T>(converter,formatter,keyEvent);
			}
		};
	}
	
	
	private FormattedField textField;
	private FormatBuilder formatter;
	private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this, "converter");
	private EventHandler<KeyEvent> keyEvent;
	
	//Constructors
	public FormattedFieldTableCell() {
		this(null, new FormatBuilder());
	}
	
	public FormattedFieldTableCell(FormatBuilder formatter) { 
		this(null,formatter);
	} 
	
	public FormattedFieldTableCell(StringConverter<T> converter, FormatBuilder formatter) { 
		this(converter,formatter, null);
	} 

	public FormattedFieldTableCell(StringConverter<T> converter, FormatBuilder formatter, EventHandler<KeyEvent> focus) {
		this.getStyleClass().add("text-field-table-cell");
		setConverter(converter);
		setFormatter(formatter);
		setKeyEvent(focus);
		
	}       
	
	

	
	//getters and setters	
	public final ObjectProperty<StringConverter<T>> converterProperty() { 
		return converter; 
	}

	public final void setConverter(StringConverter<T> value) { 
		converterProperty().set(value); 
	}

	public final StringConverter<T> getConverter() { 
		return converterProperty().get(); 
	}  
	
	public FormattedField getTextField(){
		return textField;
	}
	
	public void setKeyEvent(EventHandler<KeyEvent> keyEvent){
		this.keyEvent = keyEvent;
	}
	
	public EventHandler<KeyEvent> getKeyEvent(){
		return keyEvent;
	}
		
	public FormatBuilder getFormatter() {
		return formatter;
	}

	public void setFormatter(FormatBuilder formatter) {
		this.formatter = formatter;
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
				textField = CellUtils.createFormattedField(this, getConverter(),getFormatter(),getKeyEvent());
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
