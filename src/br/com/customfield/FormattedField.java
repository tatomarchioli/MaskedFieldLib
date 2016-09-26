package br.com.customfield;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;

public class FormattedField extends TextField {
	public static final int NO_LIMIT = -1;
	public static final int NORMAL_CASE = 0;
	public static final int LOWER_CASE = 1;
	public static final int UPPER_CASE = 2;


	private DoubleProperty doubleProperty = new SimpleDoubleProperty();
	private StringProperty unformattedText = new SimpleStringProperty();
	private ObjectProperty<FormatBuilder> formatProperty = new SimpleObjectProperty<>();

	public FormattedField(){
		this(null);
	}

	public FormattedField(FormatBuilder formatter){
		formatProperty.set(formatter == null ? new FormatBuilder() : formatter);
		unformattedText.set("");	
	}

	public void setValue(String text){
		replaceText(0,getText().length(),text == null ? "" : text);
	}

	@Override
	public void replaceText(int start, int end, String text){
		if(!formatProperty.get().decimalModeProperty().get()){
			replace(start,end,text);
		}else{
			replaceDouble(start,end,text);
		}
	}

	private void replaceDouble(int start, int end, String text){
		String old = unformattedText.get();
		int caretPos = getCaretPosition();		
		String valid = validateDouble(text);
		IndexRange selection = getSelection();//If there's a selecion, the caret calc is different.



		//calculate ofsset
		int of = getLength() == 0 ? start : start - getPrefix().length();
		if(of < 0){
			of = 0;
		}else if(of > unformattedText.get().length()){
			of = unformattedText.get().length();
		}
		//calculate end
		int	en = end - getSufix().length();
		if(en < 0){
			en = 0;
		}else if(en > unformattedText.get().length()){
			en = unformattedText.get().length();
		}


		//Set the string builder without the formatting characters ('.' , ',')
		StringBuilder sb = new StringBuilder(unformattedText.get());

		//Do the replacement or insertion.
		if(start == end){
			sb.insert(of, valid);
		}else{
			sb.replace(of, en, valid);
		}

		//Remove the ','
		try{
			sb.deleteCharAt(sb.indexOf(","));
		}catch(StringIndexOutOfBoundsException e){}

		//Inser the ',' on the right place
		if(sb.length()> getDecimalCases()){
			try{
				sb.insert(sb.length()- getDecimalCases(), ",");
			}catch(StringIndexOutOfBoundsException e){}
			//		}else{
			//			String formatted = String.format("%0"+numDecimalCaseProperty.get()+"d", Integer.parseInt(sb.toString()));
			//			sb = new StringBuilder("0,"+formatted);
		}

		try{
			doubleProperty.set(Double.parseDouble(sb.toString().replace(",", "."))); //Set the double value
		}catch(NumberFormatException e){
			doubleProperty.set(0);
		}
		unformattedText.set(sb.toString()); //Set the string formatted to the text holder



		//replace on textfield
		super.replaceText(0, getText().length(),getPrefix()+unformattedText.get()+getSufix());

		//calculate caret pos
		if(valid.length() > 0){
			if(caretPos >= getLength()-getSufix().length()-1){
				caretPos = getLength()-getSufix().length()-1;
			}else if(caretPos == 0){
				caretPos = getPrefix().length();
			}
			if(!old.contains(",") && unformattedText.get().contains(","))caretPos++;//if old not contains ',' and new contains, the carret must go one char to the right
			positionCaret(caretPos+valid.length());
		}else if(end!= start){
			if(selection.getLength()>1)
				positionCaret(caretPos);//there's a selection, so dont change the caret
			else
				positionCaret(caretPos-1);//there's not a selection, so back the caret in one position
		}	

	}


	private String validateDouble(String text){
		String validated = "";
		Pattern pattern2 = Pattern.compile("[0-9]");

		int n = text.length();

		//Runs through the inputed text, verifying each char. If it maches with the setup,
		//then inserts the char on validated text
		for (int i = 0; i < n; i++) {
			Matcher matcher2 = pattern2.matcher(String.valueOf(text.charAt(i)));

			int length = getText().length();

			if((getLimit() == NO_LIMIT || length + validated.length() + 2 <= getLimit() + getSelection().getLength()) && matcher2.find()){
				validated = validated+text.charAt(i);
			}
		}
		return validated;
	}

	private void replace(int start, int end, String text){
		int caretPos = getCaretPosition();		
		String valid = validate(text);
		IndexRange selection = getSelection();//If there's a selecion, the caret calc is different.


		//calculate ofsset
		int of = getLength() == 0 ? start : start-getPrefix().length();
		if(of < 0){
			of = 0;
		}else if(of > unformattedText.get().length()){
			of = unformattedText.get().length();
		}

		//calculate end
		int	en = end;
		en = en - getPrefix().length();
		if(en < 0){
			en = 0;
		}else if(en > unformattedText.get().length()){
			en = unformattedText.get().length();
		}

		//replace on the unformatted
		StringBuilder sb = new StringBuilder(unformattedText.get());
		if(start == end){
			sb.insert(of, valid);
		}else{
			sb.replace(of, en, valid);
		}

		unformattedText.set(sb.toString());

		//replace on textfield
		super.replaceText(0, getText().length(), getPrefix()+unformattedText.get()+getSufix());

		//calculate caret pos
		if(valid.length() > 0){
			if(caretPos >= getLength()-getSufix().length()-1){
				caretPos = getLength()-getSufix().length()-1;
			}else if(caretPos == 0){
				caretPos = getPrefix().length();
			}
			positionCaret(caretPos+valid.length());
		}else if(end!= start){
			if(selection.getLength()>1)
				positionCaret(caretPos);//there's a selection, so dont change the caret
			else
				positionCaret(caretPos-1);//there's not a selection, so back the caret in one position
		}	
	}

	//Validate the input
	private String validate(String text){
		String validated = "";
		Pattern pattern1 = Pattern.compile("[A-Z a-z à-ú &&[^\\s]]");
		Pattern pattern2 = Pattern.compile("[0-9]");
		Pattern pattern3 = Pattern.compile("[\\W|_&&[^\\sà-ú]]");
		Pattern pattern4 = Pattern.compile("\\s");

		int n = text.length();

		//Runs through the inputed text, verifying each char. If it maches with the setup,
		//then inserts the char on validated text
		for (int i = 0; i < n; i++) {
			Matcher matcher1 = pattern1.matcher(String.valueOf(text.charAt(i)));
			Matcher matcher2 = pattern2.matcher(String.valueOf(text.charAt(i)));
			Matcher matcher3 = pattern3.matcher(String.valueOf(text.charAt(i)));
			Matcher matcher4 = pattern4.matcher(String.valueOf(text.charAt(i)));

			int length = getText().length();

			if((getLimit() == NO_LIMIT || length+validated.length()+1 <= getLimit()+getSelection().getLength() )&&
					!(!isAllowLetters() && matcher1.find())&&
					!(!isAllowNumbers() && matcher2.find())&&
					!(!isAllowSpecialChars() && matcher3.find())&&
					!(!isAllowWhiteSpace() && matcher4.find())){

				if(getCase() == LOWER_CASE){
					validated = validated+Character.toLowerCase(text.charAt(i));
				}else if(getCase() == UPPER_CASE){
					validated = validated+Character.toUpperCase(text.charAt(i));					
				}else{
					validated = validated+text.charAt(i);
				}
			}
		}
		return validated;
	}

	private Double doubleFormat(double d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.parseDouble(df.format(d).replace(",", "."));
	}

	@SuppressWarnings("unused")
	private Double doubleFormat(String d) {
		if(d == null || d.length() == 0){
			return 0.00;
		}
		double dou = Double.parseDouble(d.replace(",", "."));
		return doubleFormat(dou);
	}


	@SuppressWarnings("unused")
	private String getAsMoney(Number obj){
		NumberFormat f = NumberFormat.getNumberInstance();  
		f.setMinimumFractionDigits(2);
		f.setMaximumFractionDigits(2);

		return f.format(obj);
	}




	//Properties---------------------------------------------------------
	public DoubleProperty doubleProperty() {
		return doubleProperty;
	}

	public ObjectProperty<FormatBuilder> formatProperty() {
		return formatProperty;
	}



	//Getters and setters------------------------------------------------
	public void setDoubleValue(double value){
		doubleProperty.set(value);
		if(formatProperty.get().decimalModeProperty().get()){
			setText(String.valueOf(value));
		}
	}

	public double getDoubleValue(){
		return doubleProperty.get();		                        
	}

	public FormatBuilder getFormat(){
		return formatProperty.get();
	}

	public void setFormat(FormatBuilder value){
		formatProperty.set(value);
	}



	//Properties bridge------------------------------------------------------
	public IntegerProperty numDecimalCaseProperty(){
		return formatProperty.get().numDecimalCaseProperty();
	}

	public IntegerProperty caseProperty(){
		return formatProperty.get().caseProperty();
	}

	public StringProperty prefixProperty() {
		return formatProperty.get().prefixProperty();
	}

	public StringProperty sufixProperty() {
		return formatProperty.get().sufixProperty();
	}

	public IntegerProperty limitProperty() {
		return formatProperty.get().limitProperty();
	}

	public BooleanProperty allowLettersProperty(){
		return formatProperty.get().allowLettersProperty();
	}

	public BooleanProperty allowNumbersProperty(){
		return formatProperty.get().allowNumbersProperty();
	}

	public BooleanProperty allowSpecialCharsProperty(){
		return formatProperty.get().allowSpecialCharsProperty();
	}

	public BooleanProperty allowWhiteSpaceProperty(){
		return formatProperty.get().allowWhiteSpaceProperty();
	}

	public BooleanProperty decimalModeProperty() {
		return formatProperty.get().decimalModeProperty();
	}



	//Getters and setters Bridge--------------------------------------------------
	public void setCase(int value){
		if(value <0 || value > 2){
			throw new IllegalArgumentException("Case must be NORMAL, UPPER or LOWER");
		}
		getFormat().setCase(value);
	}

	public int getCase(){
		return getFormat().getCase();
	}

	public boolean isAllowLetters() {
		return getFormat().isAllowLetters();
	}

	public void setAllowLetters(boolean allowLetters) {
		getFormat().setAllowLetters(allowLetters);
	}

	public boolean isAllowNumbers() {
		return getFormat().isAllowNumbers();
	}

	public void setAllowNumbers(boolean allowNumbers) {
		getFormat().setAllowNumbers(allowNumbers);
	}

	public boolean isAllowSpecialChars() {
		return getFormat().isAllowSpecialChars();
	}

	public void setAllowSpecialChars(boolean allowSpecialChars) {
		getFormat().setAllowSpecialChars(allowSpecialChars);
	}

	public boolean isAllowWhiteSpace() {
		return getFormat().isAllowWhiteSpace();
	}

	public void setAllowWhiteSpace(boolean allowWhiteSpace) {
		getFormat().setAllowWhiteSpace(allowWhiteSpace);
	}

	public int getLimit(){
		return getFormat().getLimit();
	}

	public void setLimit(int limit){
		this.getFormat().setLimit(limit);
	}

	public void setPrefix(String prefix){
		getFormat().setPrefix(prefix);
	}

	public String getPrefix(){
		return getFormat().getPrefix();
	}

	public void setSufix(String sufix){
		getFormat().setSufix(sufix);
	}

	public String getSufix(){
		return getFormat().getSufix();
	}

	public void setDecimalCases(int num){
		getFormat().setDecimalCases(num);
	}

	public int getDecimalCases(){
		return getFormat().getDecimalCases();
	}

	public void setDecimalMode(boolean bol){
		getFormat().setDecimalMode(bol);
	}

	public boolean isDecimalMode(){
		return getFormat().isDecimalMode();
	}

}
