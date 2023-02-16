# SpigotTester
[![Release](https://jitpack.io/v/jwdeveloper/SpigotTester.svg)](https://jitpack.io/#jwdeveloper/SpigotTester)


[Download latest version](https://github.com/jwdeveloper/SpigotTester/releases/latest)

This is a tool designed for adding integration tests to spigot plugins. This library is created to 
behave simillar to Junit so you will find a lot similarities. In order to use it
every class from your plugin which contains a test should extend the abstract class `SpigotTest`

# Examples 

## Example Plugin main 
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


## Example Test
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

To perform tests, put the SpigotTester plugin and your plugin in the `server/plugins` folder.
Then, run the server. Once the server has finished starting up, SpigotTester will perform all tests and generate a report in the location `server/plugins/SpigotTester/report.json`.


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



