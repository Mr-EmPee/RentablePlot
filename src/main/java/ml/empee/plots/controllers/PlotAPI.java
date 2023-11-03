package ml.empee.plots.controllers;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.PluginConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.services.PlotService;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
@RequiredArgsConstructor
public class PlotAPI {

  private final ItemRegistry itemRegistry;
  private final PlotService plotService;
  private final PluginConfig pluginConfig;

  public Integer convertCoinsToSeconds(ItemStack coins) {
    if (!itemRegistry.plotCoin().isPluginItem(coins)) {
      throw new IllegalArgumentException("The item is not a plot coin");
    }

    return coins.getAmount() * pluginConfig.getCoinValue();
  }

  public Plot setRent(Long plotId, UUID owner, long expireEpoch) {
    var plot = plotService.findById(plotId).orElseThrow();
    if (plot.isClaimed() && !plot.isMember(owner)) {
      throw new IllegalArgumentException("The plot " + plotId + " is already claimed and " + owner + " is not a member");
    }

    return plotService.setRent(plotId, owner, expireEpoch);
  }

  public Optional<Plot> getPlot(Long plotId) {
    return plotService.findById(plotId);
  }

  public boolean isPlotCoin(@Nullable ItemStack item) {
    return itemRegistry.plotCoin().isPluginItem(item);
  }

}
