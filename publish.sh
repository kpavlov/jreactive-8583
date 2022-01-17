# exit when any command fails
set -e

echo "Cleanup 🧹"
rm -rf build

#GRADLE_ARGS="-Pversion=1.3.5"

echo "Building 📦"
#./gradlew build --scan
./gradlew $GRADLE_ARGS build

echo "Testing 🧪"
./gradlew $GRADLE_ARGS check --stacktrace

echo "Publishing 📢"

## https://github.com/gradle-nexus/publish-plugin/
./gradlew $GRADLE_ARGS publishToSonatype
#./gradlew $GRADLE_ARGS publishToSonatype closeAndReleaseSonatypeStagingRepository
