FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY encrypt/keystore/encryptionKey.jks encryptionKey.jks
COPY build/libs/config-1.0.jar Config.jar
ENTRYPOINT ["java", "-jar", "Config.jar"]