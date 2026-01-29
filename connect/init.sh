#!/bin/sh

echo "Waiting for Kafka Connect..."

# Waiting while Kafka Connect up
while [ $(curl -s -o /dev/null -w %{http_code} http://kafka-connect:8083) -ne 200 ] ; do
  echo "Connect not ready yet..."
  sleep 5
done

echo "Deploying connector config..."

# Download config
curl -X PUT http://kafka-connect:8083/connectors/publisher-outbox-connector/config \
     -H "Content-Type: application/json" \
     -d @/etc/kafka-connect/connector-config.json

echo "\nDone!"