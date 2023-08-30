class CustomFieldSync {
    static receive(issue,replica) {

  	issue.summary      = replica.summary
	issue.description  = replica.description
	issue.comments = commentHelper.mergeComments(issue, replica)
  }
}
