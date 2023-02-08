#!/bin/sh

build() {
    v=$1
    if [ "$1" = '' ]; then
      echo "please input version, example: v1.0.0"
      exit 1
    fi

    mvn package -Dmaven.test.skip=true

    platform=linux/amd64
    IMAGE_AND_TAG=registry-shdocker-registry.cn-shanghai.cr.aliyuncs.com/tedgedriver/driver-java-example:"$v"
    docker build . --platform "$platform" -t ${IMAGE_AND_TAG} -f ./Dockerfile
    docker push ${IMAGE_AND_TAG}

    if [ $? != 0 ];then
       exit 1
    fi
}

build "$1";
