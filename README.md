### Produvar B.V. mobile interaction with API

[Project description](https://drive.google.com/open?id=1lBALN3c6MkNOsLQxZylIek8Uls5ZK_up)

[Certificate](https://drive.google.com/open?id=1nmlFUlpozSVlI6JmRusH3Njr9_bfI2eu)

*If you want to quickly look at the application, please, go to the last line of this note.*

**Company description:**   
Produvar B.V. is a company located in the vicinity of Groningen, The Netherlands. It it recognised as a software company that tracks orders for different companies. 
The company's existence lies with the need to create a flexible system aimed at small to medium- sized production companies. The focus of the company lies in the delivery of software to the targeted market. After all, Produvar B.V finds software an important factor in all companies, as most people in the company get to deal with software. That is the reason why Produvar B.V. wants to create more
than just a database with an interface. Key for the customer is, that the software is 'friendly' to use, while having a staggering interface that looks good to the eye.
Another important factor for Produvar B.V. to focus on, lies in automatizing most of the unnecessary administrative actions and thus creating more efficiency.

**Task:**  
Create a mobile application that interacts with Produvar B.V. APIs. The application should be linked to Produvar online systems for the costumers of Produvar B.V. and their customers. Moreover, the application should provide some functions for authorized employees of customers companies. For instance, the activation of a workflow in Produvar B.V. system by scanning a barcode with mobile device.

**From me:**  
Russian (development) and Dutch (coordination and analytics) students worked on this project for Dutch company called Produvar. My role was to lead developers team, create design and create Android application(Kotlin). We've also developed iOS app, test API (according to provided documentation) and back-end.

This is a copy of original repository with Android application that was sent to the client.

This repository includes only a couple dozen last commits.

This is not the final version of the application - it is actual at the time of June, 25 2018. Arisen bugs will be fixed, token storage techniques will be added, but later commits won't be included in public repositories and especially in this "version for CV".

Test API ([class](https://github.com/CepBuch/ProduvarForCV/blob/master/app/src/main/java/produvar/interactionwithapi/TestAPIProvider.kt)) that was used for development purposes was replaced with test offline data ([class](https://github.com/CepBuch/ProduvarForCV/blob/master/app/src/main/java/produvar/interactionwithapi/OfflineAPIProvider.kt) under the same interface) as free hosting may not be available in several months. Company was going to use their own API. 

If you want application to use test API , you can try to change getApiProvider() returned instance in the [Factory](https://github.com/CepBuch/ProduvarForCV/blob/master/app/src/main/java/produvar/interactionwithapi/Factory.kt)  from OfflineAPIProvider() to TestAPIProvider(), but it's not guaranteed that it works at the time you looking at this repository. That is why the application uses offline test data by default.

Note: "status update" function does not work for offline test data  - it is not updated because it's supposed to update on the server side. You can see how it works in the video or try to use "test api" version.

- APK with offline imitation of API [is here](https://drive.google.com/open?id=1SB-yVzDZkOT1EHwEG4mVniDkrJBrisC-)

- APK with test API [is here](https://drive.google.com/open?id=1DQfO15Dbzd4_CTIme5ZPvmkaeiwE8Ct-)

- Instructions about testing the applications (info about users and their permissions) [is here](https://drive.google.com/open?id=1sJqOvSoiijg9G1X--rxQ0YHJeuiNExWeiPlLryhuSeQ)

- Video demonstration of basic functionality of the app [is here](https://drive.google.com/open?id=1fXv3fuU-otj_v9-a7_1KcOagiCE7S4li)
