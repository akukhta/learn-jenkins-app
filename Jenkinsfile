/* groovylint-disable NestedBlockDepth */
pipeline {
    agent any
    options {
        skipDefaultCheckout()
    }
    environment {
        NETLIFY_SITE_ID = '4eb3438a-6bee-4b9b-969a-25e84109f695'
        NETLIFY_AUTH_TOKEN = credentials('netlify_token')
        REACT_APP_VERSION = "1.0.$BUILD_ID"
    }
    stages {
        stage('Clean WS') {
            steps {
                cleanWs()
                checkout scm
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t web-app-tools:ci .'
            }
        }
        stage('Build') {
            agent {
                docker {
                    image 'node:18-alpine'
                    reuseNode true
                }
            }

            steps {
                println "Building version ${env.REACT_APP_VERSION}"

                sh 'ls -al'
                sh 'node --version'
                sh 'npm ci --cache /tmp/empty-cache'
                sh 'npm run build'
                sh 'ls -al'
            }
        }
        stage('Unit test')
            {
                agent {
                    docker {
                        image 'node:18-alpine'
                        reuseNode true
                    }
                }
                steps {
                    sh 'ls'
                    sh 'test -f build/index.html'
                    sh 'npm test'
                }
                post {
                    always {
                        junit 'unit-test-results/junit.xml'
                    }
                }
            }
        stage('E2E Test') {
                agent {
                    docker {
                        image 'mcr.microsoft.com/playwright:v1.39.0-jammy'
                        reuseNode true
                    }
                }
                steps {
                    sh '''
                    npm ci --cache /tmp/empty-cache
                    npm install serve --cache /tmp/empty-cache
                    echo 'Serve installed'
                    node_modules/.bin/serve -s build &
                    sleep 10
                    npx playwright test --reporter=html
                    '''
                }
                post {
                    success {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            icon: '', keepAll:
                            false,
                            reportDir: 'playwright-report',
                            reportFiles: 'index.html',
                            reportName: 'HTML Report',
                            reportTitles: '',
                            useWrapperFileDirectly: true]
                        )
                    }
                }
        }
        stage('Deploy Staging') {
            agent {
                docker {
                    image 'web-app-tools:ci'
                    reuseNode true
                    args '-u root:root'
                }
            }
            steps {
                println "Deploy version ${env.REACT_APP_VERSION}"

                sh '''
                    netlify --version
                    netlify status
                    netlify deploy --dir=build --json > deploy_output.json
                '''
                script {
                    env.staging_url = sh(script: "node-jq -r '.deploy_url' deploy_output.json",
                        returnStdout: true)
                }
            }
        }
        stage('Staging E2E Test') {
            agent {
                docker {
                    image 'web-app-tools:ci'
                    reuseNode true
                }
            }
            environment {
                CI_ENVIRONMENT_URL = "${env.staging_url}"
            }
            steps {
                sh '''
                    npx playwright test --reporter=html
                '''
            }
            post {
                success {
                    publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    icon: '', keepAll:
                    false,
                    reportDir: 'playwright-report',
                    reportFiles: 'staging.html',
                    reportName: 'Staging HTML Report',
                    reportTitles: '',
                    useWrapperFileDirectly: true]
                )
                }
            }
        }
        stage('Deploy Production') {
            agent {
                docker {
                    image 'web-app-tools:ci'
                    reuseNode true
                    args '-u root:root'
                }
            }
            steps {
                println "Deploy version ${env.REACT_APP_VERSION}"

                sh '''
                    netlify deploy --dir=build --prod
                '''
            }
        }
        stage('Production E2E Test') {
            agent {
                docker {
                    image 'web-app-tools:ci'
                    reuseNode true
                }
            }
            environment {
                CI_ENVIRONMENT_URL = 'https://incandescent-strudel-0ca7bc.netlify.app'
            }
            steps {
                sh '''
                    npx playwright test --reporter=html
            '''
            }
            post {
                success {
                    publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    icon: '', keepAll:
                    false,
                    reportDir: 'playwright-report',
                    reportFiles: 'index.html',
                    reportName: 'Production HTML Report',
                    reportTitles: '',
                    useWrapperFileDirectly: true]
                )
                }
            }
        }
    }
}
