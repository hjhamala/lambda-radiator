# Radiator

Radiator is a Clojure application which generates radiator view as html from configurable
endpoints. Radiator can show Gitlab or AWS Codepipeline statuses. Also AWS Cloudwatch alarms 
and custom metrics can be shown.

Currently radiator view is configured as 3*3 grid. This can be changed with little effort.

Radiator is quite usable in situations where is need to monitor multiple AWS accounts. 
For example one can have different accounts for development, testing and production-

Radiator can be installed as a AWS Lambda or run as server. 

Because Radiator targets also Lambda enviroment all resources like images are linked
to external resources. Radiator is hardly usable without some endpoints to poll. 

## Configuring endpoints
Endpoints are configured as Clojure code in namespace radiator.config.
```clj
{:name "Name of the project"
:aws {:uri     "Uri to aws-endpoint poller"
      :api-key "key"}
:gitlab-pipelines [{:name    "Name of the gitlab pipeline"
                    :uri     "Uri to endpoint -> https://{domain}/api/v4/projects/{:id}/pipelines"
                    :api-key "api-key"}]}
```

Gitlab pipelines need only uri and the endpoint.

AWS endpoints need lambdas installed to every account which is monitored. One easy way is to use
radiator exposer from https://github.com/hjhamala/radiator-exposer.

## Other configuration options
Radiator.config namespace contains ways to change used images and texts. Example images are from Tango
desktop project and are freely usable. 

## Installation to AWS
Prerequirities
* [Leiningen][] 2.0.0
* NPM 

Run

    npm install serverless serverless-clj-plugin
    serverless deploy 

Serverless builds and tests application and deploys it behind API Gateway as a lambda.
Radiator needs token as a query parameter. Token can be set in radiator.config.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

## License

Copyright © 2017 FIXME
