FROM tomcat:8.5.96-jdk1.8-centos
RUN apk --update add mkfontscale fontconfig ttf-dejavu
COPY ./etc/fonts /usr/share/fonts

COPY ./target/geapm-1.0.0.war /usr/local/tomcat/apache-tomcat-8.5.96/webapps/geapm.war
EXPOSE 8080
