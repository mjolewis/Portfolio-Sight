# Stock Portfolio Dashboard

# Overview
Stock Portfolio Dashboard provides tools for investors to quickly analyze an investment portfolio to help inform 
potential investment decisions (e.g., asset allocation based on some common investment patterns such as sector based 
allocation, market capitalization based allocation, and portfolio tilt).

The product is available at http://portfoliosight.com

To avoid the inherent security issues in managing brokerage credentials, this product requires the user to upload a CSV 
file that contains investment information. These files can typically be exported directly from a brokerage. The 
template below shows the required fields of a well-formed CSV. The user can upload a master CSV file containing all 
their accounts from all their brokerages or, they can upload individual CSV files on a per brokerage basis.

|  Account  | Symbol | Quantity |
| --------- | ------ | -------- |
| 1234-5678 | GS     | 1000     |
| 1234-5678 | JPM    | 500      |
| q34268    | TSLA   | 2000     |


# Features
Stock Portfolio Dashboard features:

Stock Breakdown
* Stock Breakdown uses IEX Cloud's `latestPrice` attribute to calculate the total market value for each symbol in the 
  portfolio and then builds market value based allocation visualizations. The `latestPrice` refers to the latest 
  relevant price of the security which is derived from multiple sources. IEX Cloud first looks for an IEX based real 
  time price. If an IEX real time price is older than 15 minutes, the 15-minute delayed price is used. If a 15-minute 
  delayed price is not available, then the current day close price is used. Otherwise, the last available closing price 
  will be used.
  
Sector Breakdown
* Sector breakdown uses IEX Cloud's `sector` attribute to build sector allocation visualizations. The `sector` refers 
  to the sector a security belongs to.
  
Market Cap Breakdown
* Market cap breakdown uses IEX Cloud's `marketCap` attribute to build market cap allocation visualizations for each 
  company in the portfolio. The `marketCap` of a security is calculated as shares outstanding * previous day close.

Each feature `GETS` data from IEX Cloud's `/stock` endpoint using a batch call. Batch calls can return data on up to 
one hundred symbols per request, significantly reducing network traffic.

# Frameworks, Libraries, and Tools
Spring Cloud:
* Spring Data JPA provides abstraction over the Data Access Layer using Java Persistence API and Hibernate as the ORM.
* Spring Security provides a powerful and highly customizable framework authentication, authorization, and protection 
  against common exploits (e.g. Cross-Site Request Forgery).
* Spring Web module provides basic web-oriented integration features such as multipart file upload functionality and 
  the initialization of the IoC container using Servlet listeners and a web-oriented application context. It also 
  contains an HTTP client, and the web-related parts of Spring’s remoting support.

Thymeleaf
* Thymeleaf is a Java template engine for processing and creating HTML, JavaScript, and CSS that is integrated with
  Spring MVC to serve the View Layer.

Jackson-Databind
* Jackson-Databind is a convenient data-binding (to/from POJOs) tool. This product uses Jackson to deserialize JSON 
  data from IEX Cloud and construct POJOs used throughout the product. For example, this application uses market data 
  from IEX Cloud to construct `Quote` objects representing the `latestPrice` (and other attributes) of a security.

IEX Cloud
* IEX Cloud is a financial data infrastructure platform that connects this application to financial data creators. This 
application uses the IEX Cloud batch calls for its real-time and historical market data needs.
  
* This application is deployed on AWS and is available at http://portfoliosight.com. However, developers interested in 
  compiling the program need to establish an IEX Cloud account to make API calls. IEX Cloud offers both free and paid 
  accounts. After establishing an account, navigate to the `console/tokens` endpoint and copy and paste the token into 
  a `secrets.properties` file. The key-value pair should be `IexCloudApiKey=YOUR_IEX_CLOUD_TOKEN`

Maven
* Maven is a build automation tool that is used with Jenkins to facilitate the automated build process. Additionally,
  Maven manages the products dependencies through a `pom.xml` file.
  
Jenkins
* Jenkins is an open source automation server that orchestrates the entire software delivery pipeline for this product.
  When a developer opens a PR, Jenkins will automatically integrate, deliver, and deploy successful builds to the 
  products AWS Elastic Beanstalk instance.

AWS
* AWS Elastic Beanstalk is an easy-to-use service for deploying and scaling web applications. The service handles all
  the deployment needs for this application including provisioning, load balancing, auto-scaling, and health monitoring.
* AWS RDS is a distributed relational database service designed to simplify the setup, operation, and scaling of a
  relational database. Stock Portfolio Dashboard uses a MySQL database engine.

# How to compile the project

We use Apache Maven to compile and run this project.

You need to install Apache Maven (https://maven.apache.org/) on your system.

Type on the command line:

```
mvn clean compile
```

# How to create a binary runnable package

```
mvn -B -Dmaven.clean.skip=true -DskipTests package
```

# How to deploy

This application runs on AWS Elastic Beanstalk. New versions can be uploaded to Elastic Beanstalk environment and 
deployed into production.

# Run unit tests

This application uses the surefire plugin to execute unit tests during the `test` phase of the build lifecycle.

```
mvn clean compile test
```

# Run static analysis tests

Spotbugs is an open-source static code analyzer used to detect possible bugs in this product. It automatically scans 
the code to look for potential bug patterns and makes the build fail if any bugs are found. Spotbugs will also provide
the developer with a helpful report that contains the errors and potential solutions.

```
mvn spotbugs:check
```

# Run integration tests

This application uses the failsafe plugin, which has been configured to run integration tests during the `verify` phase 
of the build lifecycle. 
```
mvn -DskipSurefire=true verify
```

# CI/CD

This application uses Jenkins to orchestrate the entire software delivery pipeline. As a result, we recommend that 
developers avoid the manual steps outlined above and simply open a PR in the project's repo. Jenkins will automatically 
test, build, and deploy the application. The development team will be notified of any test failures and, the deployment 
will fail (allowing the developer to fix the build before a new version is deployed into production).