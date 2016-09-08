package br.com.customfield.testes;


import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;

public class FilteredComboBoxTooltip<T> extends ComboBox<T>{
	StringProperty filter = new SimpleStringProperty("");
	Tooltip tooltip = new Tooltip();
	ObservableList<T> itens = FXCollections.observableArrayList();
	boolean filtering = false;
	{
		
		Point2D p = localToScene(0.0, 0.0);

		setOnKeyTyped(e->{
			if(e.getCharacter().replace("", "").length() == 0)
				return;
			filter.set(filter.get()+e.getCharacter());
		});		

		setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.BACK_SPACE && filter.get().length() > 0){
				StringBuilder b = new StringBuilder(filter.get());
				b.deleteCharAt(filter.get().length()-1);
				filter.set(b.toString());
			}
		});

		focusedProperty().addListener((obs, oldv, newv)->{
			if(!newv)filter.set("");			
		});

		filter.addListener((obs, oldv, newv)->{
			tooltip.setText(newv);
			if(newv.length() == 0){
				tooltip.hide();
			}else{
				tooltip.show(this, p.getX()
						+ getScene().getX() + getScene().getWindow().getX(), p.getY()
						+ getScene().getY() + getScene().getWindow().getY());
			}
			filter(newv);
		});

		getItems().addListener((Change<? extends T> c) -> {
			while(c.next()) {
				if(!filtering)
					itens.setAll(getItems());;
			}
		});
		
		System.out.println(getItems());
	}



	public void filter(String filter){
		filtering = true;
		try{
			
			System.out.println(itens);
			
			if(filter == null || filter.isEmpty()){
				getItems().setAll(itens);
				return;
			}
			getItems().clear();
			for(T item : itens){
				if(item.toString().startsWith(filter)){
					getItems().add(item);
				}
			}
		}finally{
			filtering = false;
		}
	}

}
