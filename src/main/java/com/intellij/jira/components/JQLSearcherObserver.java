package com.intellij.jira.components;

import com.intellij.jira.events.JQLSearcherEventListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraJQLSearcherPanel;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class JQLSearcherObserver implements ProjectComponent, Updater<JQLSearcher> {

    private final Project myProject;

    private List<JQLSearcherEventListener> myListeners;

    public JQLSearcherObserver(Project myProject) {
        this.myProject = myProject;
        this.myListeners = new ArrayList<>();
    }

    @Override
    public void projectClosed() {
        myListeners.clear();
    }

    public void addListener(JiraJQLSearcherPanel jqlSearcherPanel) {
        myListeners.add(jqlSearcherPanel);
    }


    @Override
    public void update(List<JQLSearcher> jqlSearchers) {
        myListeners.forEach(l -> l.update(jqlSearchers));
    }

    @Override
    public void update(JQLSearcher jqlSearcher) {
        myListeners.forEach(l -> l.update(jqlSearcher));
    }
}
