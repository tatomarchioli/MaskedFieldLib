package br.com.customfield;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.customfield.FormatBuilder.Case;
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
	private DoubleProperty doubleValue = new SimpleDoubleProperty();
	private StringProperty value = new SimpleStringProperty("");
	private NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private ObjectProperty<FormatBuilder> format = new SimpleObjectProperty<>(new FormatBuilder());

	public FormattedField() {
		this(null);
	}

	public FormattedField(FormatBuilder formatter) {
		format.set(formatter == null ? new FormatBuilder() : formatter);
		decimalCasesProperty().addListener((obs, oldv, newv) -> {
			numberFormat.setMinimumFractionDigits(getDecimalCases());
			numberFormat.setMaximumFractionDigits(getDecimalCases());
		});

		value.addListener((obs, oldv, newv) -> {
			if (isDecimalMode() && !isFocused()) {
				setText(numberFormat.format(getDoubleValue()));
				return;
			}
			setText(newv);
		});

		focusedProperty().addListener((obs, oldv, newv) -> {
			if (isDecimalMode()) {
				if (!newv) {
					setText(numberFormat.format(getDoubleValue()));
				} else {
					setText(value.get());
				}
			}
		});
	}

	@Override
	public void replaceText(int start, int end, String text) {
		if (!format.get().decimalModeProperty().get()) {
			replace(start, end, text);
		} else {
			replaceDouble(start, end, text);
		}
	}

	private void replace(int start, int end, String text) {
		int caretPos = getCaretPosition();
		String valid = getRegex() != null ? validateRegex(text) : validate(text);
		IndexRange selection = getSelection();

		// calculate ofsset
		int of = getLength() == 0 ? start : start - getPrefix().length();
		if (of < 0) {
			of = 0;
		} else if (of > value.get().length()) {
			of = value.get().length();
		}

		// calculate end
		int en = end;
		en = en - getPrefix().length();
		if (en < 0) {
			en = 0;
		} else if (en > value.get().length()) {
			en = value.get().length();
		}

		//Replace on the value property
		StringBuilder sb = new StringBuilder(value.get());
		if (start == end) {
			sb.insert(of, valid);
		} else {
			sb.replace(of, en, valid);
		}

		//This sets the value on textfield
		value.set(sb.toString());

		// calculate caret pos
		if (valid.length() > 0) {
			if (caretPos >= getLength() - getSufix().length() - 1) {
				caretPos = getLength() - getSufix().length() - 1;
			} else if (caretPos == 0) {
				caretPos = getPrefix().length();
			}
			positionCaret(caretPos + valid.length());
		} else if (end != start) {
			if (selection.getLength() > 1)
				positionCaret(caretPos);// there's a selection, so dont change
			// the caret
			else
				positionCaret(caretPos - 1);// there's not a selection, so back
			// the caret in one position
		}
	}

	private void replaceDouble(int start, int end, String text) {
		String old = value.get();
		int caretPos = getCaretPosition();

		IndexRange selection = getSelection();// If there's a selecion, the
		// caret calc is different.

		// calculate ofsset
		int of = getLength() == 0 ? start : start - getPrefix().length();
		if (of < 0) {
			of = 0;
		} else if (of > value.get().length()) {
			of = value.get().length();
		}
		// calculate end
		int en = end - getSufix().length();
		if (en < 0) {
			en = 0;
		} else if (en > value.get().length()) {
			en = value.get().length();
		}

		String valid = isDecimalAutoFill() ? validateDouble(text) : validateManualDouble(text, of, en);

		// Set the string builder without the formatting characters ('.' , ',')
		StringBuilder sb = new StringBuilder(value.get());

		// Do the replacement or insertion.
		if (start == end) {
			sb.insert(of, valid);
		} else {
			sb.replace(of, en, valid);
		}

		if (isDecimalAutoFill()) {
			// Remove the ','
			try {
				sb.deleteCharAt(sb.indexOf(","));
			} catch (StringIndexOutOfBoundsException e) {
			}

			// Inser the ',' on the right place
			if (sb.length() > getDecimalCases()) {
				try {
					sb.insert(sb.length() - getDecimalCases(), ",");
				} catch (StringIndexOutOfBoundsException e) {
				}
				// }else{
				// String formatted =
				// String.format("%0"+numDecimalCaseProperty.get()+"d",
				// Integer.parseInt(sb.toString()));
				// sb = new StringBuilder("0,"+formatted);
			}
		}
		try {
			doubleValue.set(Double.parseDouble(sb.toString().replace(",", "."))); // Set
			// the
			// double
			// value
		} catch (NumberFormatException e) {
			doubleValue.set(0);
		}
		value.set(sb.toString()); // Set the string formatted to the text holder

		// replace on textfield
		// super.replaceText(0,
		// getText().length(),getPrefix()+sb.toString()+getSufix());

		// calculate caret pos
		if (valid.length() > 0) {
			if (caretPos >= getLength() - getSufix().length() - 1) {
				caretPos = getLength() - getSufix().length() - 1;
			} else if (caretPos == 0) {
				caretPos = getPrefix().length();
			}
			if (!old.contains(",") && value.get().contains(","))
				caretPos++;// if old not contains ',' and new contains, the
			// carret must go one char to the right
			positionCaret(caretPos + valid.length());
		} else if (end != start) {
			if (selection.getLength() > 1)
				positionCaret(caretPos);// there's a selection, so dont change
			// the caret
			else
				positionCaret(caretPos - 1);// there's not a selection, so back
			// the caret in one position
		}

	}

	private String validateDouble(String text) {
		String validated = "";
		Pattern pattern2 = Pattern.compile("[0-9]");

		int n = text.length();

		// Runs through the inputed text, verifying each char. If it maches with
		// the setup,
		// then inserts the char on validated text
		for (int i = 0; i < n; i++) {
			Matcher matcher2 = pattern2.matcher(String.valueOf(text.charAt(i)));
			int length = getText().replaceAll("\\D", "").length();
			// int sel = (getSelection().getLength() > getLimit() ? getLimit() :
			// getSelection().getLength())-1;
			if ((getLimit() == FormatBuilder.NO_LIMIT || length + validated.length() <= getLimit() - 1)
					&& matcher2.find()) {
				validated = validated + text.charAt(i);
			}
		}
		return validated;
	}

	private String validateManualDouble(String text, int of, int en) {
		String validated = "";
		Pattern pattern2 = Pattern.compile("[0-9]");

		int n = text.length();

		// Runs through the inputed text, verifying each char. If it maches with
		// the setup,
		// then inserts the char on validated text
		StringBuilder sb = new StringBuilder(getText());
		for (int i = 0; i < n; i++) {
			boolean isC = String.valueOf(text.charAt(i)).equals(",");
			Matcher matcher2 = pattern2.matcher(String.valueOf(text.charAt(i)));
			boolean canInsert = true;

			int cindex = sb.toString().indexOf(',');
			if (cindex >= of || cindex == -1) {
				if (getLimit() != FormatBuilder.NO_LIMIT) {
					int len = sb.toString().split(",")[0].length();
					int max = getLimit() - getDecimalCases();
					if (!getSelectedText().contains(","))
						max += (getSelectedText().length() > max ? max : getSelectedText().length());
					canInsert = len < max;
				}
			} else if (cindex != -1) {
				int len = 0;
				try {
					len = sb.toString().split(",")[1].length();
				} catch (Exception e) {
				}
				int max = getDecimalCases();
				if (!getSelectedText().contains(","))
					max += (getSelectedText().length() > max ? max : getSelectedText().length());
				canInsert = len < max;
			}

			if ((matcher2.find() && canInsert) || (isC && cindex == -1)) {
				validated = validated + text.charAt(i);
				sb.insert(of, text.charAt(i));
				of++;
			}

		}
		return validated;
	}

	// Validate the input
	private String validate(String text) {
		String validated = "";
		Pattern pattern1 = Pattern.compile("[A-Z a-z à-ú &&[^\\s]]");
		Pattern pattern2 = Pattern.compile("[0-9]");
		Pattern pattern3 = Pattern.compile("[\\W|_&&[^\\sà-ú]]");
		Pattern pattern4 = Pattern.compile("\\s");

		int n = text.length();

		// Runs through the inputed text, verifying each char. If it maches with
		// the setup,
		// then inserts the char on validated text
		for (int i = 0; i < n; i++) {
			Matcher matcher1 = pattern1.matcher(String.valueOf(text.charAt(i)));
			Matcher matcher2 = pattern2.matcher(String.valueOf(text.charAt(i)));
			Matcher matcher3 = pattern3.matcher(String.valueOf(text.charAt(i)));
			Matcher matcher4 = pattern4.matcher(String.valueOf(text.charAt(i)));

			int length = getText().length();

			if ((getLimit() == FormatBuilder.NO_LIMIT
					|| length + validated.length() + 1 <= getLimit() + getSelection().getLength())
					&& !(!isAllowLetters() && matcher1.find()) && !(!isAllowNumbers() && matcher2.find())
					&& !(!isAllowSpecialChars() && matcher3.find()) && !(!isAllowWhiteSpace() && matcher4.find())) {

				if (getCaseType() == Case.LOWER_CASE) {
					validated = validated + Character.toLowerCase(text.charAt(i));
				} else if (getCaseType() == Case.UPPER_CASE) {
					validated = validated + Character.toUpperCase(text.charAt(i));
				} else {
					validated = validated + text.charAt(i);
				}
			}
		}
		return validated;
	}

	// Validate the input
	private String validateRegex(String text) {
		String validated = "";
		Pattern pattern = Pattern.compile(getRegex());
		// Runs through the inputed text, verifying each char. If it maches with
		// the setup,
		// then inserts the char on validated text
		for (int i = 0; i < text.length(); i++) {
			Matcher matcher1 = pattern.matcher(String.valueOf(text.charAt(i)));

			int length = getText().length();

			if ((getLimit() == FormatBuilder.NO_LIMIT
					|| length + validated.length() + 1 <= getLimit() + getSelection().getLength()) 
					&& matcher1.find()) {

				if (getCaseType() == Case.LOWER_CASE) {
					validated = validated + Character.toLowerCase(text.charAt(i));
				} else if (getCaseType() == Case.UPPER_CASE) {
					validated = validated + Character.toUpperCase(text.charAt(i));
				} else {
					validated = validated + text.charAt(i);
				}
			}
		}
		return validated;
	}

	public final DoubleProperty doubleValueProperty() {
		return this.doubleValue;
	}

	public final double getDoubleValue() {
		return this.doubleValueProperty().get();
	}

	public final void setDoubleValue(double value) {
		if (format.get().decimalModeProperty().get()) {
			setValue(String.valueOf(value).replace(".", ","));
		}
	}

	public final StringProperty valueProperty() {
		return this.value;
	}

	public final String getValue() {
		return this.valueProperty().get();
	}

	public void setValue(String text) {
		replaceText(0, getText().length(), text == null ? "" : text);
	}

	public final ObjectProperty<FormatBuilder> formatProperty() {
		return this.format;
	}

	public final FormatBuilder getFormat() {
		return this.formatProperty().get();
	}

	public final void setFormat(final FormatBuilder format) {
		this.formatProperty().set(format);
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * */

	public final BooleanProperty decimalModeProperty() {
		return getFormat().decimalModeProperty();
	}

	public final boolean isDecimalMode() {
		return this.decimalModeProperty().get();
	}

	public final void setDecimalMode(final boolean decimalMode) {
		this.decimalModeProperty().set(decimalMode);
	}

	public final IntegerProperty decimalCasesProperty() {
		return getFormat().decimalCasesProperty();
	}

	public final int getDecimalCases() {
		return this.decimalCasesProperty().get();
	}

	public final void setDecimalCases(final int decimalCases) {
		this.decimalCasesProperty().set(decimalCases);
	}

	public final StringProperty prefixProperty() {
		return getFormat().prefixProperty();
	}

	public final String getPrefix() {
		return this.prefixProperty().get();
	}

	public final void setPrefix(final String prefix) {
		this.prefixProperty().set(prefix);
	}

	public final StringProperty sufixProperty() {
		return getFormat().sufixProperty();
	}

	public final String getSufix() {
		return this.sufixProperty().get();
	}

	public final void setSufix(final String sufix) {
		this.sufixProperty().set(sufix);
	}

	public final IntegerProperty limitProperty() {
		return getFormat().limitProperty();
	}

	public final int getLimit() {
		return this.limitProperty().get();
	}

	public final void setLimit(final int limit) {
		this.limitProperty().set(limit);
	}

	public final BooleanProperty allowLettersProperty() {
		return getFormat().allowLettersProperty();
	}

	public final boolean isAllowLetters() {
		return this.allowLettersProperty().get();
	}

	public final void setAllowLetters(final boolean allowLetters) {
		this.allowLettersProperty().set(allowLetters);
	}

	public final BooleanProperty allowNumbersProperty() {
		return getFormat().allowNumbersProperty();
	}

	public final boolean isAllowNumbers() {
		return this.allowNumbersProperty().get();
	}

	public final void setAllowNumbers(final boolean allowNumbers) {
		this.allowNumbersProperty().set(allowNumbers);
	}

	public final BooleanProperty allowSpecialCharsProperty() {
		return getFormat().allowSpecialCharsProperty();
	}

	public final boolean isAllowSpecialChars() {
		return this.allowSpecialCharsProperty().get();
	}

	public final void setAllowSpecialChars(final boolean allowSpecialChars) {
		this.allowSpecialCharsProperty().set(allowSpecialChars);
	}

	public final BooleanProperty allowWhiteSpaceProperty() {
		return getFormat().allowWhiteSpaceProperty();
	}

	public final boolean isAllowWhiteSpace() {
		return this.allowWhiteSpaceProperty().get();
	}

	public final void setAllowWhiteSpace(final boolean allowWhiteSpace) {
		this.allowWhiteSpaceProperty().set(allowWhiteSpace);
	}

	public final StringProperty regexProperty() {
		return getFormat().regexProperty();
	}

	public final String getRegex() {
		return this.regexProperty().get();
	}

	public final void setRegex(final String regex) {
		this.regexProperty().set(regex);
	}

	public final ObjectProperty<Case> caseTypeProperty() {
		return getFormat().caseTypeProperty();
	}

	public final Case getCaseType() {
		return this.caseTypeProperty().get();
	}

	public final void setCaseType(final Case caseType) {
		this.caseTypeProperty().set(caseType);
	}

	public final BooleanProperty decimalAutoFillProperty() {
		return getFormat().decimalAutoFillProperty();
	}

	public final boolean isDecimalAutoFill() {
		return this.decimalAutoFillProperty().get();
	}

	public final void setDecimalAutoFill(final boolean decimalAutoFill) {
		this.decimalAutoFillProperty().set(decimalAutoFill);
	}

}
