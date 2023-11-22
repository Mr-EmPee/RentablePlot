package ml.empee.plots.controllers.commands;

import ml.empee.plots.controllers.commands.Command;
import ml.empee.plots.services.PlotService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.config.LangConfig;
import ml.empee.plots.constants.ItemRegistry;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Plugin related commands
 */

@Singleton
@RequiredArgsConstructor
public class PluginCommand implements Command {

  private final ItemRegistry itemRegistry;
  private final LangConfig langConfig;
  private final PlotService plotService;

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "reload")
  public void reload(CommandSender sender) {
    itemRegistry.reload();
    langConfig.reload();
    plotService.reload();

    Logger.log(sender, "&7The plugin has been reloaded");
  }

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + " give-coin <target> [amount]")
  public void reload(CommandSender sender, @Argument Player target, @Argument(defaultValue = "1") Integer amount) {
    var coins = itemRegistry.plotCoin().build();
    coins.setAmount(amount);

    var result = target.getInventory().addItem(coins);
    
    if (result.isEmpty()) {
      Logger.log(sender, "&7Coins given to &e%s", target.getName());
    } else {
      Logger.log(sender, "&cFailed to give coins to &e%s", target.getName());
    }
  }

}
