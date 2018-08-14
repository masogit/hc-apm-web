#!/bin/bash
if [ "$1" == "dev" ]; then
  SERVER="120.132.21.52"
  PUB="apm-dev"
  TARGET="/usr/local/apps/apache-tomcat-8.0.43/webapps/geapm"
fi

if [ "$1" == "uat" ]; then
  SERVER="120.132.8.152"
  PUB="apm-uat"
  TARGET="/usr/local/apps/apache-tomcat-8.0.43/webapps/geapm"
fi

if [ "$1" == "prod" ]; then
  SERVER="120.132.8.142"
  PUB="apm-prod"
  TARGET="/usr/local/apps/apache-tomcat-8.5.12/webapps/geapm"
fi

echo push 'resources' folder to $SERVER...
rsync -Pav -e "ssh -i ~/.ssh/$PUB" ./src/main/webapp/resources root@$SERVER:$TARGET
echo push 'react' folder to $SERVER...
rsync -Pav -e "ssh -i ~/.ssh/$PUB" ./src/main/webapp/react root@$SERVER:$TARGET --delete

echo done.