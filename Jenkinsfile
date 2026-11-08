pipeline {
    agent any

    stages {
        stage('Clean Workspace Artifacts') {
            steps {
                bat '''
                    if exist allure-results rmdir /s /q allure-results
                    if exist allure-report rmdir /s /q allure-report
                    exit 0
                '''
            }
        }

        stage('Build and Test') {
            steps {
                // Always use 'clean test' to wipe target/ build artifacts
                bat 'mvn clean test -DCI=true'
            }
        }

        stage('Generate Allure Report') {
            steps {
                // Generate the fresh report from the isolated results directory
                allure includeProperties: false,
                       jdk: '',
                       results: [[path: 'allure-results']]
            }
        }
    }

   post {
    always {
        allure([
            commandline: 'allure', // Must match the exact name in Global Tool Configuration
            includeProperties: false,
            jdk: '',
            properties: [],
            reportBuildPolicy: 'ALWAYS',
            results: [[path: 'target/allure-results']]
        ])
    }
}
}