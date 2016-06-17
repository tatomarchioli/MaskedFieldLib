package br.com.customfield;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FormatBuilder{

	public static final int NO_LIMIT = -1;
	public static final int NORMAL_CASE = 0;
	public static final int LOWER_CASE = 1;
	public static final int UPPER_CASE = 2;

	private BooleanProperty decimalMode = new SimpleBooleanProperty();
	private IntegerProperty numDecimalCaseProperty = new SimpleIntegerProperty();

	private StringProperty prefixProperty = new SimpleStringProperty();
	private StringProperty sufixProperty = new SimpleStringProperty();
	private IntegerProperty limitProperty = new SimpleIntegerProperty();

	private BooleanProperty allowLetters = new SimpleBooleanProperty();
	private BooleanProperty allowNumbers = new SimpleBooleanProperty();
	private BooleanProperty allowSpecialChars = new SimpleBooleanProperty();
	private BooleanProperty allowWhiteSpace = new SimpleBooleanProperty();
	private IntegerProperty caseProperty = new SimpleIntegerProperty();


	public FormatBuilder(){
		decimalMode.set(false);
		prefixProperty.set("");
		sufixProperty.set("");
		limitProperty.set(-1);
		caseProperty.set(0);
		allowLetters.set(true);
		allowNumbers.set(true);
		allowSpecialChars.set(true);
		allowWhiteSpace.set(true);
	}
	
	//Properties

	public IntegerProperty numDecimalCaseProperty(){
		return numDecimalCaseProperty;
	}

	public IntegerProperty caseProperty(){
		return caseProperty;
	}

	public StringProperty prefixProperty() {
		return prefixProperty;
	}

	public StringProperty sufixProperty() {
		return sufixProperty;
	}

	public IntegerProperty limitProperty() {
		return limitProperty;
	}

	public BooleanProperty allowLettersProperty(){
		return allowLetters;
	}

	public BooleanProperty allowNumbersProperty(){
		return allowNumbers;
	}

	public BooleanProperty allowSpecialCharsProperty(){
		return allowSpecialChars;
	}

	public BooleanProperty allowWhiteSpaceProperty(){
		return allowWhiteSpace;
	}

	public BooleanProperty decimalModeProperty() {
		return decimalMode;
	}



	//Getters and setters

	public void setCase(int value){
		if(value <0 || value > 2){
			throw new IllegalArgumentException("Case must be NORMAL, UPPER or LOWER");
		}
		this.caseProperty.set(value);
	}

	public int getCase(){
		return this.caseProperty.get();
	}

	public boolean isAllowLetters() {
		return allowLetters.get();
	}

	public void setAllowLetters(boolean allowLetters) {
		this.allowLetters.set(allowLetters);
	}

	public boolean isAllowNumbers() {
		return allowNumbers.get();
	}

	public void setAllowNumbers(boolean allowNumbers) {
		this.allowNumbers.set(allowNumbers);
	}

	public boolean isAllowSpecialChars() {
		return allowSpecialChars.get();
	}

	public void setAllowSpecialChars(boolean allowSpecialChars) {
		this.allowSpecialChars.set(allowSpecialChars);
	}

	public boolean isAllowWhiteSpace() {
		return allowWhiteSpace.get();
	}

	public void setAllowWhiteSpace(boolean allowWhiteSpace) {
		this.allowWhiteSpace.set(allowWhiteSpace);
	}

	public int getLimit(){
		return this.limitProperty.get();
	}

	public void setLimit(int limit){
		this.limitProperty.set(limit);
	}

	public void setPrefix(String prefix){
		this.prefixProperty.set(prefix);
	}

	public String getPrefix(){
		return this.prefixProperty.get();
	}

	public void setSufix(String prefix){
		this.sufixProperty.set(prefix);
	}

	public String getSufix(){
		return this.sufixProperty.get();
	}

	public void setDecimalCases(int num){
		numDecimalCaseProperty.set(num);
	}

	public int getDecimalCases(){
		return numDecimalCaseProperty.get();
	}

	public void setDecimalMode(boolean bol){
		decimalMode.set(bol);
	}

	public boolean isDecimalMode(){
		return decimalMode.get();
	}

}