[![Build Status](https://travis-ci.com/tfroggatt/SainsburysTechTest.svg?branch=master)](https://travis-ci.com/tfroggatt/SainsburysTechTest)
[![codecov.io](https://codecov.io/gh/tfroggatt/SainsburysTechTest/branch/master/graphs/badge.svg)](https://codecov.io/gh/tfroggatt/SainsburysTechTest)

# SainsburysTechTest
This application will scrape web pages (if non given as command line arguments then a default URL) and return Product information in the form of JSON printed to the console.

###Installing
This application will require a java 8 jdk and maven installed.
To get this application, simply clone the repository and from the root directory run an the command **mvn install**

###Running the application
To run the application the command **mvn exec:java** can be used after the install command. This will then run the application with the default URL.
To run it with a different URL add the **-Dexec.args="<space seperated list>"** to the end of the command above.
