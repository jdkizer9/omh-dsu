#!/bin/bash

if [ "$1" == "" ]; then
    echo "Usage: build-images {docker-hub-repo-name} {version-tag}";
    exit 1;
fi

if [ "$2" == "" ]; then
    echo "Usage: build-images {docker-hub-repo-name} {version-tag}";
    exit 1;
fi

echo "Docker Hub Repo Name: $1"
echo "Version Tag: $2"

./gradlew :authorization-server:build -x test &&
docker build -t $1/ohmage-auth-server:$2 authorization-server/docker/
docker push $1/ohmage-auth-server:$2

./gradlew :resource-server:build -x test &&
docker build -t $1/ohmage-resource-server:$2 resource-server/docker/ &&
docker push $1/ohmage-resource-server:$2

# ./gradlew :ohmageomh-manage-server:build -x test &&
# docker build -t $1/ohmage-manage-server:$2 ohmageomh-manage-server/docker/  &&
# docker push $1/ohmage-manage-server:$2
