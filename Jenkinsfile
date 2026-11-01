pipeline {
    agent any

    stages {
        stage('Clean Workspace Artifacts') {
            steps {
                // Wipe old results so Allure never merges past runs
                bat 'rm -rf allure-results allure-report'
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
                       results: [[path: 'target/allure-results']]
            }
        }
    }

    post {
        always {
            // Optional: archive report artifacts if needed
            cleanWs notFailBuild: true
        }
    }
}