The project servers the purpose of notifying list of users via e-mail when new apartments are available on the www.immobilienscout24.de German portal.
This idea is to be notified immediatelly when the new apartment is available, so that you may schedule the apartment visit first. The problem in Munich is there is always at least 50 people who applied already, so the owner does not even want to answer any new e-mails.
Currently only Munich city is supported, and only apartments without agents (private) are taken into consideration.

The application is plain Java application which runs in the background and based on the specified frequency and specified parameters (price, size of the apartment etc) notifies the user(s) when sometimes new comes up.

Requirements:
- JDK7 or higher (must be in OS path)
- Maven 3+ (must be in OS path)
- Less security apps must be enabled in GMail account of the sender (https://www.google.com/settings/security/lesssecureapps)
- File named gmail.txt in the user's home folder (~user for Linux or C:\Users\UserName for Windows) with the following content:
gmail user account without @gmail.com
gmail password of the user sending e-mail
comma separated list of receivers
Example of such gmail.txt file content may be found in the root folder of the project. All you have to do is copy it to your ~user folder and adapt it.

Setup for Travel time calculation (eg. between your job and your new apartment):
Get Key for Google Maps Directions API (https://developers.google.com/maps/documentation/directions/).
Add API Key at the bottom of gmail.txt file (4th place).

Building the application:
[command line] Position in the project's root folder, where the .pom file is found, and type "mvn clean install" - this will build the application and generate the .jar artifact under /target folder.

Running the application:
[command line] Position in the /targer folder.
Type "java -jar immobilien-crawler-0.0.1-SNAPSHOT"
The application will prompt command line options to enter the filters and frequency of running (how often the check for new apartments is to be done).

Good luck with the apartment search and may you be the first to apply!
