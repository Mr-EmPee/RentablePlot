package ml.empee.plots.controllers;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.config.PluginConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Singleton
@RequiredArgsConstructor
public class PlotController {

  private final ItemRegistry itemRegistry;
  private final PlotService plotService;
  private final PluginConfig pluginConfig;
  private final LangConfig langConfig;

  public Integer convertCoinsToSeconds(ItemStack coins) {
    if (!itemRegistry.plotCoin().isPluginItem(coins)) {
      throw new IllegalArgumentException("The item is not a plot coin");
    }

    return coins.getAmount() * pluginConfig.getCoinValue();
  }

  @Nullable
  public Plot setPlotExpireEpoch(Player sender, Long plotId, Long expireEpoch) {
    var plot = plotService.findById(plotId).orElseThrow();
    if (!plot.isClaimed() || !plot.isMember(sender.getUniqueId())) {
      Logger.log(sender, "&cOnly plot members can increase rent time");
      return null;
    }

    if (expireEpoch - System.currentTimeMillis() > TimeUnit.SECONDS.toMillis(pluginConfig.getMaxPlotRent())) {
      Logger.log(sender, langConfig.translate("plot.rent.max-rent"));
      return null;
    }

    var hoursBought = TimeUnit.MILLISECONDS.toHours(expireEpoch - plot.getExpireEpoch());
    plot = plotService.setExpireEpoch(plotId, expireEpoch);

    Logger.log(sender, langConfig.translate("plot.rent.add", hoursBought));
    return plot;
  }

  @Nullable
  public Plot claimPlot(Player sender, Long plotId, Long expireEpoch) {
    var plot = plotService.findById(plotId).orElseThrow();
    if (plot.isClaimed()) {
      Logger.log(sender, "&cThe plot is already claimed!");
      return null;
    }

    plot = plotService.claim(plotId, sender.getUniqueId());
    plot = plotService.setExpireEpoch(plotId, expireEpoch);

    Logger.log(sender, langConfig.translate("plot.claimed"));

    return plot;
  }

  public Optional<Plot> getPlot(Long plotId) {
    return plotService.findById(plotId);
  }

  public boolean isPlotCoin(@Nullable ItemStack item) {
    return itemRegistry.plotCoin().isPluginItem(item);
  }

}
