package br.com.customfield;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

public class FilteredComboBox<T> extends ComboBox<T>{
	public static interface FilterCallback<T>{
		public boolean isInFilter(T item, String filter);
	}
	
	ObservableList<T> itens = FXCollections.observableArrayList();
	private String promptText;
	boolean filtering = false;
	boolean reseting = false;
	private ObjectProperty<FilterCallback<T>> filterCallback = new SimpleObjectProperty<>();
	
	{

		setFilterCallback(new FilterCallback<T>() {
			@Override
			public boolean isInFilter(T item, String text) {
				return item.toString().startsWith(text);
			}
		});
		
		getEditor().textProperty().addListener((obs, old, newv)->filter(newv));

		promptTextProperty().addListener((obs, old, newv)->{
			if(filtering)return;
			promptText = newv;
		});

		setOnKeyTyped(e->{
			if(isEditable()){
				return;
			}
			if(e.getCharacter().replace("", "").length() == 0)
				return;
			getEditor().textProperty().set(getEditor().textProperty().get()+e.getCharacter());
		});		

		setOnKeyPressed(e->{
			if(isEditable()){
				return;
			}
			if(e.getCode() == KeyCode.BACK_SPACE && getEditor().textProperty().get().length() > 0){
				StringBuilder b = new StringBuilder(getEditor().textProperty().get());
				b.deleteCharAt(getEditor().textProperty().get().length()-1);
				getEditor().textProperty().set(b.toString());
			}
		});

		focusedProperty().addListener((obs, oldv, newv)->{
			reseting = true;
			try{
				resetFilter();
				if(!newv){
					getEditor().textProperty().set(getValue() == null ? "" : getValue().toString());
					setPromptText(promptText);
				}
			}finally{
				reseting = false;
			}
		});

		getItems().addListener((Change<? extends T> c) -> {
			while(c.next()) {
				if(!filtering)
					itens.setAll(getItems());
			}
		});

		valueProperty().addListener((obs, old, newv)->{
			reseting = true;
			try{
				getEditor().textProperty().set(newv == null ? "" : newv.toString());
			}finally{
				reseting = false;
			}
		});
	}

	private void resetFilter(){
		getItems().setAll(itens);
	}

	public void filter(String filter){
		if(reseting)return;
		filtering = true;
		
		show();
		if(!isEditable())
			setPromptText(filter);
		try{
			if(filter == null || filter.isEmpty()){
				getItems().setAll(itens);
				return;
			}
			getItems().clear();
			for(T item : itens){
				if(getFilterCallback().isInFilter(item, filter)){
					getItems().add(item);
				}
			}
		}finally{
			filtering = false;
		}
	}
	
	public ObjectProperty<FilterCallback<T>> filterCallbackProperty() {
		return this.filterCallback;
	}
	

	public FilterCallback<T> getFilterCallback() {
		return this.filterCallbackProperty().get();
	}
	

	public void setFilterCallback(FilterCallback<T> filterCallback) {
		this.filterCallbackProperty().set(filterCallback);
	}
	

}
