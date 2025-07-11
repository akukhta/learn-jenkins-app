node {
    stage('Test') {
        checkout scm
        def deployer = load 'DeployWithE2E.groovy'
        deployer.deploy("local")
    }
}