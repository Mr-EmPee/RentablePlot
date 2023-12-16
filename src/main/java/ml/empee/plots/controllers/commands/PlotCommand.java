package ml.empee.plots.controllers.commands;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.config.PluginConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.services.PlotService;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Controller for plots
 */

@Singleton
@RequiredArgsConstructor
public class PlotCommand implements Command {

  private final ItemRegistry itemRegistry;
  private final PlotService plotService;
  private final LangConfig langConfig;

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "selector")
  public void giveSelector(Player sender) {
    sender.getInventory().addItem(itemRegistry.plotSelector().build());
    Logger.log(sender, "&aPlot selector given");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "create")
  public void createPlot(Player sender) {
    var selection = plotService.getSelection(sender.getUniqueId()).orElse(null);
    if (selection == null || !selection.isValid()) {
      Logger.log(sender, "&cYou need to select a region first");
      return;
    }

    plotService.create(sender.getLocation(), selection.getStart(), selection.getEnd());
    Logger.log(sender, "&aPlot created");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "move-hologram <x> <y> <z>")
  public void moveHologramPlot(Player sender, @Argument Double x, @Argument Double y, @Argument Double z) {
    var plot = plotService.findByLocation(sender.getLocation()).orElse(null);
    if (plot == null) {
      Logger.log(sender, "&cYou need to be in a plot");
      return;
    }

    plotService.moveHologram(plot.getId(), new Location(sender.getWorld(), x, y, z));
    Logger.log(sender, "&aHologram moved");
  }

  @CommandMethod(COMMAND_PREFIX + "members add <target>")
  public void addMember(Player sender, OfflinePlayer target) {
    if (target.getUniqueId().equals(sender.getUniqueId())) {
      Logger.log(sender, langConfig.translate("cmd.self-execution"));
      return;
    }

    var plot = plotService.findByLocation(sender.getLocation()).orElse(null);
    if (plot == null) {
      Logger.log(sender, langConfig.translate("plot.not-inside"));
      return;
    }

    boolean isOwner = sender.getUniqueId().equals(plot.getOwner());
    if (!isOwner) {
      Logger.log(sender, langConfig.translate("plot.not-owner"));
      return;
    }

    boolean isMember= plot.isMember(target.getUniqueId());
    if (isMember) {
      Logger.log(sender, langConfig.translate("plot.already-member"));
      return;
    }

    var plotType = plotService.findPlotType(plot.getPlotType());
    if (plot.getMembers().size() >= plotType.getMaxMembers()) {
      Logger.log(sender, langConfig.translate("plot.max-members"));
      return;
    }

    plotService.addMember(plot.getId(), target.getUniqueId());
  }

  @CommandMethod(COMMAND_PREFIX + "members remove <target>")
  public void removeMember(Player sender, OfflinePlayer target) {
    if (target.getUniqueId().equals(sender.getUniqueId())) {
      Logger.log(sender, langConfig.translate("cmd.self-execution"));
      return;
    }

    var plot = plotService.findByLocation(sender.getLocation()).orElse(null);
    if (plot == null) {
      Logger.log(sender, langConfig.translate("plot.not-inside"));
      return;
    }

    boolean isOwner = sender.getUniqueId().equals(plot.getOwner());
    if (!isOwner) {
      Logger.log(sender, langConfig.translate("plot.not-owner"));
      return;
    }

    boolean isMember = plot.isMember(target.getUniqueId());
    if (!isMember) {
      Logger.log(sender, langConfig.translate("plot.not-member"));
      return;
    }

    plotService.removeMember(plot.getId(), target.getUniqueId());
  }

  //TODO DELETE

}
