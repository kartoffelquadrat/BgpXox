# Xox

A [BoardGamePlatform](https://github.com/kartoffelquadrat/BoardGamePlatform) compatible implementation of *Tic Tac Toe*.

![preview](markdown/xox-screencapture.png)

## About

This repository contains the sources of an inofficial LobbyService demo Game.  

 > **Xox is an optional component for a LS-centrist Micro-Service Architecture. It serves as sample project for game developers.**

Key features of this implementation are:

 * Registration at the Lobby-Service on power-up / un-registration on shutdow.
 * No dedicated user management. Users are identified via the Lobby-Service, which acts as Single-Sign-On. Token verification is delegated to the LS, whenever user authentication is required.
 * Dynamic maintenance of parallel game sessions at runtime, accessible through [contractually stipulated REST endpoints](https://github.com/kartoffelquadrat/LobbyService/blob/master/markdown/game-dev.md#callbacks).
 * A self-hosted web-interface, strictly limited to game-specific functionality.

### Architecture

Xox is split into a RESTful API backend and a Web-Interface.

 * The web UI is based on an [existing open source implementation](https://github.com/angle943/tic-tac-toe).
 * The API backend is coded in Spring Boot and exposes the following resources:  
(Dashed box highlights the endpoints stipulated by the Lobby-Service)  
![api](markdown/restif.png)

## Setup

Every game coded for the LobbyService requires credentials. In the case of Xox, the credentials are pre-registered in the Lobby-Serive's DB.

 * Username: ```Xox```
 * Password: ```laaPhie*aiN0```

To test and modify this game-template on your system, you need a working lobby-service instance, running in nateive mode - that is to say the LobbyService must run as a native java process.  

 * Follow the [official LobbyService setup instructions](https://github.com/kartoffelquadrat/LobbyService/blob/master/markdown/build-deploy.md#standard-setup) (dev mode).
 * Power up this sample game:  
```mvn spring-boot:run -P dev```


### IntelliJ Run configurations:

 * JUNIT / Coverage:    
 * Mvn Test: clean test
 * Mvn Run: clean spring-boot:run
  * Spring Boot Run:

### Native Deploment

This first option 

 * Clone this repository
 * Power the service up:  
```mvn exec:spring-boot```
 * Shut the serivce down (implicitly unregisters the game at the LS). Type ```enter```.

### Docker Compose

Xox can be launched as:

 * single service (will not work without manual and configuration for an external lobby-service instance)
 * microservice compound

#### Single Service

 * first time:
   * enable ```ENTRYPOINT``` line in ```Dockerfile-ls-xox```.
   * build the image: ```docker build -t "ls-xox:Dockerfile" . -f Dockerfile-ls-xox```
   * start a container: ```docker run --name=ls-xox -p 4244:4244 -d ls-xox:Dockerfile```
 * follow up launches:  
```docker start ls-xox```

#### Microservice Compound

Add the following entries to the LobbyServices ```docker-compose.yml```:



## Open tasks

 * Update this markdown
   * add sample jsons
   * update tree (add getter for stats)
   * mention action philosophy / blackboard style
   * add screenshots
 * properly close sessions on game end
 * write a docker file / docker compose integration with LS
 * add JS logic to webui
 * document property file attributes


## Usage

*Xox* can not be used as standalone, you need the LobbyService to start new sessions and provide user authentication. *Xox* does not provide a dedicated UI for that, it can therefore only be used in combination with the [*Lobby Service Web Interface*](https://github.com/kartoffelquadrat/LobbyServiceWebInterface).

 * All access to the web-ui requires login.
   * Administrators are directly forwarded to an administration panel. That allows manipulation of user data.
   * Users are forwarded to a game lobby that allows creation and participation in game sessions.

## Contact / Pull Requests

Contact information for bug reports and pull requests:

 * Author: Maximilian Schiedermeier ![email](markdown/email.png)
 * Github: [Kartoffelquadrat](https://github.com/kartoffelquadrat)
 * Webpage: [McGill University, School of Computer Science](https://www.cs.mcgill.ca/~mschie3)
 * License: [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)

