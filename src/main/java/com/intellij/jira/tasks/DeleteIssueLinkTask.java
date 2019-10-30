package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class DeleteIssueLinkTask extends AbstractBackgroundableTask{
    private String issueKey;
    private String issueLinkId;

    public DeleteIssueLinkTask(@NotNull Project project, String issueKey, String issueLinkId) {
        super(project, "Deleting comment...");
        this.issueKey = issueKey;
        this.issueLinkId = issueLinkId;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueLink(issueLinkId);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue link has not been deleted");
        }

        // Retrieve updated issue
        Result issueResult = jiraRestApi.getIssue(issueKey);
        if(issueResult.isValid()){
            JiraIssue issue = (JiraIssue) issueResult.get();
            // Update panels
            getJiraIssueUpdater().update(issue);
        }

    }

    @Override
    public void onSuccess() {
        showNotification("Jira", "Issue Link deleted successfully");
    }


}
