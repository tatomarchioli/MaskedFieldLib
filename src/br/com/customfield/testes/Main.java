package br.com.customfield.testes;
	
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.customfield.FilteredComboBox;
import br.com.customfield.FormatBuilder;
import br.com.customfield.FormattedField;
import br.com.customfield.FormattedFieldTableCell;
import br.com.customfield.MaskedField;
import br.com.customfield.MaskedFieldTableCell;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	class Model{
		
		public Model(double v1, double v2){
			d1.set(v1);
			d2.set(v2);
		}
		
		DoubleProperty d1 = new SimpleDoubleProperty();
		DoubleProperty d2 = new SimpleDoubleProperty();
		ObjectProperty<LocalDateTime> d3 = new SimpleObjectProperty<>(); 
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			VBox root = new VBox();
			
			FilteredComboBox<CFOP> cb = new FilteredComboBox<>();
			cb.setEditable(true);
			cb.setFilterCallback((item, filter)-> item.getCodigo().startsWith(filter) || item.getNome().toLowerCase().contains(filter.toLowerCase()));
			cb.getItems().addAll(CFOP.getEntrada());
			root.getChildren().add(cb);
			
			FormattedField f = new FormattedField();
			f.setDecimalMode(true);
			f.setDecimalCases(6);
			f.setLimit(20);
			f.decimalValueProperty().addListener((obs, oldv, newv)->{
				System.out.println(newv.toPlainString());	
			});
			f.setDecimalValue(new BigDecimal("0.000001"));
			
			
			MaskedField f2 = new MaskedField();
			f2.setPromptText("DATA");
			f2.setMask("DD/DD/DDDD");
			
			
			TableView<Model> table = new TableView<>();
			table.setEditable(true);
			table.getItems().addAll(new Model(2.0,30.0),new Model(25.10,30.50),new Model(5.0,40.0));
			FormatBuilder format = new FormatBuilder();
			format.setDecimalMode(true);
			format.setDecimalCases(10);
			format.setLimit(21);
			
			
			StringConverter<Number> decimalConverter = new StringConverter<Number>() {
				@Override
				public String toString(Number t) {
					return t == null ? "" : String.valueOf(t);
				}

				@Override
				public Double fromString(String string) {
					try {
						return Double.parseDouble(string.replace(",", "."));
					} catch (Exception exc) {
						return null;
					}
				}
			}; 
			
			EventHandler<KeyEvent> keyEvent = (e)->{
				if(e.getCode() == KeyCode.TAB){

					System.out.println("opi");
					
					TablePosition<?, ?> tc = table.getFocusModel().getFocusedCell();
					
					int index = table.getColumns().indexOf(tc.getTableColumn())+1;				
					if(index == table.getColumns().size())index = 0;

					boolean BOL = false;

					for(int i = index; i < table.getColumns().size(); i++){
						if(table.getColumns().get(i).isEditable()){
							index = i;
							BOL = true;
							break;
						}
					}

					if(!BOL){
						for(int i = 0; i < table.getColumns().size(); i++){
							if(table.getColumns().get(i).isEditable()){
								index = i;
								break;
							}
						}
					}

					table.getSelectionModel().select( tc.getRow(), table.getColumns().get(index));
					table.edit( tc.getRow(), table.getColumns().get(index));
					if(index == 0){
						table.edit( tc.getRow(), table.getColumns().get(index));
					}
					e.consume();
				}
			};
			
			TableColumn<Model, Number> colDesconto = new TableColumn<>("Desconto");
			colDesconto.setStyle("-fx-alignment: CENTER-RIGHT;");
			colDesconto.setCellValueFactory(p->p.getValue().d1);
			colDesconto.setCellFactory(FormattedFieldTableCell.forTableColumn(decimalConverter,format,keyEvent));
			table.getColumns().add(colDesconto);
			
			TableColumn<Model, LocalDateTime> cold2 = new TableColumn<>("Desconto");
			cold2.setStyle("-fx-alignment: CENTER-RIGHT;");
			cold2.setCellValueFactory(p->p.getValue().d3);
			cold2.setCellFactory(MaskedFieldTableCell.forTableColumn("DD/DD/DDDD", new LocalDateTimeStringConverter(), keyEvent));
			table.getColumns().add(cold2);

			
			Button test = new Button("ok");
			test.setOnAction(e->f.setValue("12345"));
			
			root.getChildren().addAll(f,f2);
			root.getChildren().add(test);
			root.getChildren().add(table);
			
			Scene scene = new Scene(root,400,400);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
		
		System.out.println(55555555555555.1111111);
	}
}
