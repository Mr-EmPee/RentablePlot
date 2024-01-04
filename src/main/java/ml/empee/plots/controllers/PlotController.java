package ml.empee.plots.controllers;

import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.config.PluginConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
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

  public boolean canBuild(Player sender, Long plotId) {
    var plot = plotService.findById(plotId).orElseThrow();
    return plot.isMember(sender.getUniqueId());
  }

  public Plot deletePlot(Player sender, Long plotId) {
    var plot = plotService.delete(plotId);
    Logger.log(sender, "The plot has been deleted");

    return plot;
  }

  @Nullable
  public Plot addContainer(Player sender, Long plotId, Location container) {
    var plot = plotService.findById(plotId).orElseThrow();
    var plotType = plotService.findPlotType(plot.getPlotType()).orElseThrow();

    if (!sender.hasPermission(Permissions.BYPASS_CONTAINERS)) {
      if (plot.getTotalContainers() > plotType.getMaxContainers()) {
        Logger.log(sender, langConfig.translate("plot.containers.plot-limit"));
        return null;
      }

      if (plot.getContainers(sender.getUniqueId()).size() > plotType.getContainersPerPlayer()) {
        Logger.log(sender, langConfig.translate("plot.containers.player-limit"));
        return null;
      }
    }

    return plotService.addContainer(plotId, sender.getUniqueId(), container);
  }

  public Plot removeContainer(Player sender, Long plotId, Location container) {
    return plotService.removeContainer(plotId, container);
  }

  public Plot addMember(Player sender, Long plotId, OfflinePlayer target) {
    if (target.getUniqueId().equals(sender.getUniqueId())) {
      Logger.log(sender, langConfig.translate("cmd.self-execution"));
      return null;
    }

    var plot = plotService.findById(plotId).orElseThrow();

    boolean isOwner = sender.getUniqueId().equals(plot.getOwner());
    if (!isOwner) {
      Logger.log(sender, langConfig.translate("plot.not-owner"));
      return null;
    }

    boolean isMember= plot.isMember(target.getUniqueId());
    if (isMember) {
      Logger.log(sender, langConfig.translate("plot.already-member"));
      return null;
    }

    var plotType = plotService.findPlotType(plot.getPlotType()).orElseThrow();
    if (plot.getMembers().size() >= plotType.getMaxMembers()) {
      Logger.log(sender, langConfig.translate("plot.max-members"));
      return null;
    }

    plot = plotService.addMember(plot.getId(), target.getUniqueId());

    var owner = Bukkit.getOfflinePlayer(plot.getOwner());
    for (var member : plot.getMembers()) {
      var player = Bukkit.getOfflinePlayer(member);
      if (player.isOnline()) {
        Logger.log(player.getPlayer(), langConfig.translate("plot.member-added", target.getName(), owner.getName()));
      }
    }

    return plot;
  }

  public Plot removeMember(Player sender, Long plotId, OfflinePlayer target) {
    if (target.getUniqueId().equals(sender.getUniqueId())) {
      Logger.log(sender, langConfig.translate("cmd.self-execution"));
      return null;
    }

    var plot = plotService.findById(plotId).orElseThrow();

    boolean isOwner = sender.getUniqueId().equals(plot.getOwner());
    if (!isOwner) {
      Logger.log(sender, langConfig.translate("plot.not-owner"));
      return null;
    }

    boolean isMember = plot.isMember(target.getUniqueId());
    if (!isMember) {
      Logger.log(sender, langConfig.translate("plot.not-member"));
      return null;
    }

    plot = plotService.removeMember(plot.getId(), target.getUniqueId());

    var owner = Bukkit.getOfflinePlayer(plot.getOwner());
    for (var member : plot.getMembers()) {
      var player = Bukkit.getOfflinePlayer(member);
      if (player.isOnline()) {
        Logger.log(player.getPlayer(), langConfig.translate("plot.member-removed", target.getName(), owner.getName()));
      }
    }

    return plot;
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

  public Optional<Plot> findByLocation(Location location) {
    return plotService.findByLocation(location);
  }

  public boolean isPlotCoin(@Nullable ItemStack item) {
    return itemRegistry.plotCoin().isPluginItem(item);
  }

}
