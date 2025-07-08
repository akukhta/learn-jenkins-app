/* groovylint-disable NestedBlockDepth */
pipeline {
    agent any
    options {
        skipDefaultCheckout()
    }
    environment {
        NETLIFY_SITE_ID = '4eb3438a-6bee-4b9b-969a-25e84109f695'
        NETLIFY_AUTH_TOKEN = credentials('netlify_token')
    }
    stages {
        stage('Clean WS') {
            steps {
                cleanWs()
                checkout scm
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

        stage('Deploy') {
            agent {
                docker {
                    image 'node:18-alpine'
                    reuseNode true
                    args '-u root:root'
                }
            }
            steps {
                sh '''
                    npm install netlify-cli@20.1.1 --cache /tmp/empty-cache
                    node_modules/.bin/netlify --version
                    node_modules/.bin/netlify status
                    node_modules/.bin/netlify deploy --dir=build --prod
                '''
            }
        }
        stage('Production E2E Test') {
            agent {
                docker {
                    image 'mcr.microsoft.com/playwright:v1.39.0-jammy'
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
