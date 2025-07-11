def deploy(String environment) {
    def steps = [
        "npm install netlify-cli@20.1.1 node-jq --cache /tmp/empty-cache",
        "node_modules/.bin/netlify --version",
        "node_modules/.bin/netlify status",
        "node_modules/.bin/netlify deploy --dir=build --json > deploy_output.json"
    ]

    // def staging_url = sh(script: "node_modules/.bin/node-jq -r '.deploy_url' deploy_output.json",
    //                     returnStdout: true)

    steps.each(step -> echo "${step}")
}

return this