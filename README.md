# SpigotTester
Library for Integration testing plugins with Spigot/Bukkit API

Config
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


Example Plugin tests:

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
