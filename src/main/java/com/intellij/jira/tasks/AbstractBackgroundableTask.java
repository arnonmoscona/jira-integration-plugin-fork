package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.JiraServerConfigurationNotFoundException;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.listener.JiraIssueChangeListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.result.Result;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class AbstractBackgroundableTask extends Task.Backgroundable {

    protected String issueIdOrKey;
    protected JiraServerManager jiraServerManager;

    public AbstractBackgroundableTask(@NotNull Project project, @NotNull String title, String issueIdOrKey) {
        super(project, title, false, ALWAYS_BACKGROUND);
        this.issueIdOrKey = issueIdOrKey;
        this.jiraServerManager = JiraServerManager.getInstance(project);
    }

    @NotNull
    public JiraRestApi getJiraRestApi() throws JiraServerConfigurationNotFoundException{
        JiraRestApi jiraRestApi = jiraServerManager.getJiraRestApi();
        if(isNull(jiraRestApi)) {
            throw new JiraServerConfigurationNotFoundException();
        }

        return jiraRestApi;
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        String content = nonNull(error.getCause()) ? error.getCause().getMessage() : "";
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError(error.getMessage(), content));
    }

    @Override
    public void onSuccess() {
        // Retrieve updated issue
        Result issueResult = getJiraRestApi().getIssue(issueIdOrKey);
        if(issueResult.isValid()){
            JiraIssue issue = (JiraIssue) issueResult.get();
            // Update views
            myProject.getMessageBus().syncPublisher(JiraIssueChangeListener.TOPIC).issueChanged(issue);
        }
    }

    public void showNotification(String title, String content){
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification(title, content));
    }

}
