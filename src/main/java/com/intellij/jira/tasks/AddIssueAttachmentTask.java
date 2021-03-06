package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class AddIssueAttachmentTask extends AbstractBackgroundableTask {

    private File attachment;

    public AddIssueAttachmentTask(@NotNull Project project, @NotNull String issueKey, @NotNull File attachment) {
        super(project, "Adding issue attachment...", issueKey);
        this.attachment = attachment;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueAttachment(issueIdOrKey, attachment);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue attachment has not been added");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Issue attachment added successfully");
    }

}
