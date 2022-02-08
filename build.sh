# exit when any command fails
set -e

echo "Cleanup 🧹"
rm -rf build

echo "\nBuilding 📦"
#./gradlew build --scan
./gradlew $GRADLE_ARGS build

echo "\nTesting 🧪"
./gradlew $GRADLE_ARGS check --stacktrace
