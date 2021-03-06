package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AssignUserTask extends AbstractBackgroundableTask {

    private String accountId;
    private String username;

    public AssignUserTask(@NotNull Project project, String accountId,  String username, String issueKey) {
        super(project, "Assigning User to Issue...", issueKey);
        this.accountId = accountId;
        this.username = username;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();
        Result result = jiraRestApi.assignUserToIssue(accountId, username, issueIdOrKey);
        if(!result.isValid()) {
            throw new InvalidResultException("Assignment error", "Issue has not been updated");
        }

    }


    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Assignment successful", "Issue assignee has been updated");
    }

}
