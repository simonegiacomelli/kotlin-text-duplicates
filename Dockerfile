FROM openjdk:8

RUN apt-get update && apt-get install -y screen mc net-tools python3
