package ml.empee.plots.repositories.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;

import ml.empee.plots.model.entities.Entity;
import ml.empee.plots.repositories.AbstractRepository;

/**
 * FULL In-Memory repository with async persistence
 */

public abstract class AbstractMemoryRepository<T extends Entity> {

  private final TreeMap<Long, T> cache = new TreeMap<>();
  private final AbstractRepository<T> repository;

  protected AbstractMemoryRepository(AbstractRepository<T> repository) {
    this.repository = repository;

    loadFromRepository();
  }

  protected void loadFromRepository() {
    repository.findAll().join().forEach(
        e -> cache.put(e.getId(), e));
  }

  public Collection<T> findAll() {
    return Collections.unmodifiableCollection(cache.values());
  }

  public T save(T entity) {
    if (entity.getId() == null) {
      entity = (T) entity.withId(
          cache.isEmpty() ? 0L : cache.lastKey() + 1
      );
    }

    cache.put(entity.getId(), entity);
    repository.save(entity);

    return entity;
  }

  public void delete(Long id) {
    cache.remove(id);
    repository.delete(id);
  }

  public Optional<T> get(Long id) {
    return Optional.ofNullable(cache.get(id));
  }

}
