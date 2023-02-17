# SpigotTester
[![Release](https://jitpack.io/v/jwdeveloper/SpigotTester.svg)](https://jitpack.io/#jwdeveloper/SpigotTester)


[Download latest version](https://github.com/jwdeveloper/SpigotTester/releases/latest)

This is a tool designed for adding integration tests to spigot plugins. This library is created to 
behave simillar to Junit so you will find a lot similarities. In order to use it
every class from your plugin which contains a test should extend the abstract class `SpigotTest`

# Examples 

## Example Plugin main 
``` java
public final class PluginMain extends JavaPlugin implements PluginTestsSetup {

    //Example class that is passed to tests as parameter
    private CraftingManager craftingManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        craftingManager = new CraftingManager();
    }

   //Here you can configure tests and inject parameters
    @Override
    public void onTestsSetup(TestsBuilder builder) {
        builder.addParameter(craftingManager);
    }
}


```


## Example Test
```java 
public class ExampleTests extends PluginTest {

    @Test(name = "crafting permission test")
    public void shouldUseCrafting() {
        //Arrange
        Player player = addPlayer("mike");
        CraftingManager craftingManager = getParameter(CraftingManager.class);
        PermissionAttachment attachment = player.addAttachment(getPlugin());
        attachment.setPermission("crating", true);

        //Act
        boolean result = craftingManager.canPlayerUseCrating(player);

        //Assert
        assertThat(result).shouldBeTrue();

        assertThatPlayer(player)
                .hasName("mike")
                .hasPermission("crating");
    }

    @Test(name = "teleport only player with op")
    public void shouldBeTeleported() {
        //Arrange
        Player playerJoe = addPlayer("joe");
        Player playerMike = addPlayer("mike");

        //Act
        invokeCommand(playerJoe, "teleport " + playerJoe.getName() + " 1 3 3");

        playerMike.setOp(true);
        invokeCommand(playerMike, "teleport " + playerMike.getName() + " 1 2 3");

        //Assert 
        assertThatEvent(PlayerTeleportEvent.class)
                .wasInvoked(Times.once()) //since only one player has OP event will be triggered once
                .validate();

        assertThatCommand("teleport")
                .wasInvoked(Times.once())
                .byPlayer(playerJoe)
                .validate();

        assertThatCommand("teleport")
                .wasInvoked(Times.once())
                .byPlayer(playerMike)
                .validate();
    }
}
```

### Report output
 `server/plugins/SpigotTester/report.json`
```json
{
  "isPassed": true,
  "reportId": "a7e47b6d-4165-4692-a286-b07404d37401",
  "createdAt": "2023-02-17 15:11:35.4724659+01",
  "serverVersion": "3638-Spigot-d90018e-7dcb59b (MC: 1.19.3)",
  "spigotVersion": "1.19.3-R0.1-SNAPSHOT",
  "spigotTesterVersion": "1.0.0-Release",
  "plugins": [
    {
      "isPassed": true,
      "pluginVersion": "1.0.0",
      "pluginName": "ExamplePluginToTest",
      "classResults": [
        {
          "className": "ExampleTests",
          "classPackage": "io.github.jwdeveloper.spigot.exampleplugintotest",
          "isIgnored": false,
          "isPassed": true,
          "testMethods": [
            {
              "name": "teleport only player with op",
              "isPassed": true,
              "executionTime": 26.0652,
              "isIgnored": false,
              "errorMessage": "",
              "stackTrace": ""
            },
            {
              "name": "crafting permission test",
              "isPassed": true,
              "executionTime": 18.7894,
              "isIgnored": false,
              "errorMessage": "",
              "stackTrace": ""
            }
          ]
        }
      ]
    }
  ]
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



