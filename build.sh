#!/bin/sh

# exit when any command fails
set -e

echo "Cleanup 🧹"
rm -rf build

printf '\nBuilding 📦\n'

#./gradlew build --scan
./gradlew $GRADLE_ARGS build

printf '\nTesting 🧪\n'

./gradlew $GRADLE_ARGS check --stacktrace
