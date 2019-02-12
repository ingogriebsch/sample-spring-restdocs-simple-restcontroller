# Spring REST Docs simple REST controller sample
[![Build Status](https://travis-ci.org/ingogriebsch/sample-spring-restdocs-simple-restcontroller.svg?branch=master)](https://travis-ci.org/ingogriebsch/sample-spring-restdocs-simple-restcontroller)
[![Codecov Status](https://codecov.io/gh/ingogriebsch/sample-spring-restdocs-simple-restcontroller/branch/master/graph/badge.svg)](https://codecov.io/gh/ingogriebsch/sample-spring-restdocs-simple-restcontroller)
[![Coveralls Status](https://coveralls.io/repos/github/ingogriebsch/sample-spring-restdocs-simple-restcontroller/badge.svg?branch=master)](https://coveralls.io/github/ingogriebsch/sample-spring-restdocs-simple-restcontroller?branch=master)
[![Codacy Status](https://api.codacy.com/project/badge/Grade/932a7d3670e64310a345db7df8db6305)](https://app.codacy.com/app/ingo.griebsch/sample-spring-restdocs-simple-restcontroller?utm_source=github.com&utm_medium=referral&utm_content=ingogriebsch/sample-spring-restdocs-simple-restcontroller&utm_campaign=Badge_Grade_Dashboard)
[![DepShield Status](https://depshield.sonatype.org/badges/ingogriebsch/sample-spring-restdocs-simple-restcontroller/depshield.svg)](https://depshield.github.io)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This sample shows you how to document a simple REST controller with Spring REST Docs.

This service is written using Spring Boot which makes it easy to get it up and running so that you can start exploring the REST API and their documentation.
After you started up the service, this page is available under <http://localhost:8080>. 
The documentation explaining the REST API implemented through this service is available under <http://localhost:8080/docs/index.html>.

If you want to test the REST API, you can simply use [Postman](https://www.getpostman.com/), [curl](https://curl.haxx.se/) or another HTTP client to execute some requests against this service. 
The necessary input to successfully execute a request and what you can expect as output is explained through the REST API documentation. 

The service uses Spring REST Docs to document the public REST API. 
The API is implemented through class [BookController](https://github.com/ingogriebsch/sample-spring-restdocs-simple-restcontroller/blob/master/src/main/java/com/github/ingogriebsch/sample/spring/restdocs/restcontroller/BookController.java).
Class [BookControllerDoc](https://github.com/ingogriebsch/sample-spring-restdocs-simple-restcontroller/blob/master/src/test/java/com/github/ingogriebsch/sample/spring/restdocs/restcontroller/BookControllerDoc.java) is using the Spring REST Docs framework to implement tests which are generating a set of snippets.
These snippets are used as includes to complete the documentation located under [src/main/asciidoc](https://github.com/ingogriebsch/sample-spring-restdocs-simple-restcontroller/tree/master/src/main/asciidoc).
The build process defined in the [pom.xml](https://github.com/ingogriebsch/sample-spring-restdocs-simple-restcontroller/blob/master/pom.xml) generates the resulting html files which are then copied into the static content service folder to be available during runtime.  

## Used frameworks
Collection of the mainly used frameworks in this project. There are more, but they are not that present inside the main use case therefore they are not listed here.

*   [Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/1.2.6.RELEASE/reference/html5/)
*   [Spring Web](https://docs.spring.io/spring/docs/4.3.12.RELEASE/spring-framework-reference/htmlsingle/#spring-web)
*   [Spring Boot](https://docs.spring.io/spring-boot/docs/1.5.10.RELEASE/reference/htmlsingle)

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).
