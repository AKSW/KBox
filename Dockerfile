FROM java:8-jre
ENV KBOX_VERSION v0.0.2-alpha
RUN wget -O kbox.jar https://github.com/AKSW/KBox/releases/download/${KBOX_VERSION}/kbox-${KBOX_VERSION}.jar
ENTRYPOINT ["java", "-Xmx4G", "-Xms1G", "-jar", "kbox.jar"]
EXPOSE 8080 3030
