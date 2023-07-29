# Distributed Content Finder

A simple overlay-based solution that allows a set of nodes to share contents (e.g., music files) among each other


## Tech Stack

Java 8+ 

Maven

SpringBoot

IDE : IntelliJ IDEA

MobaXterm


## Run Locally

Clone the project:

bash

Extract the attached zip file `OR`  `git clone https://github.com/GiRiSaHaa/distributed-content-finder.git`


Go to the project directory:

bash

`cd distributed-content-finder`


Install dependencies:

bash
`mvn clean build`


Start the Bootstrap server:

`cd bootstrap-server`

`javac BootstrapServer.java`

`java BootstrapServer`

Start the Application:

bash

`cd content-finder`

`mvn spring-boot:run` (Run in multiple bash terminals separately to create multiple nodes)