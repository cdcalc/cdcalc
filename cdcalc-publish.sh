#!/usr/bin/env bash

./gradlew clean
./gradlew bintrayUpload
./gradlew publishPlugins
