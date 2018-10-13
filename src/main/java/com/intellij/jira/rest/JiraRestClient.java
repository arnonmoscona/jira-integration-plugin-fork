package com.intellij.jira.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.model.*;
import com.intellij.tasks.jira.JiraRepository;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.intellij.jira.rest.JiraIssueParser.*;
import static com.intellij.jira.util.JiraGsonUtil.*;
import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.util.Objects.nonNull;

public class JiraRestClient {
    private static final Integer DEFAULT_MAX_ISSUES_RESULTS = 500;
    private static final String ISSUE = "issue";
    private static final String TRANSITIONS = "transitions";
    private static final String SEARCH = "search";

    private JiraRepository jiraRepository;

    public JiraRestClient(JiraRepository jiraRepository) {
        this.jiraRepository = jiraRepository;
    }

    public JiraIssue getIssue(String issueIdOrKey) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, issueIdOrKey));
        method.setQueryString(method.getQueryString() + "?fields=" + JiraIssue.REQUIRED_FIELDS);
        String response = jiraRepository.executeMethod(method);
        return parseIssue(response);
    }

    public List<JiraIssue> findIssues() throws Exception {
        GetMethod method = getBasicSearchMethod(this.jiraRepository.getSearchQuery(), DEFAULT_MAX_ISSUES_RESULTS);
        method.setQueryString(method.getQueryString() + "&fields=" + JiraIssue.REQUIRED_FIELDS);
        String response = jiraRepository.executeMethod(method);
        return parseIssues(response);
    }


    public List<JiraIssueTransition> getTransitions(String issueId) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(ISSUE, issueId, TRANSITIONS));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "transitions.fields")});
        String response = jiraRepository.executeMethod(method);
        return parseIssueTransitions(response);
    }


    public String transitIssue(String issueId, String transitionId, Map<String, FieldEditorInfo> requiredFields, Map<String, FieldEditorInfo> optionalFields) throws Exception {
        String requestBody = getTransitionRequestBody(transitionId, requiredFields, optionalFields);
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueId, TRANSITIONS));
        method.setRequestEntity(createJsonEntity(requestBody));
        return jiraRepository.executeMethod(method);
    }

    public List<JiraIssueUser> getAssignableUsers(String issueKey) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("user", "assignable", SEARCH));
        method.setQueryString(new NameValuePair[]{new NameValuePair("issueKey", issueKey)});
        String response = jiraRepository.executeMethod(method);
        return parseUsers(response);
    }


    public String assignUserToIssue(String username, String issueKey) throws Exception {
        String requestBody = "{\"name\": \"" + username + "\"}";
        PutMethod method = new PutMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "assignee"));
        method.setRequestEntity(createJsonEntity(requestBody));
        return jiraRepository.executeMethod(method);
    }


    public JiraIssueComment addCommentToIssue(String body, String issueKey) throws Exception {
        String requestBody = "{\"body\": \"" + body + "\"}";
        PostMethod method = new PostMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "comment"));
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = jiraRepository.executeMethod(method);
        return parseIssueComment(response);
    }


    private GetMethod getBasicSearchMethod(String jql, int maxResults) {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl(SEARCH));
        method.setQueryString(new NameValuePair[]{new NameValuePair("jql", jql), new NameValuePair("maxResults", String.valueOf(maxResults))});
        return method;
    }


    private static RequestEntity createJsonEntity(String requestBody) {
        try {
            return new StringRequestEntity(requestBody, "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding is not supported");
        }
    }


    public String deleteCommentToIssue(String issueKey, String commentId) throws Exception {
        DeleteMethod method = new DeleteMethod(this.jiraRepository.getRestUrl(ISSUE, issueKey, "comment", commentId));
        return jiraRepository.executeMethod(method);
    }

    public List<JiraIssuePriority> getIssuePriorities() throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("priority"));
        String response = jiraRepository.executeMethod(method);
        return parseIssuePriorities(response);
    }

    public String changeIssuePriority(String priorityName, String issueIdOrKey) throws Exception {
        String requestBody = "{\"update\": {\"priority\": [{\"set\": {\"name\": \"" + priorityName + "\"}}]}}";
        PutMethod method = new PutMethod(this.jiraRepository.getRestUrl(ISSUE, issueIdOrKey));
        method.setRequestEntity(createJsonEntity(requestBody));
        return jiraRepository.executeMethod(method);
    }


    public List<JiraIssueUser> findUsersWithPermissionOnIssue(String issueKey, JiraPermission permission) throws Exception {
        GetMethod method = new GetMethod(this.jiraRepository.getRestUrl("user", "permission", SEARCH));
        method.setQueryString(new NameValuePair[]{new NameValuePair("issueKey", issueKey), new NameValuePair("username", jiraRepository.getUsername()), new NameValuePair("permissions", permission.toString())});
        String response = jiraRepository.executeMethod(method);
        return parseUsers(response);
    }


    private String getTransitionRequestBody(String transitionId, Map<String, FieldEditorInfo> requiredFields, Map<String, FieldEditorInfo> optionalFields) {
        JsonObject transition = new JsonObject();
        transition.add("transition", createIdObject(transitionId));

        // Comment
        FieldEditorInfo commentField = optionalFields.remove("comment");
        if(nonNull(commentField) && !isEmpty(trim(commentField.getInputValue()))){
            transition.add("update", createCommentObject(commentField.getInputValue()));
        }

        // TODO: mirar si todos los fields se comportan igual
        //Fields
        JsonObject fieldsObject = new JsonObject();
        requiredFields.forEach((key, value) -> {
            if(!isEmpty(trim(value.getInputValue()))){
                fieldsObject.add(key, createJsonObject(key, value.getInputValue(), value.isArray()));
            }
        });

        optionalFields.forEach((key, value) -> {
            if(!isEmpty(trim(value.getInputValue()))){
                fieldsObject.add(key, createJsonObject(key, value.getInputValue(), value.isArray()));
            }
        });

        if(fieldsObject.size() > 0){
            transition.add("fields", fieldsObject);
        }


        return transition.toString();
    }



    private JsonElement createJsonObject(String name, String value, boolean isArray){
        if(name.equals("summary") || name.equals("description")
                || name.equals("environment") || name.equals("duedate")){
            return new JsonPrimitive(value);
        }

        return createNameObject(value, isArray);
    }



}

