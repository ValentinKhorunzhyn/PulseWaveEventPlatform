FROM debezium/connect:2.7.3.Final

USER root
COPY confluentinc-kafka-connect-avro-converter-7.7.4.zip /tmp/avro-converter.zip
RUN unzip /tmp/avro-converter.zip -d /kafka/connect/ \
    && rm /tmp/avro-converter.zip
USER kafka