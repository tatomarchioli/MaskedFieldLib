package br.com.customfield.testes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FormatBuilder{

	public static final int NO_LIMIT = -1;
	
	public static enum Case{
		NORMAL,LOWER_CASE,UPPER_CASE;
	}

	private BooleanProperty decimalMode = new SimpleBooleanProperty(false);
	private IntegerProperty decimalCases = new SimpleIntegerProperty();
	private BooleanProperty decimalAutoFill = new SimpleBooleanProperty(true);
	private StringProperty prefix = new SimpleStringProperty("");
	private StringProperty sufix = new SimpleStringProperty("");
	private IntegerProperty limit = new SimpleIntegerProperty(-1);
	private BooleanProperty allowLetters = new SimpleBooleanProperty(true);
	private BooleanProperty allowNumbers = new SimpleBooleanProperty(true);
	private BooleanProperty allowSpecialChars = new SimpleBooleanProperty(true);
	private BooleanProperty allowWhiteSpace = new SimpleBooleanProperty(true);
	private StringProperty regex = new SimpleStringProperty();
	private ObjectProperty<Case> caseType = new SimpleObjectProperty<>(Case.NORMAL);
	
	public final BooleanProperty decimalModeProperty() {
		return this.decimalMode;
	}
	
	public final boolean isDecimalMode() {
		return this.decimalModeProperty().get();
	}
	
	public final void setDecimalMode(final boolean decimalMode) {
		this.decimalModeProperty().set(decimalMode);
	}
	
	public final IntegerProperty decimalCasesProperty() {
		return this.decimalCases;
	}
	
	public final int getDecimalCases() {
		return this.decimalCasesProperty().get();
	}
	
	public final void setDecimalCases(final int fractionalNumbers) {
		this.decimalCasesProperty().set(fractionalNumbers);
	}
	
	public final StringProperty prefixProperty() {
		return this.prefix;
	}
	
	public final String getPrefix() {
		return this.prefixProperty().get();
	}
	
	public final void setPrefix(final String prefix) {
		this.prefixProperty().set(prefix);
	}
	
	public final StringProperty sufixProperty() {
		return this.sufix;
	}
	
	public final String getSufix() {
		return this.sufixProperty().get();
	}
	
	public final void setSufix(final String sufix) {
		this.sufixProperty().set(sufix);
	}
	
	public final IntegerProperty limitProperty() {
		return this.limit;
	}
	
	public final int getLimit() {
		return this.limitProperty().get();
	}
	
	public final void setLimit(final int limit) {
		this.limitProperty().set(limit);
	}
	
	public final BooleanProperty allowLettersProperty() {
		return this.allowLetters;
	}
	
	public final boolean isAllowLetters() {
		return this.allowLettersProperty().get();
	}
	
	public final void setAllowLetters(final boolean allowLetters) {
		this.allowLettersProperty().set(allowLetters);
	}
	
	public final BooleanProperty allowNumbersProperty() {
		return this.allowNumbers;
	}
	
	public final boolean isAllowNumbers() {
		return this.allowNumbersProperty().get();
	}
	
	public final void setAllowNumbers(final boolean allowNumbers) {
		this.allowNumbersProperty().set(allowNumbers);
	}
	
	public final BooleanProperty allowSpecialCharsProperty() {
		return this.allowSpecialChars;
	}
	
	public final boolean isAllowSpecialChars() {
		return this.allowSpecialCharsProperty().get();
	}
	
	public final void setAllowSpecialChars(final boolean allowSpecialChars) {
		this.allowSpecialCharsProperty().set(allowSpecialChars);
	}
	
	public final BooleanProperty allowWhiteSpaceProperty() {
		return this.allowWhiteSpace;
	}
	
	public final boolean isAllowWhiteSpace() {
		return this.allowWhiteSpaceProperty().get();
	}
	
	public final void setAllowWhiteSpace(final boolean allowWhiteSpace) {
		this.allowWhiteSpaceProperty().set(allowWhiteSpace);
	}
	
	public final StringProperty regexProperty() {
		return this.regex;
	}
	
	public final String getRegex() {
		return this.regexProperty().get();
	}
	
	public final void setRegex(final String regex) {
		this.regexProperty().set(regex);
	}
	
	public final ObjectProperty<Case> caseTypeProperty() {
		return this.caseType;
	}
	
	public final Case getCaseType() {
		return this.caseTypeProperty().get();
	}
	
	public final void setCaseType(final Case caseType) {
		this.caseTypeProperty().set(caseType);
	}

	public final BooleanProperty decimalAutoFillProperty() {
		return this.decimalAutoFill;
	}
	

	public final boolean isDecimalAutoFill() {
		return this.decimalAutoFillProperty().get();
	}
	

	public final void setDecimalAutoFill(final boolean decimalAutoFill) {
		this.decimalAutoFillProperty().set(decimalAutoFill);
	}
	
	
	
	
}