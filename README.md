[Project description](https://drive.google.com/open?id=1qHblmwvAgmG7d9NuqgHyuvBtzD6Ue0jv)

Russian (development) and Dutch (coordination and analytics) students worked on this project for Dutch company called Produvar. My role was to lead developers team and create Android application. We've also developed iOS app, test API (according to provided documentation) and back-end.

This is a copy of original repository with Android application that was sent to the client.

This repository includes only a couple dozen last commits.

This is not the final version of the application - it is actual at the time of June, 25 2018. Arisen bugs will be fixed, token storage techniques will be added, but later commits won't be included in public repositories and especially in this "version for CV".

Test API ([class](https://github.com/CepBuch/ProduvarForCV/blob/master/app/src/main/java/produvar/interactionwithapi/TestAPIProvider.kt)) that was used for development purposes was replaced with test offline data ([class](https://github.com/CepBuch/ProduvarForCV/blob/master/app/src/main/java/produvar/interactionwithapi/OfflineAPIProvider.kt) under the same interface) as free hosting may not be available in several months. Company was going to use their own API. 

If you want application to use test API , you can try to change getApiProvider() returned instance in the [Factory](https://github.com/CepBuch/ProduvarForCV/blob/master/app/src/main/java/produvar/interactionwithapi/Factory.kt)  from OfflineAPIProvider() to TestAPIProvider(), but it's not guaranteed that it works at the time you looking at this repository. That is why the application uses offline test data by default.

Note: "status update" function does not work for offline test data  - it is not updated because it's supposed to update on the server side. You can see how it works in the video or try to use "test api" version.

APK with offline imitation of API [is here](https://drive.google.com/open?id=1SB-yVzDZkOT1EHwEG4mVniDkrJBrisC-)

APK with test API [is here](https://drive.google.com/open?id=1DQfO15Dbzd4_CTIme5ZPvmkaeiwE8Ct-)

Instructions about testing the applications (info about users and their permissions) [is here](https://drive.google.com/open?id=1sJqOvSoiijg9G1X--rxQ0YHJeuiNExWeiPlLryhuSeQ)
