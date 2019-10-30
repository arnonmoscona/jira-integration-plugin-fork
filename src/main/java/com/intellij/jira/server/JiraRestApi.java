package com.intellij.jira.server;

import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.JiraRestClient;
import com.intellij.jira.rest.model.*;
import com.intellij.jira.util.BodyResult;
import com.intellij.jira.util.EmptyResult;
import com.intellij.jira.util.Result;
import com.intellij.tasks.jira.JiraRepository;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JiraRestApi {

    private static final Logger log = LoggerFactory.getLogger(JiraRestApi.class);

    private JiraRestClient jiraRestClient;

    public JiraRestApi(JiraRepository jiraRepository) {
        this.jiraRestClient = new JiraRestClient(jiraRepository);
    }


    public Result getIssue(String issueIdOrKey){
        try {
            JiraIssue issue = this.jiraRestClient.getIssue(issueIdOrKey);
            return BodyResult.ok(issue);
        } catch (Exception e) {
            log.error(String.format("Issue %s not found", issueIdOrKey));
        }

        return BodyResult.error();
    }


    public List<JiraIssue> getIssues(String searchQuery) {
        try {
            return this.jiraRestClient.findIssues(searchQuery);
        } catch (Exception e) {
            log.error("No issues found");
            return new ArrayList<>();
        }
    }

    public List<JiraIssueTransition> getTransitions(String issueId){
        try {
            return jiraRestClient.getTransitions(issueId);
        } catch (Exception e) {
            log.error(String.format("No transitions was found for issue '%s'", issueId));
            return new ArrayList<>();
        }
    }


    public Result transitIssue(String issueId, String transitionId, Map<String, FieldEditorInfo> fields){
        try {
            String response = jiraRestClient.transitIssue(issueId, transitionId, fields);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error executing transition '%s' in issue '%s'", transitionId, issueId));
            return EmptyResult.error();
        }
    }

    public List<JiraIssueUser> getAssignableUsers(String issueKey){
        try {
            return jiraRestClient.getAssignableUsers(issueKey);
        } catch (Exception e) {
            log.error("Error fetching users to assign");
            return new ArrayList<>();
        }
    }


    public Result assignUserToIssue(String username, String issueKey){
        try {
            String response = jiraRestClient.assignUserToIssue(username, issueKey);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error assigning user '%s' to issue '%s'", username, issueKey));
            return EmptyResult.error();
        }
    }

    @Nullable
    public JiraIssueComment getComment(String issueKey, String commentId) {
        JiraIssueComment comment = null;
        try {
            comment = jiraRestClient.getComment(issueKey, commentId);
        } catch (Exception e) {
            log.error(String.format("Comment with id = %s doesn't exists", commentId));
        }

        return comment;
    }

    public Result addIssueComment(String body, String issueKey, String viewableBy){
        try {
            JiraIssueComment comment = jiraRestClient.addCommentToIssue(body, issueKey, viewableBy);
            return BodyResult.ok(comment);
        } catch (Exception e) {
            log.error(String.format("Error creating comment in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result editIssueComment(String issueKey, String commentId, String body, String viewableBy){
        try {
            JiraIssueComment comment = jiraRestClient.editIssueComment(issueKey, commentId, body, viewableBy);
            return BodyResult.ok(comment);
        } catch (Exception e) {
            log.error(String.format("Error editing comment in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }


    public Result deleteIssueComment(String issueKey, String commentId) {
        try {
            String response = jiraRestClient.deleteCommentToIssue(issueKey, commentId);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error deleting comment in issue '%s'", issueKey));
            return EmptyResult.error();
        }

    }

    public List<JiraIssuePriority> getIssuePriorities() {
        try {
            return jiraRestClient.getIssuePriorities();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result changeIssuePriority(String priorityName, String issueIdOrKey) {
        try {
            String response = jiraRestClient.changeIssuePriority(priorityName, issueIdOrKey);
            return EmptyResult.create(response);
        } catch (Exception e) {
            e.printStackTrace();
            return EmptyResult.error();
        }

    }

    public boolean userHasPermissionOnIssue(String issueKey, JiraPermissionType permission){
        LinkedHashMap<String, JiraPermission> permissions = new LinkedHashMap<>();
        try {
            permissions = jiraRestClient.findUserPermissionsOnIssue(issueKey);
        } catch (Exception e) {
            log.error("Current user has not permission to do this action");
        }

        JiraPermission jiraPermission = permissions.get(permission.toString());
        if(Objects.isNull(jiraPermission)){
            jiraPermission = permissions.get(permission.getOldPermission());
        }

        return Objects.isNull(jiraPermission) ? false : jiraPermission.isHavePermission();
    }


    public List<JiraIssueLinkType> getIssueLinkTypes(){
        try {
            return jiraRestClient.getIssueLinkTypes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<JiraGroup> getGroups(){
        try {
            return jiraRestClient.getGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getDefaultSearchQuery(){
        return jiraRestClient.getDefaultSearchQuery();
    }

    public boolean testConnection() throws Exception {
        return jiraRestClient.testConnection();
    }

    public List<String> getProjectRoles(String projectKey) {
        try {
            return jiraRestClient.getProjectRoles(projectKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result addIssueLink(String linkType, String inIssueKey, String outIssueKey) {
        try {
            Integer statusCode = jiraRestClient.addIssueLink(linkType, inIssueKey, outIssueKey);
            return statusCode == 201 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error creating issue link");
            return BodyResult.error();
        }
    }

    public Result deleteIssueLink(String issueLinkId) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueLink(issueLinkId);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error creating issue link");
            return BodyResult.error();
        }
    }

    public String getUsername(){
        return jiraRestClient.getUsername();
    }

}