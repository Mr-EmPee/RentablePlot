package ml.empee.plots.constants;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.cryptomorin.xseries.XMaterial;

import lombok.RequiredArgsConstructor;
import ml.empee.itembuilder.ItemBuilder;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.utils.helpers.PluginItem;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Contains all the plugin custom items
 */

@Singleton
@RequiredArgsConstructor
public class ItemRegistry {

  private final JavaPlugin plugin;
  private final LangConfig langConfig;

  private PluginItem plotSelector;
  private PluginItem plotCoin;

  public void reload() {
    plotCoin = null;
  }

  public PluginItem plotSelector() {
    if (plotSelector == null) {
      plotSelector = new PluginItem(
          plugin, "plot_selector", "1",
          ItemBuilder.from(XMaterial.STICK.parseItem()).setName("&ePlot Selector"));
    }

    return plotSelector;
  }

  public PluginItem plotCoin() {
    if (plotCoin == null) {
      plotCoin = new PluginItem(
          plugin, "plot_coin", "1",
          ItemBuilder.from(XMaterial.SUNFLOWER.parseItem())
              .setName(langConfig.translate("coin.name"))
              .setLore(langConfig.translate("coin.lore"))
      );
    }

    return plotCoin;
  }

}
