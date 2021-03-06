package com.intellij.jira.ui.renders;

import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;

import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import static com.intellij.jira.util.JiraLabelUtil.IN_PROGRESS_TEXT_COLOR;
import static java.awt.BorderLayout.LINE_START;

public class JiraIssueStatusTableCellRenderer extends JiraIssueTableCellRenderer {

    private String statusName;
    private Color statusCategoryColor;
    private boolean isInProgressCategory;

    public JiraIssueStatusTableCellRenderer(String statusName, Color statusCategoryColor, boolean isInProgressCategory) {
        super();
        this.statusName = statusName;
        this.statusCategoryColor = statusCategoryColor;
        this.isInProgressCategory = isInProgressCategory;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component label = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

        JiraPanel panel = new JiraPanel(new BorderLayout()).withBackground(label.getBackground());
        if(!isSelected){
            panel.withBackground(JiraLabelUtil.getBgRowColor());
        }

        setText(StringUtil.toUpperCase(statusName));
        setBackground(statusCategoryColor);
        setForeground(isInProgressCategory ? IN_PROGRESS_TEXT_COLOR : Color.white);
        setFont(JBFont.create(new Font("SansSerif", Font.BOLD, 8)));
        setBorder(JBUI.Borders.empty(4, 3));

        panel.setBorder(JBUI.Borders.empty(4, 3));
        panel.add(this, LINE_START);

        return panel;
    }

}
