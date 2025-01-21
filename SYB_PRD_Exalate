import com.exalate.basic.domain.hubobject.v1.BasicHubOption
import com.exalate.basic.domain.hubobject.v1.BasicHubIssue
import com.exalate.hubobject.jira.AttachmentHelper
import com.exalate.hubobject.jira.CommentHelper
import jcloudnode.services.jcloud.hubobjects.NodeHelper
import jcloudnode.services.replication.PreparedHttpClient
import com.exalate.basic.domain.BasicIssueKey
import com.exalate.services.utils.CollectionService

public class SYB_PRD_Exalate{
	public SYB_PRD_Exalate(){
	
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++ FNWE +++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public def SYB_FWE_SSB_outgoingSync(BasicHubIssue issue, BasicHubIssue replica){
    replica.issueKey = issue.keyissueUrl
    replica.key            = issue.key
    replica.type           = issue.type
    replica.assignee       = issue.assignee
    replica.summary        = issue.summary
    replica.description    = issue.description
    replica.priority       = issue.priority
    replica.attachments    = issue.attachments

//--------------   Custom Fields -----------------

    replica.customFields."FNWE - support organization release number"  =  issue.customFields."FNWE - support organization release number"
    replica.customFields."FNWE - support PRD component"                =  issue.customFields."FNWE - support PRD component"
    replica.customFields."FNWE - support PRD team"                     =  issue.customFields."FNWE - support PRD team"
    replica.customFields."FNWE - support internal qualification"       =  issue.customFields."FNWE - support internal qualification"
    replica.customFields."FNWE - support request type"                 =  issue.customFields."FNWE - support request type"
    replica.customFields."FNWE - support PRD organization"             =  issue.customFields."FNWE - support PRD organization"
}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public def SYB_FWE_SSB_incomingSync(BasicHubIssue issue, BasicHubIssue replica, CommentHelper commentHelper){
//---------------         Custom Fields (CF)  ---------------------
// Key replica
issue.customFields."FNWE - support PRD issue number"?.value = replica.key

//------------------- Processing Field Team
if (replica.customFields."TeamWidget"?.value?.value != null) {
    issue.customFields."FNWE - support PRD team"?.value = replica.customFields."TeamWidget"?.value?.value
}

///------------------- Field Components   

def lastcomponent = replica.components?.isEmpty() ? null : replica.components
if (lastcomponent != null) {
   issue.customFields."FNWE - support PRD component"?.value = replica.components.collect { it.name }.join(", ")
} else {
   issue.customFields."FNWE - support PRD component".value = null
}

//--------------- FIXVERSIONS

def fixVersionNames = replica.fixVersions?.collect { it.name }
issue.customFields."FNWE - support PRD release number"?.value = fixVersionNames?.join(", ")


//-------------- RELEASE DATE
issue.customFields."FNWE - support PRD release date"?.value = replica.customFields."Release date"?.value

//-------------- PRD RESOLUTION  ------------------------------------------
if (replica.resolution == null) {
   issue.customFields."FNWE - support PRD Resolution".value = null
}
 
if (replica.resolution != null) {
  
   def resolutionMap = [
        "Done" : "Done",
        "Cannot Reproduce" :  "Cannot Reproduce",
        "Duplicate":"Duplicate",
        "Won't Do":"Won't Do",
        "Created in error": "Created in error",
        "Information provided" : "Information provided",
		  "Awaiting PS" : "Awaiting PS"
   ]
   def resolutionPRD = resolutionMap[replica.resolution.name]
    issue.customFields."FNWE - support PRD Resolution".value = resolutionPRD
   if (resolutionPRD != "Done") {
       issue.comments = commentHelper.mergeComments(issue, replica, {it.internal = true})
   }  
}
//-------- Cas du Awaiting PS : manque d'info pour repasser en in progress cote SSB
def newLabel = "AWAITING_PS"
if (replica.labels.any { it.label == newLabel }) {
    issue.customFields."FNWE - support PRD Resolution".value = "Awaiting PS"
    issue.comments = commentHelper.mergeComments(issue, replica, {it.internal = true})
}
//----------------------------- COMMENTS
// Ajouter le dernier commentaire à l'issue destination
//issue.comments = commentHelper.mergeComments(issue, replica)

//-------------- On force la fermeture de la demande SSB si demande SYB Done
//if (replica.resolution != null) {
//if (issue.status.name == "Awaiting R&D" && replica.resolution.name == "Done") 
//{
//   workflowHelper.transition(issue, "Clôture R&D")
//}}
}
//end of function SYB_FNWE_incomingSync
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++ PRD +++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public def SYB_PRD_normal_incomingSync(BasicHubIssue issue, BasicHubIssue replica, AttachmentHelper attachmentHelper){
    //--------------  File attachments 
    issue.attachments  = attachmentHelper.mergeAttachments(issue, replica)
}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public def SYB_PRD_DSO_create_incomingSync(BasicHubIssue issue, BasicHubIssue replica, NodeHelper nodeHelper){
    issue.projectKey  = "DSO"
    
    //issue.typeName = nodeHelper.getIssueType(replica.type?.name, issue.projectKey ?: issue.project?.key)?.name ?: "Bug"
    if(replica.customFields."FNWE - support request type"?.value.value == "Bug"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Bug"
    } 
    if(replica.customFields."FNWE - support request type"?.value.value == "Suggestion"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
	 if(replica.customFields."FNWE - support request type"?.value.value == "Support"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 

    if(replica.customFields."FNWE - support request type"?.value.value == "Incident"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
	if(replica.customFields."FNWE - support request type"?.value.value == "FAQ"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
    if(replica.customFields."FNWE - support request type"?.value.value == null){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
    if(replica.customFields."FNWE - support request type"?.value.value == "Point d'extension"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
                 
    } 
       
   //------------------- Field Priority   

  def priorityMapping = [
    // remote side priority <-> local side priority             
    "Blocker" : "Critical",            
    "High" : "High",
    "Medium" : "Medium",
    "Low" : "Low"
  ]

  def priorityName = priorityMapping[replica.priority?.name] ?: "Low" // set default priority in case the proper urgency could not be found
  issue.priority = nodeHelper.getPriority(priorityName)

//-------------------- SUMMARY ------------------------------------
issue.summary = replica.summary 
//store(issue)
 
// ------- END FUNCTION SYB_PRD_DSO_create_incomingSync
}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public def SYB_PRD_DSO_update_incomingSync(BasicHubIssue issue, BasicHubIssue replica, NodeHelper nodeHelper){
issue.projectKey  = "DSO"
 // -------------------- Field Reporter  - to check with modification w/email
def defaultUser = nodeHelper.getUserByEmail("jean-pierre.frayssinhes@forterro.com")
issue.reporter  = nodeHelper.getUserByEmail(replica.assignee?.email) ?: defaultUser
//-------------------------------------------- Custom Fields Customers
   issue.customFields."Customers".value   = replica.customFields."FNWE - support PRD organization".value
//------------------- Processing Field WORKTYPE

def worktypemap = [
    "Bug":"Bug",
    "Suggestion":"Suggestion",
    "Support":"Support",
	"Incident":"Incident",
	"FAQ":"FAQ",
	"Point d'extension":"Suggestion",
	"":"Bug",
    null:"Bug"
]

def worktypePRD = worktypemap[replica.customFields."FNWE - support request type"?.value?.value]
issue.customFields."Work type"?.value   = worktypePRD
	
//------------------ Labels pour point d'extension -------------------
if (replica.customFields."FNWE - support request type"?.value?.value == "Point d'extension") {
	//issue.labels += nodeHelper.getLabel("POINTS_EXTENSION")
}
//------------------- Processing Field Team

def teammap = [
    "PRD-T047-Sylob-Titan":"6620a434-fc95-4521-88f0-94856d09ba68-45",
    "PRD-T048-Sylob-Atlas":"6620a434-fc95-4521-88f0-94856d09ba68-3",
    "PRD-T045-Sylob-Orthanc":"6620a434-fc95-4521-88f0-94856d09ba68-10",
	"PRD-T046-Sylob-Pollux":"6620a434-fc95-4521-88f0-94856d09ba68-49",
	"PRD-T043-Sylob-DevOps":"6620a434-fc95-4521-88f0-94856d09ba68-19",
	"PRD-T053-Sylob-Doc":"1a5c037a-d931-40c2-ad44-99de205bfe45",
	"PRD-T044-Sylob-QA":"6620a434-fc95-4521-88f0-94856d09ba68-44",
	"":"",
    null:"None"
]

def teamPRD = teammap[replica.customFields."FNWE - support PRD team"?.value?.value]
issue.customFields."Team"?.value   = teamPRD

//------------------- Field Components   

    if(replica.customFields."FNWE - support PRD component"?.value?.value){
       issue.projectKey = "DSO" 
       def project = issue.project ?: nodeHelper.getProject(issue.projectKey)
       def ComponentString = replica.customFields."FNWE - support PRD component"?.value.value
       def component = nodeHelper.getComponent(ComponentString, project)
          issue.components += component
    }
          else { issue.components=null}
 
 //------------------- Field FNWE - support customer servicepack affects version  

if(replica.customFields."FNWE - support organization release number"?.value){
    issue.projectKey = "DSO" 
    def project = nodeHelper.getProject(issue.projectKey)
    def affectsVersionString = replica.customFields."FNWE - support organization release number"?.value
    def affectedVersionsList = []

    if (affectsVersionString) {
      def version = nodeHelper.getVersion(affectsVersionString, project)
         if (version) {
             affectedVersionsList << version
         } else {
        affectedVersionsList << nodeHelper.createVersion(issue, affectsVersionString, "")}
    
    }
issue.affectedVersions = affectedVersionsList
}
//------------------ Processing field Description 

// Vérifiez si le champ source contient des données
if (!replica.customFields."FNWE - support internal qualification".value) {
  issue.description = replica.description
} else {
  issue.description = "Support Analysis :" + "\n\n" + replica.customFields."FNWE - support internal qualification".value + "\n\n" + 
  "Customer Description : " + "\n\n" + (replica.description ?: "")
}
	
// ------- END FUNCTION SYB_PRD_DSO_update_incomingSync
} 
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
public def SYB_PRD_SYB_create_incomingSync(BasicHubIssue issue, BasicHubIssue replica, NodeHelper nodeHelper){
    issue.projectKey  = "SYB"
    
    //issue.typeName = nodeHelper.getIssueType(replica.type?.name, issue.projectKey ?: issue.project?.key)?.name ?: "Bug"
    if(replica.customFields."FNWE - support request type"?.value.value == "Bug"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Bug"
    } 
    if(replica.customFields."FNWE - support request type"?.value.value == "Suggestion"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
	 if(replica.customFields."FNWE - support request type"?.value.value == "Support"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 

    if(replica.customFields."FNWE - support request type"?.value.value == "Incident"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
	if(replica.customFields."FNWE - support request type"?.value.value == "FAQ"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
    if(replica.customFields."FNWE - support request type"?.value.value == null){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
    if(replica.customFields."FNWE - support request type"?.value.value == "Point d'extension"){
        issue.typeName    = nodeHelper.getIssueType(replica.customFields."FNWE - support request type"?.value.value, issue.projectKey)?.name ?: "Story"
    } 
       
   //------------------- Field Priority   

  def priorityMapping = [
    // remote side priority <-> local side priority             
    "Blocker" : "Critical",            
    "High" : "High",
    "Medium" : "Medium",
    "Low" : "Low"
  ]

  def priorityName = priorityMapping[replica.priority?.name] ?: "Low" // set default priority in case the proper urgency could not be found
  issue.priority = nodeHelper.getPriority(priorityName)

//-------------------- SUMMARY ------------------------------------
issue.summary = replica.summary 
//store(issue)
 
// ------- END FUNCTION SYB_PRD_SYB_create_incomingSync
}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public def SYB_PRD_SYB_update_incomingSync(BasicHubIssue issue, BasicHubIssue replica, NodeHelper nodeHelper){
issue.projectKey  = "SYB"
 // -------------------- Field Reporter  - to check with modification w/email
def defaultUser = nodeHelper.getUserByEmail("jean-pierre.frayssinhes@forterro.com")
issue.reporter  = nodeHelper.getUserByEmail(replica.assignee?.email) ?: defaultUser
//-------------------------------------------- Custom Fields Customers
   issue.customFields."Customers".value   = replica.customFields."FNWE - support PRD organization".value
//------------------- Processing Field WORKTYPE

def worktypemap = [
    "Bug":"Bug",
    "Suggestion":"Suggestion",
    "Support":"Support",
	"Incident":"Incident",
	"FAQ":"FAQ",
	"Point d'extension":"Suggestion",
	"":"Bug",
    null:"Bug"
]

def worktypePRD = worktypemap[replica.customFields."FNWE - support request type"?.value?.value]
issue.customFields."Work type"?.value   = worktypePRD
	
//------------------ Labels pour point d'extension -------------------
if (replica.customFields."FNWE - support request type"?.value?.value == "Point d'extension") {
	//issue.labels += nodeHelper.getLabel("POINTS_EXTENSION")
	    
	 //   issue.setLabels(["POINTS_EXTENSION"])
	
}
//------------------- Processing Field Team

def teammap = [
    "PRD-T047-Sylob-Titan":"6620a434-fc95-4521-88f0-94856d09ba68-45",
    "PRD-T048-Sylob-Atlas":"6620a434-fc95-4521-88f0-94856d09ba68-3",
    "PRD-T045-Sylob-Orthanc":"6620a434-fc95-4521-88f0-94856d09ba68-10",
	"PRD-T046-Sylob-Pollux":"6620a434-fc95-4521-88f0-94856d09ba68-49",
	"PRD-T043-Sylob-DevOps":"6620a434-fc95-4521-88f0-94856d09ba68-19",
	"PRD-T053-Sylob-Doc":"1a5c037a-d931-40c2-ad44-99de205bfe45",
	"PRD-T044-Sylob-QA":"6620a434-fc95-4521-88f0-94856d09ba68-44",
	"":"",
    null:"None"
]

def teamPRD = teammap[replica.customFields."FNWE - support PRD team"?.value?.value]
issue.customFields."Team"?.value   = teamPRD
//------------------- Field Components   

    if(replica.customFields."FNWE - support PRD component"?.value?.value){
       issue.projectKey = "SYB" 
       def project = issue.project ?: nodeHelper.getProject(issue.projectKey)
       def ComponentString = replica.customFields."FNWE - support PRD component"?.value.value
       def component = nodeHelper.getComponent(ComponentString, project)
          issue.components += component
    }
          else { issue.components=null}
 
//------------------- Field FNWE - support customer servicepack affects version  

if(replica.customFields."FNWE - support organization release number"?.value){
    issue.projectKey = "SYB" 
    def project = nodeHelper.getProject(issue.projectKey)
    def affectsVersionString = replica.customFields."FNWE - support organization release number"?.value
    def affectedVersionsList = []

    if (affectsVersionString) {
      def version = nodeHelper.getVersion(affectsVersionString, project)
         if (version) {
             affectedVersionsList << version
         } else {
        affectedVersionsList << nodeHelper.createVersion(issue, affectsVersionString, "")}
    
    }
issue.affectedVersions = affectedVersionsList
}

//------------------ Processing field Description 

// Vérifiez si le champ source contient des données
if (!replica.customFields."FNWE - support internal qualification".value) {
  issue.description = replica.description
} else {
  issue.description = "Support Analysis :" + "\n\n" + replica.customFields."FNWE - support internal qualification".value + "\n\n" + 
  "Customer Description : " + "\n\n" + (replica.description ?: "")
}
	
// ------- END FUNCTION SYB_PRD_SYB_update_incomingSync
}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public def SYB_PRD_normal_outgoingSync(BasicHubIssue issue, BasicHubIssue replica, NodeHelper nodeHelper){
replica.key                         = issue.key
replica.fixVersions                 = issue.fixVersions
replica.components                  = issue.components
replica.customFields."Release date" = issue.customFields."Release date"
replica.customFields."TeamWidget"   = issue.customFields."TeamWidget"
//replica.customFields."Team"         = issue.customFields."Team"


//------------ on envoie le labels dans le cas où on passe en AWAITING_PS car manque d'information
replica.labels = issue.labels

// Récupérer le dernier commentaire
   def lastComment = issue.comments?.isEmpty() ? null : issue.comments.last()

   if (lastComment) {
      replica.comments = [lastComment]
   }
  
//-----------------  Le ticket est resolu ---------

if (issue.resolutionDate) {
   //--- On envoie la resolution (done ou won't do)
   replica.resolution = issue.resolution
}

//-------------  Le ticket peut etre installé : cas du critical patch

if (issue.priority.name == "Critical" && issue.status.name == "Testing") {
     replica.resolution = nodeHelper.getResolution("Information provided")
}
//-------- END FUNCTION SYB_PRD_outgoingSync
}	
//-------- END CLASS SYB_PRD_Exalate
}
