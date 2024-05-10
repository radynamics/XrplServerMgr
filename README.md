# XRPL Server Manager
XRPL Server Manager is a graphical user interface toolset for XRPL servers. With its various tools it facilitates installing and maintaining rippled/xahaud nodes and validators. Binaries are available for Windows, MacOS and Linux (Java jar).

![Welcome Screen](https://www.radynamics.com/xrplservermgr/github_welcome.png)
![Logs](https://www.radynamics.com/xrplservermgr/github_logs.png)

### Requirements
1. Windows, MacOS or Linux Computer
2. JRE 15 or later (Java Runtime Environment)
3. SSH access to a Linux server (opt. running [rippled](https://xrpl.org/docs/infrastructure/installation/))

### Architecture
![Logs](https://www.radynamics.com/xrplservermgr/github_architecture.png)

### Installation
1. This project is known to run on Windows, macOS and Linux. This README lists out steps to run the software on Windows using free [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download).
2. Clone this repo
3. Open the project using IntelliJ. IntelliJ processes src/build.gradle and downloads all necessary dependencies.
4. Create a new "Run/Debug Configuration" with type "Application" with the following parameters:
    * Module: java 15 or later
    * -cp: XrplServerMgr.main
    * Main class: com.radynamics.xrplservermgr.Main
5. Run

