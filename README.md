# SpigotTester
It is Plugin-Library for Spigot plugins integration testing. This library is created to 
behave simillar Junit so you can find a lot of common things. In order to use it
every Test's class from your plugin should implement interface `SpigotTest`

Example
```java 
import io.spigot.MyPlugin;
import io.github.jwdeveloper.spigot.tester.api.SpigotTest;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import io.github.jwdeveloper.spigot.tester.api.assertions.SpigotAssertion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ServerTest implements SpigotTest {
    private final Plugin plugin;
    
    //this is optional plugin will be automatically injected to constructor
    public ServerTest(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void beforeAll() {
        if (Bukkit.getPlayer("mike") == null) {
            throw new RuntimeException("Players are required for this test");
        }
    }

    @Test(name = "Give player Diamond")
    public void playerShouldRecieveItem() {
        //arrange
        var player = Bukkit.getPlayer("mike");
        var item = new ItemStack(Material.DIAMOND,1);

        //act
        player.getInventory().setItemInOffHand(item);

        //assert
        SpigotAssertion.shouldBeEqual(Material.DIAMOND, player.getInventory().getItemInMainHand().getType());
        SpigotAssertion.shouldBeEqual(1, player.getInventory().getItemInMainHand().getAmount());
    }


    @Test(ignore = true)
    public void someLegacyStuff() {

    }
}
```

To perform tests put SpigotTester and your plugin to `server/plugins` folder.
Run server, when server will be loaded SpigotTester perform all tests and generate report `server/plugins/SpigotTester/report.json`  
After that server will be automatically shut down.



For more flexibility you can modify some behaviors of SpigotTester in config `server/plugins/SpigotTester/config.yml` 

```yaml
#Closing server when tests are done
close-server-after-tests: false

#Display tests info in console
display-logs: true

#Generate report.html it's simple website that visualize tests result
open-report-in-website: true

#lists of plugins that constains SpigotTests but should be ignored
ignore-plugins:
  - "Example"
```



