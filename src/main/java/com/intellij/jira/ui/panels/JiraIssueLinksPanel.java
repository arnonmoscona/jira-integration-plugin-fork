package com.intellij.jira.ui.panels;

import com.intellij.jira.actions.AddIssueLinkDialogAction;
import com.intellij.jira.actions.DeleteIssueLinkDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueLink;
import com.intellij.jira.ui.JiraIssueLinkListModel;
import com.intellij.jira.ui.renders.JiraIssueLinkListCellRenderer;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssueLinksPanel extends AbstractJiraPanel {

    private JiraIssueLink issueLink;
    private JBList<JiraIssueLink> issueLinkList;

    JiraIssueLinksPanel(@NotNull JiraIssue issue) {
        super(true, issue);
        initContent(issue.getIssueLinks());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddIssueLinkDialogAction(projectKey, issueKey));
        group.add(new DeleteIssueLinkDialogAction(issueKey, () -> issueLink));

        return group;
    }

    private void initContent(List<JiraIssueLink> issueLinks) {
        JBPanel panel = new JBPanel(new BorderLayout());

        issueLinkList = new JBList<>();
        issueLinkList.setEmptyText("No links");
        issueLinkList.setModel(new JiraIssueLinkListModel(issueLinks));
        issueLinkList.setCellRenderer(new JiraIssueLinkListCellRenderer());
        issueLinkList.setSelectionMode(SINGLE_SELECTION);
        issueLinkList.addListSelectionListener(e -> {
            SwingUtilities.invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueLinkList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueLink selectedLink = issueLinkList.getSelectedValue();
        if(!Objects.equals(issueLink, selectedLink)){
            issueLink = selectedLink;
            initToolbar();
        }
    }

}
