pipeline {
    agent any
    triggers {
        pollSCM '* * * * *' // 5 stars means poll the scm every minute
    }
    tools {
        maven 'Maven 3.6.3'
    }
    options {
        skipStagesAfterUnstable()
    }
    environment {
        //Extract branch name in a way that works on a simple pipeline and also on multibranch pipelines
        BRANCH_NAME = "${GIT_BRANCH.split('/').size() > 1 ? GIT_BRANCH.split('/')[0..-1].join('_') : GIT_BRANCH}"
    }
    stages { // Continuous Integration phase
        stage('Unit Test') {
            steps {
                    sh 'mvn test'
            }
            post { // 	If the maven goal succeeded, archive the JUnit test reports for display in the Jenkins web UI.
                success {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

//         stage('Integration Test') { // Usually not run in CI b/c it takes a long time. Usually run by a scheduled job
//             steps {
//                 withCredentials([file(credentialsId: 'IEXCloud', variable: 'FILE')]) {
//                     dir('/Users/mlewis/.jenkins/workspace/SPD-Pipeline_' + BRANCH_NAME + '/target/classes') {
//                         sh 'cat $FILE > secrets.properties'
//                     }
//
//                     sh 'mvn -B -Dskip.surefire.tests verify' // failsafe:integration-test'
//                 }
//             }
//         }
        stage('Build') { // The Continuous Delivery phase
            steps {
                withCredentials([file(credentialsId: 'IEXCloud', variable: 'FILE')]) {
                    dir('/Users/mlewis/.jenkins/workspace/SPD-Pipeline_' + BRANCH_NAME + '/target/classes') {
                        sh 'cat $FILE > secrets.properties'
                    }

                    sh 'mvn -B -Dmaven.clean.skip=true -DskipTests package'
                }
            }
            post { // If the maven build succeeded, archive the jar file
                success {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
        stage('Deploy') { // The Continuous Deployment phase
            steps {
                echo "TODO DEPLOY TO AWS"
                //sh 'mvn -DskipTests deploy'
            }
        }
    }
   post {
        always {
            echo 'Cleaning up resources...'
//             withCredentials([file(credentialsId: 'IEXCloud', variable: 'FILE')]) {
//                 dir('/Users/mlewis/.jenkins/workspace/SPD-Pipeline_' + BRANCH_NAME + '/target/classes') {
//                     sh 'rm secrets.properties'
//                 }
//             }
        }
        success {
            echo 'SUCCESS: SPD-Pipeline completed successfully'
        }
        failure {
            echo 'FAILURE: SPD-Pipeline failed'
        }
        unstable {
            echo 'This will run only if the run was marked as unstable'
        }
        changed {
            echo 'This will run only if the state of the Pipeline has changed'
            echo 'For example, if the Pipeline was previously failing but is now successful'
        }
   }
}