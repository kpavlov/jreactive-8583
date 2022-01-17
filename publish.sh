
echo "Cleanup 🧹"
rm -rf build

echo "Building 📦"
#./gradlew build --scan
./gradlew build

echo "Testing 🧪"
./gradlew check --stacktrace

echo "Publishing 📢"

## https://github.com/gradle-nexus/publish-plugin/
./gradlew publishToSonatype
#./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
