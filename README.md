
# JDA-Utilities

Some utility classes to help with making a Discord Bot with [JDA](https://github.com/DV8FromTheWorld/JDA)

## Features
 One class for everything: **JDAUtilities.class**. It includes:
- Audio system: play / search for music in vc
    - Includes effects! You can make your own by extending **AbstractEffect.class**
    - Includes sources to search from: currently only supports YouTube and Spotify
- Commands: easily create commands with our command tool
    - Supports slash and context commands!
- Component system: makes it easier for you to make and edit messages
    - Create a component by extending **SendableComponent.class**. Add your components (buttons, embeds etc.) to this class and send them as one message using `JDAUtilities.createComponent(MyClass.class).send` or `JDAUtilities.createComponent(MyClass.class).reply` depending on your event
    - Smart components: components that can directly interact with the event provided
        - Currently includes SmartButton, SmartDropdown, SmartModal and SmartReaction
        - These components have an event listener assigned to it, to subscribe to these events use `withListener`. No need to listen for them on your own event listener!
- Guild settings manager: a locally stored database used for getting and setting custom settings

## TODO
- [ ]  Add more documentation
- [ ]  Add a chat filtering system
    - filter out spam, discord invites, etc.
    - customisable with specific filtering rules

    More suggestions welcome!

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
To initialize JDA-Utilities, you will need to add the internal event listener to your JDA instance.
```java
JDA instance = JDABuilder.createDefault("TOKEN")
                .addEventListeners(JDAUtilities.getInstance().listener(), yourlistener)
                .build();
```
After that you can freely use all methods in the JDAUtilities class

## Examples
Examples are located in src/test/java

## License
[MIT](https://choosealicense.com/licenses/mit/)

