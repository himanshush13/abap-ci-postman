import jenkins.model.*

def GITURL = 'https://github.com/himanshush13/abap-ci-postman.git'
def BRANCH = 'master'
def PACKAGE = '''$zdevops'''
def COVERAGE = 80
def VARIANT = "DEFAULT"

parallel (
    "NPL":{
        node {
        	def LABEL = "NPL"
        	def HOST = "in-blr-1709.corp.capgemini.com"
        	def CREDENTIAL = "NPL"
        	
        	git poll: true, branch: BRANCH, url: GITURL
        		
        	stage('[' + LABEL + '] Preparation') {
        		  steps{
                     withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ID OF YOUR CREDENTIALS', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                     sh '''
                     newman run https://github.com/himanshush13/abap-ci-postman/master/SimpleRESTTest.postman_collection.json -k --bail --environment https://github.com/himanshush13/abap-ci-postman/master/SimpleRESTTest.postman_collection.json -k --timeout-request 120000 --global-var "username=$USERNAME" --global-var "password=$PASSWORD" --global-var "package=$zdevops" 
                     '''
                     }
        	}
        	
        	def sap_pipeline = load "sap-pipeline/sap.groovy"
        	sap_pipeline.abap_unit(LABEL,HOST,CREDENTIAL,PACKAGE,COVERAGE)
        	sap_pipeline.abap_sci(LABEL,HOST,CREDENTIAL,PACKAGE,VARIANT)
        	sap_pipeline.sap_api_test(LABEL,HOST,CREDENTIAL)
        }
    }
    /* Add more system as needed...
	,"NPL":{
        node {
        	def LABEL = "NPL"
        	def HOST = "vhcalnplci.dummy.nodomain"
        	def CREDENTIAL = "NPL"
        	
        	git poll: true, branch: BRANCH, url: GITURL
        		
        	stage('[' + LABEL + '] Preparation') {
        		deleteDir()
        		dir('sap-pipeline') {
        			bat "git clone " + PIPELINE_GITURL + " ."
        		}
        	}
        	
        	def sap_pipeline = load "sap-pipeline/sap.groovy"
        	sap_pipeline.abap_unit(LABEL,HOST,CREDENTIAL,PACKAGE,COVERAGE)
        	sap_pipeline.abap_sci(LABEL,HOST,CREDENTIAL,PACKAGE,VARIANT)
        	sap_pipeline.sap_api_test(LABEL,HOST,CREDENTIAL)
        }
	} */
)
