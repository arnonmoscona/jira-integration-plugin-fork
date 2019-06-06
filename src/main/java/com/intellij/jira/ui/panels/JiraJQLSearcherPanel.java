package com.intellij.jira.ui.panels;

import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.components.JQLSearcherObserver;
import com.intellij.jira.events.JQLSearcherEventListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.tasks.RefreshIssuesTask;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class JiraJQLSearcherPanel extends JBPanel implements JQLSearcherEventListener {

    private final Project myProject;

    private ComboBox<JQLSearcher> myComboBox;
    private CollectionComboBoxModel<JQLSearcher> myComboBoxItems;

    public JiraJQLSearcherPanel(@NotNull Project project) {
        super(new BorderLayout());
        this.myProject = project;

        init();
        installListeners();
    }

    private void init() {
        setBorder(JBUI.Borders.empty(2, 4));

        myComboBoxItems = new CollectionComboBoxModel(new ArrayList());
        for(JQLSearcher searcher : getJQLSearcherManager().getSearchers(myProject)){
            JQLSearcher clone = searcher.clone();
            myComboBoxItems.add(clone);
        }

        myComboBox = new ComboBox(myComboBoxItems, 300);
        if(getJQLSearcherManager().hasSelectedSearcher(myProject)){
            myComboBox.setSelectedIndex(getJQLSearcherManager().getSelectedSearcherIndex(myProject));
        }

        add(myComboBox, BorderLayout.WEST);
    }

    private void installListeners() {
        this.myComboBox.addActionListener(e -> {
            JQLSearcher selectedItem = (JQLSearcher) this.myComboBox.getSelectedItem();
            if(nonNull(selectedItem)){
                getJQLSearcherManager().update(myProject, selectedItem.getAlias(), selectedItem, true);
                SwingUtilities.invokeLater(() -> new RefreshIssuesTask(myProject).queue());
            }
        });

        getJQLSearcherObserver().addListener(this);

    }


    private JQLSearcherManager getJQLSearcherManager(){
        return JQLSearcherManager.getInstance();
    }

    private JQLSearcherObserver getJQLSearcherObserver(){
        return myProject.getComponent(JQLSearcherObserver.class);
    }

    @Override
    public void update(List<JQLSearcher> searchers) {
        myComboBoxItems.removeAll();

        if(!searchers.isEmpty()){
            myComboBoxItems.add(searchers);
            if(myComboBox.getSelectedIndex() != getJQLSearcherManager().getSelectedSearcherIndex(myProject)){
                myComboBox.setSelectedIndex(getJQLSearcherManager().getSelectedSearcherIndex(myProject));
            }
        }
        else {
            myComboBoxItems.setSelectedItem(null);
        }

        myComboBoxItems.update();
    }

    @Override
    public void update(JQLSearcher jqlSearcher) {
        // do nothing
    }
}
