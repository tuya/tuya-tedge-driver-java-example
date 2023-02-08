FROM registry.shdocker.tuya-inc.top/tedge/amazoncorretto:11.0.17-alpine3.15

# environment variables
ENV APP_DIR=/home/driver
ENV APP=driver-java-example.jar
ENV RES_DIR=$APP_DIR/resources

ENV TZ Asia/Shanghai
RUN apk add --no-cache --upgrade tzdata \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && mkdir -p $APP_DIR \
    && mkdir -p $RES_DIR

#copy JAR and property files to the image
WORKDIR $APP_DIR
COPY target/driver-java-example-jar-with-dependencies.jar $APP_DIR/$APP
COPY src/main/resources/logback.xml $RES_DIR/logback.xml

ENTRYPOINT java -jar -Dlogback.configurationFile=$RES_DIR/logback.xml -Djava.security.egd=file:/dev/urandom -Xmx256M $APP
