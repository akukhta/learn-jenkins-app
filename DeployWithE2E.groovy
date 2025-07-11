def deploy(String environment) {
    def steps = [
        "npm install netlify-cli@20.1.1 node-jq --cache /tmp/empty-cache",
        "node_modules/.bin/netlify --version",
        "node_modules/.bin/netlify status",
    ]

    def deploy_step = "${environment == 'local' ? 'node_modules/.bin/serve -s build &' : 'node_modules/.bin/netlify deploy --dir=build'}"

    if(environment == 'prod') {
        deploy_step = deploy_step + "prod"
    }

    if (environment != 'local') {
        deploy_step = deploy_step + ' --json > deploy_output.json'
    }

    steps << deploy_step

    steps.each { println "${it}" }

    if (environment != 'local') {
        return sh(script: "node_modules/.bin/node-jq -r '.deploy_url' deploy_output.json",
            returnStdout: true)
    }

    return 'localhost:3000'
}

return this