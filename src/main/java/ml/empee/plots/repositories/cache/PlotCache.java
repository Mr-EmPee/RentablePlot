package ml.empee.plots.repositories.cache;

import java.util.Optional;
import java.util.TreeMap;

import ml.empee.plots.model.entities.Plot;
import ml.empee.plots.repositories.PlotRepository;
import mr.empee.lightwire.annotations.Singleton;

/**
 * FULL In-Memory cache for plots
 */

@Singleton
public class PlotCache {

  private final TreeMap<Long, Plot> plots = new TreeMap<>();
  private final PlotRepository plotsRepository;

  public PlotCache(PlotRepository plotsRepository) {
    this.plotsRepository = plotsRepository;
    loadFromRepository();
  }

  private void loadFromRepository() {
    plotsRepository.findAll().join().forEach(
        plot -> plots.put(plot.getId(), plot)
    );
  }

  public Plot save(Plot plot) {
    if (plot.getId() == null) {
      plot = plot.withId(plots.lastKey() + 1);
    }

    plots.put(plot.getId(), plot);
    plotsRepository.save(plot);
    
    return plot;
  }

  public Optional<Plot> get(Long id) {
    return Optional.ofNullable(plots.get(id));
  }

}
