#FROM alpine:3.8
FROM openjdk:8-alpine

# This Dockerfile is optimized for go binaries, change it as much as necessary
# for your language of choice.

RUN apk --no-cache add ca-certificates=20190108-r0 libc6-compat=1.1.20-r5

ENV MAVEN_VERSION 3.5.4
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION /usr/lib/mvn


COPY /carpoolling/target/carpoolling*.jar /myapp

EXPOSE 9091
#ENTRYPOINT [ "/car-pooling-challenge" ]
ENTRYPOINT [ "/usr/bin/java", "-jar", "/myapp" ]
