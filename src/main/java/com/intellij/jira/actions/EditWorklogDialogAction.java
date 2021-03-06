package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.EditWorklogDialog;
import com.intellij.jira.util.factory.JiraIssuTimeTrackingFactory;
import com.intellij.jira.util.factory.JiraIssueWorklogFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class EditWorklogDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Edit Work Log", AllIcons.Actions.Edit);

    private String issueKey;
    private String projectKey;
    private JiraIssueWorklogFactory worklogFactory;
    private JiraIssuTimeTrackingFactory timetrackingFactory;

    public EditWorklogDialogAction(String issueKey, String projectKey, JiraIssueWorklogFactory factory, JiraIssuTimeTrackingFactory timetrackingFactory) {
        super(properties);
        this.issueKey = issueKey;
        this.projectKey = projectKey;
        this.worklogFactory = factory;
        this.timetrackingFactory = timetrackingFactory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssueWorklog worklogToEdit = jiraRestApi.getWorklog(issueKey, worklogFactory.create().getId());
        // Check permissions
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_ALL_WORKLOGS);
        if (!userHasPermission) {
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_OWN_WORKLOGS);
            if (!userHasPermission) {
                throw new InvalidPermissionException("Edit Work Log Failed", "You don't have permission to edit work logs");
            }

            if (nonNull(worklogToEdit)
                    && !jiraRestApi.getUsername().equals(worklogToEdit.getAuthor().getName())
                    && !jiraRestApi.getUsername().equals(worklogToEdit.getAuthor().getEmailAddress())) {
                throw new InvalidPermissionException("Edit Work Log Failed", "This work log not yours. You cannot edit it.");
            }
        }

        if (Objects.nonNull(worklogToEdit)) {
            List<String> projectRoles = jiraRestApi.getProjectRoles(projectKey);

            EditWorklogDialog dialog = new EditWorklogDialog(project, issueKey, projectRoles, worklogToEdit, timetrackingFactory.create(), false);
            dialog.show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(worklogFactory.create()));
    }

}
