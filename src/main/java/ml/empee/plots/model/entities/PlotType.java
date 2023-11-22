package ml.empee.plots.model.entities;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class PlotType {

  String id;
  Integer maxMembers;
  String title;
  Integer containersPerPlayer;

  public int getMaxContainers() {
    return containersPerPlayer * maxMembers;
  }

}
