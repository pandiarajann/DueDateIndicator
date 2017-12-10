package com.example.plugins.tutorial;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.query.Query;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.MailException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DueDateIndicator extends AbstractJiraContextProvider
{
    private static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
    private SearchService searchService = null;
    private SearchResults searchResult = null;

    @Override
    public Map getContextMap(ApplicationUser user, JiraHelper jiraHelper) {
        Map contextMap = new HashMap();
        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
        Timestamp dueDate = currentIssue.getDueDate();
        SearchResults jqlSearchResult = null;
        //SMTPMailServer mailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
        //Email statusMail = new Email("princeanand.anandaraj@object-frontier.com");

        Project currentProject = (Project) jiraHelper.getContextParams().get("project");
        String query = "project = " + currentProject.getName() + " AND status != Done AND issue in issueHistory()";
        jqlSearchResult = jqlSearchService(query, user);
        
        if (dueDate != null)
        {
            int currentTimeInDays = (int) (System.currentTimeMillis() / MILLIS_IN_DAY);
            int dueDateTimeInDays = (int) (dueDate.getTime() / MILLIS_IN_DAY);
            int daysAwayFromDueDateCalc = dueDateTimeInDays - currentTimeInDays + 1;
            contextMap.put("daysAwayFromDueDate", daysAwayFromDueDateCalc);
        }

         /*if (jqlSearchResult.getIssues() != null) {
        	statusMail.setSubject("Status of Project " + currentProject.getName());
        	statusMail.setBody(searchResult.getIssues().toString());
        	statusMail.setFrom(mailServer.getDefaultFrom());

                try {
        	    mailServer.send(statusMail);
                } catch (MailException mailException) {
                    mailException.printStackTrace();
                }
        }*/

        contextMap.put("availableIssueTypes", currentIssue.getStatusObject());
        return contextMap;
    }

    public SearchResults jqlSearchService(String query, User user) {
    	
    	searchService = ComponentAccessor.getComponent(SearchService.class);
    	SearchService.ParseResult parseResult=searchService.parseQuery(user, query);
    	
    	try {
    	    searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
    	} catch (SearchException searchException) {
    	    searchException.printStackTrace();
    	}
    	
	return searchResult;
    }
}

