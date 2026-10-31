pipeline {
    agent any

    stages {
        stage('Clean & Build') {
            steps {
                bat 'mvn clean compile'
            }
        }
        stage('Run Self-Healing Tests') {
            steps {
                bat 'mvn test -DsuiteXmlFile=src/test/resources/testng.xml'
            }
            post {
                always {
                    // Archive Allure results for telemetry reporting
                    allure includeProperties: false,
                           results: [[path: 'allure-results']]
                }
            }
        }
    }
}