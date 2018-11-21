# startups_evaluation_innmind_bct4mas
MAS (Jade) + BCT (Hyperledger)

## Getting Started ##
All the files that use the Java SDK for Hyperledger Fabric 1.2 are stored in the `fabric` package
The SDK acts on behave of a particular User which is defined by the embedding application through the implementation (in Our Case we Called the class `UserImplementation`)
 of the SDK's `User` interface.

### Definitions & Abbreviations ###
* MAS - Multi-Agent System
* BCT - BlockChain Technology
* HF - Hyperledger Fabric

### Main Technologies Involved ###
The main technologies involved are 2, one for the MAS part and the other for the BCT part as listed:
* MAS - [Jade, JAVA Agent DEvelopment Framework](http://jade.tilab.com/)
* BCT - [IBM Hyperledger Fabric](https://www.hyperledger.org/projects/fabric) 

### System Requirements ###
* Any linux distro (I used [ubuntu 18.04 LTS](https://www.ubuntu.com/download/desktop))

### Dependencies ###
To build this project, the following dependencies must be met
 * [JDK 1.8 or above](https://thishosting.rocks/install-java-ubuntu/)
 * [Apache Maven 3.5.0](https://maven.apache.org/)
 * [Go version 1.10.x is required](https://golang.org/doc/install).
 * [Docker version 17.06.2-ce or greater is required](https://docs.docker.com/install/linux/docker-ce/ubuntu/#install-docker-ce).
 * [jq](https://stedolan.github.io/jq/)
 * All Java dependencies are in pom.xml (Maven)
 * All Go dependencies are in vendor folder as [chaincode development's best practices](https://hyperledger-fabric.readthedocs.io/en/v1.1.0-alpha/chaincode4ade.html#managing-external-dependencies-for-chaincode-written-in-go)

### List of Config Files ###
* config.properties - Properties of Hyperledger Fabric Java SDK (further understanding)
* configJade.json - configuration file for what concern the MAS part of the project
* configHF.json - configuration file for what conern the BCT part of the project
* pom.xml - contains information about the project and configuration details used by Maven to build the project (project dependencies)
* JavaGoogleStandardFormat.xml - Coding Standard syntax for the editor (Eclipse, IntelliJIDEA)

### List of Directories ###
* chaincode - Chaincode (Golang) Code
* src - Java Source Code
* setup-fabric-network - fabric network config files
* bc-mas-app - bash files with cli commands
* fabric-tools - Hyperledger Fabric's CLI factory tools
* resources - multimedia files (images,...) used by the java application
* logs - files of log output
* docs - Miscellaneous documentation files

## BCT4MAS environment
The network is composed by one Fabric orderer and one organization (Org1) with 3 peers (peer0, peer1, peer2) , one fabric-ca service.

## GO Lang chaincode
Go lang chaincode dependencies must be contained in vendor folder.
 For an explanation of this see [Vendor folder explanation](https://blog.gopheracademy.com/advent-2015/vendor-folder/)

## Steps

Follow these steps to setup and run this code pattern. 

1. [Setup the Blockchain Network](#1-setup-the-blockchain-network)
2. [Build BCT4MAS](#2-build-BCT4MAS)

### 1. Setup the Blockchain Network

[Clone this repo](https://github.com/PhDavide/startups_evaluation_innmind_bct4mas.git) using the following command.

```
$ git clone https://github.com/PhDavide/startups_evaluation_innmind_bct4mas.git
```

The automated scripts to build the network are provided under `setup-fabric-network` directory. The `setup-fabric-network/docker-compose.yml` file defines the blockchain network topology. This pattern provisions a Hyperledger Fabric 1.1 network consisting of one organization, maintaining three peer node, one certificate authorities for each organization and a solo ordering service. Need to run the script as follows to build the network.

> **Note:** Please clean up the old docker images (if any) from your environment otherwise you may get errors while setting up network.

   ```
   cd bc-mas-app
   ./startFabric.sh
   ```

To stop the running network, run the following script.

   ```
   cd bc-mas-app
   ./stopFabric.sh
   ```

To delete the network completely, following script need to execute.

   ```
   cd bc-mas-app
   ./destroyFabric.sh
   ```

### 2. Build BCT4MAS

The previous step creates all required docker images with the appropriate configuration.

**Java Client**
* The java client sources are present in the folder `src` of the repo.
* Check your environment before executing the next step. Make sure, you are able to run `mvn` commands properly.
   > If `mvn` commands fails, please refer to [Pre-requisites](#pre-requisites) to install maven.


To work with the deployed network using Hyperledger Fabric SDK java 1.2.0, perform the following steps.

* Open a command terminal and navigate to `tourism_trust_bct4mas`, the main repo directory. Run the command `mvn install`.

   ```
   cd startups_evaluation_innmind_bct4mas
   mvn install
   ```

* A jar file `tourism_trust_bct4mas-1.0.jar` is built and can be found under the `target` folder. 
**IMPORTANT:** Don't run the .jar outside of  `target` directory, this is required as the java code can access required artifacts during execution.

   ```
   cd target
   ```
   
* Run this built jar.

   ```
   java -jar startups_evaluation_innmind_bct4mas-1.0.jar
   ```
* Enjoy. ;)

## Coding Standard Guidelines ##

* Writing tests
* Code review
* Other guidelines

## Who do I talk to? ###

* Davide Calvaresi - davide.calvaresi@hevs.ch 
* Valerio Mattioli - valerio.mattioli@students.hevs.ch


