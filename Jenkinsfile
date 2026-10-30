pipeline {
    agent any

    environment {
        // Forces Playwright to use an accessible directory for browser binaries
        PLAYWRIGHT_BROWSERS_PATH = "${WORKSPACE}/ms-playwright"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Playwright Browsers') {
            steps {
                // Installs the binaries directly into the workspace-mapped path
                bat 'mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install"'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'allure-results']]
            ])
        }
        success {
            echo 'Pipeline passed successfully! 🚀'
        }
        failure {
            echo 'Pipeline failed. Check the Allure report for details.'
        }
    }
}