pipeline {
    agent any
    options {
        skipDefaultCheckout()
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

        stage('Testing') {
            parallel {
                stage('Test')
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
                }

                stage('E2E Test')
                {
                    agent {
                        /* groovylint-disable-next-line NestedBlockDepth */
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
                }
            }
        }
    }

    post {
        always {
            junit 'unit-test-results/junit.xml'
        }
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
                useWrapperFileDirectly: true])
        }
    }
}
