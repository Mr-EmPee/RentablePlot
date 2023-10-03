package ml.empee.plots.repositories.memory;

import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.repositories.PlotRepository;
import mr.empee.lightwire.annotations.Singleton;

/**
 * FULL In-Memory cache for plots
 */

@Singleton
public class PlotMemoryCache extends AbstractMemoryRepository<Plot> {

  public PlotMemoryCache(PlotRepository plotsRepository) {
    super(plotsRepository);
  }

}
