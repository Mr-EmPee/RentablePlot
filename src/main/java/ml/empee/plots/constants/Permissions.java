package ml.empee.plots.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Store the plugin permissions
 **/

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Permissions {

  private static final String PREFIX = "demoplugin.";
  public static final String ADMIN = PREFIX + "admin";
  public static final String BYPASS_CONTAINERS = PREFIX + "bypass.containers";

}
