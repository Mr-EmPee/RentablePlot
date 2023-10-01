package ml.empee.plots.config;

import mr.empee.lightwire.annotations.Singleton;

/**
 * Generic plugin configs
 */

@Singleton
public class PluginConfig {
  
  /**
   * Value in seconds of a coin
   */
  public int getCoinValue() {
    return 60 * 60;
  }

}
