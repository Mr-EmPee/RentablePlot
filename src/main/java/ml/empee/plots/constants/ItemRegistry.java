package ml.empee.plots.constants;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import ml.empee.itembuilder.ItemBuilder;
import ml.empee.plots.utils.helpers.PluginItem;

/**
 * Contains all the plugin custom items
 */

public class ItemRegistry {

  private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(ItemRegistry.class);

  public static final PluginItem PLOT_SLECTOR = new PluginItem(
      plugin, "plot_selector", "1",
      ItemBuilder.from(Material.STICK).setName("&ePlot Selector")
  );

}
