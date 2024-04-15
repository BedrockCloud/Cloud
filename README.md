[![CircleCI](https://circleci.com/gh/BedrockCloud/Cloud.svg?style=svg)]

# Requirements
- Java 17 or higher
- Debian 10 or higher
- At least 4 vCores and 8 gigabytes ram

# Install
- Create a folder for the Cloud
- Download the [PHP-Binary](https://github.com/pmmp/PHP-Binaries/releases).
- Put the "bin" directory in the Cloud folder
- Make a start file with this content: `java -jar Cloud.jar`
- Give the start file permissions and execute the script

# Features
- Templates
- Dynamic Servers
- Static Servers
- Maintenance system
- RestAPI
- Automatically downloading server software (PocketMine, WaterdogPE) & plugins (CloudBridge)
- Notify (In-game notifications)
- Custom cloud plugins

# Note
- You don't have to download the plugins (CloudBridge-PM, CloudBridge-Proxy)

# Support
- If you find any bugs, please create a issue on GitHub
- If you need help, you can join our [Discord server](https://discord.gg/JPK5Wk2auY)

# Maven
```xml
<repositories>
	<repository>
        <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.BedrockCloud</groupId>
    <artifactId>Cloud</artifactId>
    <version>1.0.7</version>
</dependency>
```
