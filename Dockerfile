FROM java:7-jre
ENV KBOX_VERSION v0.0.2-alpha
ENV JAVA_MEM_MIN 1G
ENV JAVA_MEM_MAX 4G
RUN wget -O kbox.jar https://github.com/AKSW/KBox/releases/download/v0.0.2-alpha/kbox-$KBOX_VERSION.jar
ENTRYPOINT ["/bin/bash", "-c", "java -Xmx$JAVA_MEM_MAX -Xms$JAVA_MEM_MIN -jar kbox.jar \"$0\" \"$1\" \"$2\" \"$3\" \"$4\" \"$5\""]
EXPOSE 8080 3030

