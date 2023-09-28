package ml.empee.plots;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.val;
import ml.empee.plots.config.CommandsConfig;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.config.client.DbClient;
import ml.empee.plots.controllers.Controller;
import ml.empee.plots.utils.Logger;
import ml.empee.simplemenu.SimpleMenu;
import mr.empee.lightwire.Lightwire;

/**
 * Boot class of this plugin.
 **/

public final class RentablePlots extends JavaPlugin {

  private final Lightwire iocContainer = new Lightwire();
  private final SimpleMenu simpleMenu = new SimpleMenu();

  /**
   * Called when enabling the plugin
   */
  public void onEnable() {
    simpleMenu.init(this);

    iocContainer.addBean(this);
    iocContainer.loadBeans(getClass().getPackage());

    loadPrefix();
    registerListeners();
    registerCommands();
  }

  private void loadPrefix() {
    val langConfig = iocContainer.getBean(LangConfig.class);
    Logger.setPrefix(langConfig.translate("prefix"));
  }

  private void registerCommands() {
    var commandManager = iocContainer.getBean(CommandsConfig.class);
    iocContainer.getAllBeans(Controller.class).forEach(
        c -> commandManager.register(c)
    );
  }

  private void registerListeners() {
    iocContainer.getAllBeans(Listener.class).forEach(
        l -> getServer().getPluginManager().registerEvents(l, this)
    );
  }

  public void onDisable() {
    var dbClient = iocContainer.getBean(DbClient.class);
    if (dbClient != null) {
      dbClient.closeConnections();
    }

    simpleMenu.unregister(this);
  }
}
