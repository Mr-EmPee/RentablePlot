package ml.empee.plots.config;

import ml.empee.plots.model.entities.PlotType;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Generic plugin configs
 */

@Singleton
public class PluginConfig extends AbstractConfig {

  public PluginConfig(JavaPlugin plugin) {
    super(plugin, "config.yml", 1);
  }

  /**
   * Value in seconds of a coin
   */
  public int getCoinValue() {
    return config.getInt("coin.seconds", 3600);
  }

  public int getMaxPlotRent() {
    return config.getInt("max-rent.seconds", 864000);
  }

  public List<PlotType> getPlotTypes() {
    var result = new ArrayList<PlotType>();
    var types = config.getConfigurationSection("plots");
    for (var type : types.getKeys(false)) {
      result.add(parsePlotType(types.getConfigurationSection(type)));
    }

    return result;
  }

  private PlotType parsePlotType(ConfigurationSection config) {
    return PlotType.builder()
        .id(config.getName())
        .maxMembers(config.getInt("max-members", 5))
        .title(config.getString("title", ""))
        .containersPerPlayer(config.getInt("max-per-player-containers", 5))
        .build();
  }

  @Override
  protected void update(int from) {

  }
}
