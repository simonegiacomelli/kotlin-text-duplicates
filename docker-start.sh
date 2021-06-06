DIR=$(dirname $(readlink -f $0))
echo $DIR
cd $DIR
mkdir -p docker-log
mv reboot.log docker-log/"$(date +"old-%m-%d-%y--%T.%N")".log
date > reboot.log
echo ------------------------------------------------------------ | tee -a reboot.log
echo ------------------------------------------------------------ | tee -a reboot.log
echo ------------------------------------------------------------ | tee -a reboot.log
echo ------------------------------------------------------------ | tee -a reboot.log
./gradlew clean --console=plain 2>&1 | tee -a reboot.log
./gradlew appJsProd --console=plain 2>&1 | tee -a reboot.log
./gradlew buildFatJarV2 --console=plain 2>&1 | tee -a reboot.log

(java -jar build/libs/kotlin-text-duplicates-fat-v2-1.0-SNAPSHOT.jar 2>&1 | tee -a reboot.log) &

sleep infinity

