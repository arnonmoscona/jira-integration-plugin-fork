package com.intellij.jira.tasks;

import com.google.gson.JsonElement;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EditWorklogTask extends AbstractBackgroundableTask {

    private String workLogId;
    private List<TransitionFieldHelper.FieldEditorInfo> worklogFields;
    private String remainingEstimate;

    public EditWorklogTask(@NotNull Project project, String issueKey, String workLogId, List<TransitionFieldHelper.FieldEditorInfo> worklogFields, JsonElement remainingEstimateValue) {
        super(project, "Editing Work Log", issueKey);
        this.workLogId = workLogId;
        this.worklogFields = worklogFields;
        this.remainingEstimate = remainingEstimateValue.getAsString();
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.editIssueWorklog(issueIdOrKey, workLogId, worklogFields, remainingEstimate);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue Work Log has not been edited");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Work Log edited successfully");
    }

}
