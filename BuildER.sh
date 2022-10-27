# killall terminal root ANDROIDFACT_D="/home/rdos/D/android-fact"
set -x
ANDROIDFACTd='/home/rdos/D/android-fact/'
mv VERSION VERSION.tmp
mv VERSION.prod VERSION
./gradlew app:bundleRelease --warning-mode=all 
cd "${ANDROIDFACTd}app/build/outputs/bundle/release"

killall nautilus
nautilus ./

cd $ANDROIDFACTd
mv VERSION VERSION.prod
mv VERSION.tmp VERSION