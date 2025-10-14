pipeline {
    agent any

    environment {
        MAVEN_USER = credentials('maven')
        TELEGRAM_TOKEN = credentials('telegramToken')
        TELEGRAM_CHAT_ID = "-1001869636392"
    }

    stages {
        stage('Checkout') {
            steps {
                scmSkip(deleteBuild: false, skipPattern:'.*\\[no build\\].*')
            }
        }

        stage('Build e caricamento del modulo') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    sh "./gradlew build --refresh-dependencies -PmavenUser=$MAVEN_USER_USR -PmavenPassword=$MAVEN_USER_PSW"
                }
            }
            post {
                success {
                    script {
                        def jarFile = sh(script: 'ls -1 build/libs/*.jar | head -n 1', returnStdout: true).trim()
                        archiveArtifacts artifacts: jarFile, fingerprint: true
                        sshPublisher(publishers: [
                            sshPublisherDesc(
                                configName: 'AtlantisRP',
                                transfers: [
                                    sshTransfer(
                                        sourceFiles: jarFile,
                                        removePrefix: 'build/libs',
                                        remoteDirectory: 'plugins/'
                                    )
                                ]
                            )
                        ])
                    }
                }
            }
        }

        stage('Publish su Maven') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                script {
                    sh "./gradlew publish -PmavenUser=$MAVEN_USER_USR -PmavenPassword=$MAVEN_USER_PSW"
                }
            }
        }
    }

    post {
        success {
            script {
                sh """#!/bin/bash
                curl -X POST -H "Content-Type: application/json" -d '{"chat_id": "$TELEGRAM_CHAT_ID", "text": "✅ <b>${env.JOB_NAME.split('/')[1]}:${env.BRANCH_NAME}</b> <code>#${env.BUILD_NUMBER}</code> <i>buildato e deployato correttamente.</i>", "parse_mode": "html", "reply_markup": {"inline_keyboard": [[{"text": "📂 Logs", "url": "${env.BUILD_URL}"}]]}}' "https://api.telegram.org/bot$TELEGRAM_TOKEN/sendMessage"
                """
            }
        }
        failure {
            script {
                sh """#!/bin/bash
                curl -X POST -H "Content-Type: application/json" -d '{"chat_id": "$TELEGRAM_CHAT_ID", "text": "❌ <b>${env.JOB_NAME.split('/')[1]}:${env.BRANCH_NAME}</b> <code>#${env.BUILD_NUMBER}</code> <i>non è stato buildato correttamente.</i>", "parse_mode": "html", "reply_markup": {"inline_keyboard": [[{"text": "📂 Logs", "url": "${env.BUILD_URL}"}]]}}' "https://api.telegram.org/bot$TELEGRAM_TOKEN/sendMessage"
                """
            }
        }
    }
}
