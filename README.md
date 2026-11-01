# Self-Healing Playwright Java Automation Framework

A production-grade, governance-driven UI automation framework built with Java, Playwright, TestNG, and Allure.

This framework features a **ThreadLocal Dynamic Proxy** for runtime DOM recovery, combined with **Strict Quality Governance** in CI pipelines to eliminate silent technical debt.

---

## 🔑 Key Features

* **Runtime Element Self-Healing:** Intercepts failed locators at runtime and resolves alternative selectors in `<200ms`.
* **Environment-Aware Governance:**
* **Local Environment:** Facilitates fast, fluid local debugging by healing broken selectors dynamically.
* **CI Pipeline (`CI=true`):** Enforces a hard `AssertionError` build failure when self-healing triggers, preventing unmaintained selectors from slipping into master.


* **Instant Delta Telemetry:** Emits explicit failure diagnostics in Allure reports, mapping the broken locator directly against the correct healed selector.
* **Isolated Reporting Pipeline:** Clean workspace sanitization scripts prevent ghost suites and cumulative result duplication across builds.

---

## 🏗️ Architecture & How It Works

```
                     ┌──────────────────────────┐
                     │   Playwright Test Exec   │
                     └─────────────┬────────────┘
                                   │
                           [Locator Strategy]
                                   │
                    ┌──────────────▼──────────────┐
                    │ Does Primary Selector Work? │
                    └──────┬──────────────┬───────┘
                        YES│              │NO
                           │              │
                           ▼              ▼
                    [Execute Action]  [Trigger Healing Engine]
                                          │
                                          ▼
                               [Resolve Healed Locator]
                                          │
                                 ┌────────┴────────┐
                                 │ Is CI=true Set? │
                                 └────┬────────┬───┘
                              NO (Local)│        │YES (CI)
                                      │        │
                                      ▼        ▼
                              [Log & Proceed] [Throw Governance Failure]

```

1. **Proxy Layer:** Every page object interacts with Playwright through a `ThreadLocal` dynamic proxy.
2. **Fallback Resolver:** When an element is missing, the fallback resolver scans alternative strategies (attributes, text contents, relative positions).
3. **Telemetry Interceptor:** Stores original vs. healed selectors and logs colored console warnings alongside Allure attachments.
4. **TestNG Listener (`SelfHealingTestListener`):** Checks if healing occurred at test completion. If `CI=true`, it converts functional passes into test failures with an `AssertionError`.

---

## 🛠️ Prerequisites

* **Java Development Kit (JDK):** Version 21
* **Apache Maven:** 3.8+
* **Jenkins / CI Agent:** Windows CMD or PowerShell execution environment (or standard Linux shell)

---

## 🚀 Getting Started

### 1. Clone & Install

```bash
git clone https://github.com/your-org/playwright-java-self-healing.git
cd playwright-java-self-healing
mvn clean compile

```

### 2. Run Tests Locally

Run tests locally to experience functional self-healing without hard pipeline stops:

```bash
mvn clean test

```

### 3. Run Tests in CI Mode (Governance Enforcement)

Simulate pipeline behavior locally by passing the `CI=true` parameter:

```bash
mvn clean test -DCI=true

```

---

## 📊 Allure Reporting

To generate and serve the Allure report locally after running tests:

```bash
mvn allure:serve

```

### Reporting Highlights

When a self-healing event occurs in CI mode, the test fails and produces the following structured output in Allure:

```text
🚨 [Self-Healing Governance Failure] Test passed functionally via fallback, but failed governance rules.
  • Failed Locator (Broken): #invalid-id-username
  • Correct Locator (Healed): input[name='username']

```

---

## ⚙️ CI/CD Pipeline (Jenkinsfile Example)

Below is the optimized Jenkins Declarative Pipeline stage setup for Windows agents, featuring workspace clearing and post-build report generation:

```groovy
pipeline {
    agent any

    stages {
        stage('Clean Workspace Artifacts') {
            steps {
                // Wipe old Allure results to prevent duplicate suite displays
                powershell 'Remove-Item -Recurse -Force allure-results, allure-report -ErrorAction SilentlyContinue'
            }
        }

        stage('Build and Test') {
            steps {
                // Execute tests with CI governance flag
                bat 'mvn clean test -DCI=true'
            }
        }
    }

    post {
        always {
            // Guarantee report compilation even on build failures
            allure includeProperties: false,
                   jdk: '',
                   results: [[path: 'target/allure-results']]
        }
    }
}

```

---

## 📁 Project Structure

```text
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── playwright
│   │               └── framework
│   │                   ├── proxy/         # ThreadLocal dynamic proxy & locator resolvers
│   │                   └── telemetry/     # Allure interceptors & TestNG listeners
│   └── test
│       ├── java
│       │   └── tests/                     # TestNG test suites
│       └── resources
│           └── testng.xml                 # TestNG suite definition
├── Jenkinsfile                            # CI pipeline definition
└── pom.xml                                # Project dependencies and plugins

```

---

How does this look for your repository root? Let me know if you want to add any specific maintainer contacts or tweak any section before we move on to TypeScript!