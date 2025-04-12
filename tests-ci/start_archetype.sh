#!/usr/bin/env bash

PORT=$1
if [[ $PORT == '' ]]; then
  echo "*** Using default port 8081"
  PORT='8081'
fi

echo "*** Waiting 5sec to be sure the Jar is here"
sleep 5

echo "*** List folder"
ls -al

echo "*** Starting application ..."
if test -f "mvnw"; then
  ./mvnw \
    spring-boot:run &
  > /dev/null
elif test -f "gradlew"; then
  ./gradlew \
    bootRun &
  > /dev/null
fi

retryCount=1
maxRetry=30
httpUrl="http://localhost:"$PORT"/management/health"

rep=$(curl -v "$httpUrl")
status=$?
while [ "$status" -ne 0 ] && [ "$retryCount" -le "$maxRetry" ]; do
  echo "*** [$(date)] Application not reachable yet. Sleep and retry - retryCount =" $retryCount "/" $maxRetry
  retryCount=$((retryCount + 1))
  sleep 5
  rep=$(curl -v "$httpUrl")
  status=$?
done

if [ "$status" -ne 0 ]; then
  echo "*** [$(date)] Not connected after" $retryCount " retries."
  exit 1
fi
