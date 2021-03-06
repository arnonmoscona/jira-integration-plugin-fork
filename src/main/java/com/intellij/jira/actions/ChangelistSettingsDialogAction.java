package com.intellij.jira.actions;

import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.ChangelistSettingsDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ChangelistSettingsDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Settings...");

    public ChangelistSettingsDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        ChangelistSettingsDialog dialog = new ChangelistSettingsDialog(project);
        dialog.show();
    }
}
