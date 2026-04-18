# JDA-Utilities

Some utility classes to help with making a Discord Bot with [JDA](https://github.com/discord-jda/JDA)

## Features
 One class for everything: **JDAUtilities.class**. It includes:
- Audio API: play / search for music in vc
    - Includes sources to search from: currently only supports YouTube and Spotify
- Commands: easily create commands with our command tool
    - Supports slash and context commands!
- Component system: makes it easier for you to make and edit messages
    - Create a component by extending **SendableComponent.class**. Add your components (buttons, embeds etc.) to this class and send them as one message using `JDAUtilities.createComponent(MyClass.class).send` or `JDAUtilities.createComponent(MyClass.class).reply` depending on your event
    - Smart components: components that can directly interact with the event provided
        - Currently, includes SmartButton, SmartDropdown, SmartModal and SmartReaction
        - These components have an event listener assigned to it, to subscribe to these events use `withListener`. No need to listen for them on your own event listener!
- Database system: locally stored database for persistent data storage
    - Supports both file-based SQLite databases and remote databases
    - Use `JDAUtilities.getDatabaseConnection()` to get a connection
- Guild settings manager: a locally stored database used for getting and setting custom settings
- Chat filters: filter out spam, discord invites, etc.
    - Customisable with specific filtering rules. See `JDAUtilities.getGuildFilterManager(Guild guild)`
- Event watching: listen for specific events with conditions
    - Use `JDAUtilities.addMessageListener()` or `JDAUtilities.addGuildMessageListener()`

## TODO
- implement auto disconnect after inactivity
- playlist integration in lavaplayer and lavalink
- implement displaying song information (hang status, voice channel status)
  - More suggestions welcome!

## Installation
Latest version (replace **@TAG@** with this):
[![](https://jitpack.io/v/JustRed23/JDA-Utilities.svg)](https://jitpack.io/#JustRed23/JDA-Utilities)

Using gradle:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.JustRed23:JDA-Utilities:@TAG@'
}
```

Using maven:
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.JustRed23</groupId>
    <artifactId>JDA-Utilities</artifactId>
    <version>@TAG@</version>
</dependency>
```


    
## Usage
To initialize JDA-Utilities, first build a configuration snapshot, then apply it to your JDA or shard builder.
```java
Builder builder = JDAUtilities.getInstance()
        .withMusicManager()
            .useImplementation(yourMusicManager)
            .build();

Builder.Configuration utilities = builder.buildConfiguration();

JDA instance = utilities.configure(JDABuilder.createDefault("TOKEN"))
        .addEventListeners(yourlistener)
        .build();

// or, if you are using shards:
ShardManager shardManager = utilities.configure(DefaultShardManagerBuilder.createDefault("TOKEN"))
        .addEventListeners(yourlistener)
        .build();
```
If your `MusicManager` provides a voice dispatch interceptor, JDA Utilities will automatically apply it when the configuration snapshot is built.

After that you can freely use all methods in the JDAUtilities class

## Examples
Examples are located in src/test/java
