# SpigotTester
Library for Integration testing plugins with Spigot/Bukkit API
* Tests must be located in the Plugin package 

Example Initialization 

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        SpigotTester.create(this)
                //will parameter will be passed to test constructor
                .withParameter(this, JavaPlugin.class)
                .configure(options ->
                {
                    //tests will be running in order
                    options.setRunInParallel(false);

                    //tests will be running in parallel
                    options.setRunInParallel(true);

                    //output report file path
                    options.setReportPath("...");
                })
                .onSuccess(report ->
                {
                    Bukkit.getLogger().info("Tests passed");
                })
                .onFail(report -> {
                    Bukkit.getLogger().info("Tests not passed");
                })
                .onException(e ->
                {
                    e.printStackTrace();
                })
                .run();
    }
}
```
Example Test:

```java 
import jw.spigot.MyPlugin;
import jw.spigot.tester.api.SpigotTest;
import jw.spigot.tester.api.annotations.Test;
import jw.spigot.tester.api.assertions.SpigotAssertion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ServerTest implements SpigotTest {
    private final MyPlugin plugin;

    public ServerTest(MyPlugin plugin) {
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
    public void someThinkElse() {

    }
}
```
