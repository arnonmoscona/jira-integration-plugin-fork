package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.isNotEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.util.Objects.nonNull;

public class DateFieldEditor extends AbstractFieldEditor {

    private static final DateFormatter DATE_FORMATTER = new DateFormatter(new SimpleDateFormat("yyyy-MM-dd"));

    @SuppressWarnings("unused")
    private JPanel myPanel;
    protected JFormattedTextField myFormattedTextField;
    protected JLabel myInfoLabel;
    private Date currentValue;

    public DateFieldEditor(String fieldName, String issueKey, boolean required, Object currentValue) {
        super(fieldName, issueKey, required);
        if (currentValue instanceof Date) {
            this.currentValue = (Date) currentValue;
        }
    }

    @Override
    public JComponent createPanel() {
        myFormattedTextField.setFormatterFactory(new DefaultFormatterFactory(getDateFormatter()));
        if (currentValue != null) {
            myFormattedTextField.setValue(currentValue);
        }
        myInfoLabel.setToolTipText(getToolTipMessage());
        myInfoLabel.setIcon(AllIcons.Actions.Help);

        return myPanel;
    }


    public DateFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }

    public String getToolTipMessage() {
        return "E.g. yyyy-MM-dd";
    }

    protected String getValue() {
        return nonNull(myFormattedTextField) ? trim(myFormattedTextField.getText()) : "";
    }

    @Override
    public JsonElement getJsonValue() {
        if (isEmpty(myFormattedTextField.getText())) {
            return JsonNull.INSTANCE;
        }

        return createPrimitive(getValue());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if (isRequired() && isEmpty(trim(myFormattedTextField.getText()))) {
            return new ValidationInfo(myLabel.getMyLabelText() + " is required.");
        } else {
            if (isNotEmpty(trim(myFormattedTextField.getText()))) {
                try {
                    LocalDate.parse(myFormattedTextField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException e) {
                    return new ValidationInfo("Wrong format in " + myLabel.getMyLabelText() + " field.");
                }
            }
        }

        return null;
    }
}
