node {
    stage('Build Docker Image') {
        checkout scm
        sh 'docker build -t web-app-tools:ci .'
    }
}