package com.example.plugins.tutorial;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import java.util.Map;

public interface DueDateIndicatorImpl
{
    Map getContextMap(User user, JiraHelper jiraHelper);
}
