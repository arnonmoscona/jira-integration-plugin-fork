package com.intellij.jira.rest.model;

import java.util.Date;

public class JiraIssueWorklog {
    private String self;
    private String id;
    private String issueId;

    private JiraIssueUser author;

    private Date created;
    private Date updated;
    private Date started;

    private String timeSpent;
    private String timeSpentSeconds;

    private String comment;

    public JiraIssueWorklog() { }

    public String getId() {
        return id;
    }

    public JiraIssueUser getAuthor() {
        return author;
    }

    public Date getCreated() {
        return created;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public String getComment() {
        return comment;
    }
}
