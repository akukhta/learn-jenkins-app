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

        stage('Test')
        {
            steps {
                sh 'test -f build/index.html'
                sh 'npm test'
            }
        }

        stage('E2E Test')
        {
            agent {
                docker {
                    image 'mcr.microsoft.com/playwright:v1.53.0-noble'
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
                npx playwright test
                '''
            }
        }
    }

    post {
        always {
            junit 'test-results/junit.xml'
        }
    }
}
