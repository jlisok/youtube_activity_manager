#!/usr/bin/env bash

# IMAGE_VERSION will be passed through environment variables

IMAGE_VERSION_STRING=${IMAGE_VERSION:-"0.0.1-SNAPSHOT"}
IMAGE_NAME="com.jlisok/youtube_activity_manager:${IMAGE_VERSION_STRING}"
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

"${SCRIPT_DIR}"/../gradlew bootBuildImage --imageName "${IMAGE_NAME}"

