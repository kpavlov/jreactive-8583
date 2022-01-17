# exit when any command fails
set -e

echo "Cleanup 🧹"
rm -rf build

echo "Building 📦"
#./gradlew build --scan
./gradlew $GRADLE_ARGS build

echo "Testing 🧪"
./gradlew $GRADLE_ARGS check --stacktrace
