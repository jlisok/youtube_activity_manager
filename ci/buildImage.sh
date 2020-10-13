#!/usr/bin/env bash

set -xe

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

# IMAGE_VERSION will be passed through environment variables
IMAGE_VERSION_STRING=${IMAGE_VERSION:-"0.0.1-SNAPSHOT"}
IMAGE_TAG="com.jlisok/youtube_activity_manager:${IMAGE_VERSION_STRING}"
ECR_IMAGE_TAG="955603615851.dkr.ecr.eu-central-1.amazonaws.com/${IMAGE_TAG}"

"${SCRIPT_DIR}"/../gradlew bootBuildImage --imageName "${IMAGE_TAG}"
docker tag "${IMAGE_TAG}" "${ECR_IMAGE_TAG}"
docker push "${ECR_IMAGE_TAG}"



