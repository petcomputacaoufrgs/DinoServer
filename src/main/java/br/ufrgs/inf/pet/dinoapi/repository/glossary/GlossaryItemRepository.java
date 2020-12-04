package br.ufrgs.inf.pet.dinoapi.repository.glossary;

import br.ufrgs.inf.pet.dinoapi.entity.glossary.GlossaryItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlossaryItemRepository extends CrudRepository<GlossaryItem, Long> {

    @Query("SELECT gi FROM GlossaryItem gi WHERE gi.id = :id AND gi.exists = TRUE")
    Optional<GlossaryItem> findById(Long id);

    @Query("SELECT gi FROM GlossaryItem gi WHERE gi.exists = TRUE")
    List<GlossaryItem> findAll();

    @Query("SELECT gi FROM GlossaryItem gi WHERE gi.id IN :ids AND gi.exists = TRUE")
    List<GlossaryItem> findByIds(List<Long> ids);
}
