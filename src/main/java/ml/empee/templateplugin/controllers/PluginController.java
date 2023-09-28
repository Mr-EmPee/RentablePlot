package ml.empee.templateplugin.controllers;

import org.bukkit.command.CommandSender;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import lombok.RequiredArgsConstructor;
import ml.empee.templateplugin.constants.Permissions;
import ml.empee.templateplugin.utils.Logger;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Plugin related commands
 */

@Singleton
@RequiredArgsConstructor
public class PluginController implements Controller {

  @CommandMethod("template reload")
  @CommandPermission(Permissions.ADMIN)
  public void reload(CommandSender sender) {
    Logger.log(sender, "&7The plugin has been reloaded");
  }

}
