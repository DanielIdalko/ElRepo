class BasicFieldSync 
{
  static receive(issue,
		 		 replica,
		 		 nodeHelper,
		 		 commentHelper,
		 		 attachmentHelper) {

    issue.summary      = replica.summary
	issue.description  = replica.description
	issue.assignee     = nodeHelper.getUserByUsername(replica.assignee?.username)
	//issue.reporter     = nodeHelper.getUserByUsername(replica.reporter?.username)
	issue.labels       = replica.labels
	issue.comments     = commentHelper.mergeComments(issue, replica)
	issue.attachments  = attachmentHelper.mergeAttachments(issue, replica)
  }
}
