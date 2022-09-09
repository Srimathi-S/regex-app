# Getting Started with the App.

Run `./gradlew build` and start the application

## Usage

##### `curl -X POST -H "Content-Type: application/json" -d '{"expression": "{regexExpression}"}'  http://localhost:8080/describe`

Replace the {regexExpression} with the regex expression you want to know the description to

## For example

##### `curl -X POST -H "Content-Type: application/json" -d '{"expression": "[0-9]{3}"}'  http://localhost:8080/describe`

returns `[["matches any character from 0-9","matches previous token exactly 3 times"]]`