package ml.empee.templateplugin.config;

import lombok.Getter;
import mr.empee.lightwire.annotations.Instance;
import mr.empee.lightwire.annotations.Singleton;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Handle messages
 */

@Singleton
public class LangConfig extends AbstractConfig {

  private static final String HEX_PREFIX = "&#";
  private static final Pattern HEX_COLOR = Pattern.compile(HEX_PREFIX + "[a-zA-z0-9]{6}");

  @Instance @Getter
  private static LangConfig instance;

  public LangConfig(JavaPlugin plugin) {
    super(plugin, "messages.yml", 1);
  }

  @Override
  protected void update(int from) {
  }

  /**
   * Translate a key to a message
   */
  public String translate(String key, Object... placeholders) {
    var translation = config.getString(key);
    if (translation == null) {
      throw new IllegalArgumentException("Missing translation key " + key);
    }

    return String.format(ChatColor.translateAlternateColorCodes(
        '&', translateHexCodes(translation)
    ), placeholders);
  }

  public List<String> translateBlock(String key, Object... placeholders) {
    return List.of(translate(key, placeholders).split("\n"));
  }

  private static String translateHexCodes(String input) {
    Matcher matcher = HEX_COLOR.matcher(input);
    while (matcher.find()) {
      String group = matcher.group().substring(HEX_PREFIX.length());
      StringBuilder hex = new StringBuilder("&x");
      for (char code : group.toLowerCase().toCharArray()) {
        hex.append("&").append(code);
      }

      input = input.replace(HEX_PREFIX + group, hex.toString());
    }

    return input;
  }

}