#!/bin/bash

# exit when any command fails
set -e

GRADLE_ARGS="-Pversion=1.4.2"

source build.sh

echo "Publishing 📢"

## https://github.com/gradle-nexus/publish-plugin/
./gradlew $GRADLE_ARGS publishToSonatype
#./gradlew $GRADLE_ARGS publishToSonatype closeAndReleaseSonatypeStagingRepository
