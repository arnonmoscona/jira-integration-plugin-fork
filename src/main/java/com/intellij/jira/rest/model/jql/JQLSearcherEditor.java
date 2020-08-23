package com.intellij.jira.rest.model.jql;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.tasks.jira.jql.JqlLanguage;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;

public class JQLSearcherEditor {

    private final static int DEFAULT_WIDTH = 300;
    private final static int DEFAULT_HEIGHT = 24;

    private final Project myProject;
    private final JQLSearcher mySearcher;
    private boolean mySelectedSearcher;
    private boolean mySharedSearcher;

    private JBLabel myAliasLabel;
    private JBTextField myAliasField;

    private JBLabel mySearchLabel;
    private EditorTextField mySearchQueryField;

    private JCheckBox myDefaultSearcherCheckBox;
    private JCheckBox mySharedSearcherCheckBox;

    private JPanel myPanel;


    public JQLSearcherEditor(@NotNull Project project, @NotNull JQLSearcher searcher, boolean selected) {
        this.myProject = project;
        this.mySearcher = searcher;
        this.mySelectedSearcher = selected;
        this.mySharedSearcher = searcher.isShared();

        init();
    }

    private void init() {
        this.myAliasLabel = new JBLabel("Alias:", 4);
        this.myAliasField = new JBTextField(mySearcher.getAlias());
        this.myAliasField.setPreferredSize(UI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.mySearchLabel = new JBLabel("Search:", 4);
        this.mySearchQueryField = new LanguageTextField(JqlLanguage.INSTANCE, this.myProject, mySearcher.getJql());
        this.mySearchQueryField.setPreferredSize(UI.size(DEFAULT_WIDTH, 30));

        this.myDefaultSearcherCheckBox = new JCheckBox("Set Default");
        this.myDefaultSearcherCheckBox.setBorder(JBUI.Borders.emptyRight(4));
        this.myDefaultSearcherCheckBox.setSelected(mySelectedSearcher);

        this.mySharedSearcherCheckBox = new JCheckBox("Share");
        this.mySharedSearcherCheckBox.setBorder(JBUI.Borders.emptyRight(4));
        this.mySharedSearcherCheckBox.setSelected(mySharedSearcher);


        this.myPanel = FormBuilder.createFormBuilder()
                        .addLabeledComponent(this.myAliasLabel, this.myAliasField)
                        .addLabeledComponent(this.mySearchLabel, this.mySearchQueryField)
                        .addComponent(myDefaultSearcherCheckBox)
                        .addComponent(mySharedSearcherCheckBox)
                        .getPanel();

    }


    public void apply(){
        this.mySearcher.setAlias(trim(myAliasField.getText()));
        this.mySearcher.setJql(trim(mySearchQueryField.getText()));
    }


    public JPanel getPanel(){
        return myPanel;
    }

    @Nullable
    public ValidationInfo validate(){
        if(isEmpty(trim(myAliasField.getText()))){
            return new ValidationInfo("Alias field is required");
        }

        if(isEmpty(trim(mySearchQueryField.getText()))){
            return new ValidationInfo("JQL field is required");
        }

        return null;
    }

    public JBTextField getAliasField() {
        return myAliasField;
    }

    public boolean isSelectedSearcher(){
        return myDefaultSearcherCheckBox.isSelected();
    }

    public boolean isSharedSearcher(){
        return mySharedSearcherCheckBox.isSelected();
    }
}