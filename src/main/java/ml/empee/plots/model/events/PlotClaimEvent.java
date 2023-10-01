package ml.empee.plots.model.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ml.empee.plots.model.entities.Plot;

/**
 * Event fired when a player claims a plot
 */

@RequiredArgsConstructor
public class PlotClaimEvent extends Event {

  @Getter
  private static final HandlerList handlerList = new HandlerList();

  @Getter
  private final Plot plot;

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }
  
}
