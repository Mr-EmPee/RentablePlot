package ml.empee.plots.controllers;

import org.bukkit.command.CommandSender;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.constants.Permissions;
import ml.empee.plots.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Plugin related commands
 */

@Singleton
@RequiredArgsConstructor
public class PluginController implements Controller {

  @CommandPermission(Permissions.ADMIN)
  @CommandMethod(COMMAND_PREFIX + "reload")
  public void reload(CommandSender sender) {
    Logger.log(sender, "&7The plugin has been reloaded");
  }

}
