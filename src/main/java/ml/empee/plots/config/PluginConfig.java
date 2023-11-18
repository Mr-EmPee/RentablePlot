package ml.empee.plots.config;

import mr.empee.lightwire.annotations.Singleton;

import java.util.concurrent.TimeUnit;

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

  public long getMaxPlotRent() { return TimeUnit.DAYS.toSeconds(10); }

}
