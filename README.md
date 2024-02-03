Wow coooooo 😮😎

`
1.17 - 1.20.4
`

[![](https://jitpack.io/v/Cyrzuu/SuperSign.svg)](https://jitpack.io/#Cyrzuu/SuperSign)

**Maven:**
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Cyrzuu</groupId>
    <artifactId>SuperSign</artifactId>
    <version>1.1</version>
</dependency>
```

**Gradle:**
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Cyrzuu:SuperSign:1.1'
}
```
\
\
Register
```java
public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SuperSign.registerSuperSign(this);

        //code
    }

} 
```
\
\
Example
```java
public final class Class extends JavaPlugin {

    @Override
    public void onEnable() {
        SuperSign.registerSuperSign(this);

        getCommand("command").setExecutor((sender, command, label, args) -> {
            if(!(sender instanceof Player player)) return false;
            SuperSign.build(player)
                    .onAccept(lines -> {
                        String username = lines[0].replace(" ", "");
                        Player target = Bukkit.getPlayer(username);
                        if(target == null || Objects.equals(player.getUniqueId(), target.getUniqueId())) {
                            player.sendMessage("User is offline");
                            return;
                        }

                        target.sendMessage("%s ping you!".formatted(player.getName()));
                        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f);
                    })
                    .setLines(null, "^".repeat(16), "Set username")
                    .setColorSign(ColorSign.MANGROVE)
                    .run();

            return false;
        });
    }

}

```
