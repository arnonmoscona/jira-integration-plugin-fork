package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.AddIssueAttachmentDialogAction;
import com.intellij.jira.actions.DeleteIssueAttachmentDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.jira.ui.JiraIssueAttachmentListModel;
import com.intellij.jira.ui.renders.JiraIssueAttachmentListCellRenderer;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueAttachmentsPanel extends AbstractJiraPanel {

    private JiraIssueAttachment issueAttachment;
    private JBList<JiraIssueAttachment> issueAttachmentList;

    JiraIssueAttachmentsPanel(JiraIssue issue) {
        super(true, issue);
        initContent(issue.getAttachments());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddIssueAttachmentDialogAction(issueKey));
        group.add(new DeleteIssueAttachmentDialogAction(issueKey, () -> issueAttachment));

        return group;
    }

    private void initContent(List<JiraIssueAttachment> issueAttachments) {
        JBPanel panel = new JBPanel(new BorderLayout());

        issueAttachmentList = new JBList<>();
        issueAttachmentList.setEmptyText("No attachments");
        issueAttachmentList.setModel(new JiraIssueAttachmentListModel(issueAttachments));
        issueAttachmentList.setCellRenderer(new JiraIssueAttachmentListCellRenderer());
        issueAttachmentList.setSelectionMode(SINGLE_SELECTION);
        issueAttachmentList.addListSelectionListener(e -> {
            SwingUtilities.invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueAttachmentList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueAttachment selectedAttachment = issueAttachmentList.getSelectedValue();
        if(!Objects.equals(issueAttachment, selectedAttachment)){
            issueAttachment = selectedAttachment;
            initToolbar();
        }
    }

}
