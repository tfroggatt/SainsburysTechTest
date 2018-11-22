[![Build Status](https://travis-ci.com/tfroggatt/SainsburysTechTest.svg?branch=master)](https://travis-ci.com/tfroggatt/SainsburysTechTest)
[![codecov.io](https://codecov.io/gh/tfroggatt/SainsburysTechTest/branch/master/graphs/badge.svg)](https://codecov.io/gh/tfroggatt/SainsburysTechTest)

# SainsburysTechTest

This application will scrape web pages (if non given as command line arguments then a default URL) and return Product information in the form of JSON printed to the console.

### Installing

This application will require a java 8 jdk and maven installed.

To get this application, simply clone the repository and from the root directory run an the command **mvn install**. This will compile and run the unit tests.

To create an executable JAR file for the application run the command **mvn clean compile assembly:single**. 

### Compiling and running the application

To run the application the command **mvn exec:java** can be used after the install command. This will then run the application with the default URL.

To run it with a different URL add the **-Dexec.args="<url>"** to the end of the command above.

If the code was compiled into the executable JAR, you can run it by going into the target folder and running the command **java -jar <jarName>**. (A compiled jar
will be provided with the name **WebScraperApp.jar** in the root directory of the repository.
