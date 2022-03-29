# killall terminal
mv VERSION VERSION.tmp
mv VERSION.prod VERSION
./gradlew app:bundleRelease --warning-mode=all 
cd /home/rdos/D/android_fact/app/build/outputs/bundle/release
killall nautilus
nautilus ./
cd ~/D/android_fact/
mv VERSION VERSION.prod
mv VERSION.tmp VERSION
