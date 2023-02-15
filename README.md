# SpigotTester

[Download latest version](https://github.com/jwdeveloper/SpigotTester/releases/latest)

It is Plugin-Library for Spigot plugins integration testing. This library is created to 
behave simillar Junit so you can find a lot of common things. In order to use it
every Test's class from your plugin should implement interface `SpigotTest`

Example Plugin main
``` java
public final class PluginMain extends JavaPlugin implements SpigotTesterSetup {

    //Example class that is passed to tests as parameter
    private CraftingManager craftingManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        craftingManager = new CraftingManager();
    }

   //Here you can configure tests and inject parameters
    @Override
    public void onSpigotTesterSetup(TestRunnerBuilder builder) {
        builder.injectParameter(craftingManager);
    }
}


```


Example Test
```java 
import io.spigot.MyPlugin;
import io.github.jwdeveloper.spigot.tester.api.SpigotTest;
import io.github.jwdeveloper.spigot.tester.api.annotations.Test;
import io.github.jwdeveloper.spigot.tester.api.assertions.SpigotAssertion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExampleTests extends SpigotTest {

    public ExampleTests(TestContext testContext) {
        super(testContext);
    }


    @Test(name = "crafting permission test")
    public void shouldUseCrafting() {
        //Arrange
        Player player = addPlayer();
        CraftingManager craftingManager = getParameter(CraftingManager.class);
        
        PermissionAttachment attachment = player.addAttachment(getPlugin());
        attachment.setPermission("crating", true);

        //Act
        boolean result = craftingManager.canPlayerUseCrating(player);

        //Assert
        assertion(result).shouldBeTrue();
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



