FROM tomcat:8.5-jre8-alpine

RUN apk --update add mkfontscale fontconfig ttf-dejavu

COPY ./etc/fonts /usr/share/fonts
COPY ./target/geapm-1.0.0.war /usr/local/tomcat/webapps/geapm.war

EXPOSE 8080
