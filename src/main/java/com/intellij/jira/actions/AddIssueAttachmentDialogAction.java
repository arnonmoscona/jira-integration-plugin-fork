package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddIssueAttachmentDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.CREATE_ATTACHMENTS;

public class AddIssueAttachmentDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Add Attachment",  AllIcons.General.Add);

    private final String issueKey;

    public AddIssueAttachmentDialogAction(String issueKey) {
        super(properties);
        this.issueKey = issueKey;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, CREATE_ATTACHMENTS);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to attach files");
        }

        AddIssueAttachmentDialog dialog = new AddIssueAttachmentDialog(project, issueKey);
        dialog.show();
    }

}
