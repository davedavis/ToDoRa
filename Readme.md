# ToDoRa
An android app that lets you use Jira like a basic to-do app, keeping
all your personal and work tasks in one interface.

# API
https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issues/#api-rest-api-3-issue-issueidorkey-get

# API Token
https://developer.atlassian.com/cloud/jira/platform/basic-auth-for-rest-apis/
MpkWzHiXp3QdnrtdSZdqF38A

# Sample API Queries

curl -D- \
   -u dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A \
   -X GET \
   -H "Content-Type: application/json" \
   https://davedavis.atlassian.net/rest/api/2/issue/createmeta



   curl -D- \
   -u dave@davedavis.io:MpkWzHiXp3QdnrtdSZdqF38A \
   -X GET \
   -H "Content-Type: application/json" \
   https://davedavis.atlassian.net/rest/api/2/search?jql=project="TODORA"


# Sample API Response
sampleresponse.json


