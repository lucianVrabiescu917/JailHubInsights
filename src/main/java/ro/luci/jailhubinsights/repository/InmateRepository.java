package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.luci.jailhubinsights.domain.Inmate;

/**
 * Spring Data JPA repository for the Inmate entity.
 *
 * When extending this class, extend InmateRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface InmateRepository
    extends InmateRepositoryWithBagRelationships, JpaRepository<Inmate, Long>, JpaSpecificationExecutor<Inmate> {
    default Optional<Inmate> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Inmate> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Inmate> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Modifying
    @Query(value = "update inmate i set i.assigned_cell_id = null where i.assigned_cell_id = :areaId", nativeQuery = true)
    void removeAreaFromInmateByAreaId(@Param("areaId") Long areaId);

    @Modifying
    @Query(value = "DELETE asa.* FROM rel_area_inmates asa " + "              WHERE asa.inmate_id = :inmateId", nativeQuery = true)
    void deleteRelationsWithAreaById(@Param("inmateId") Long inmateId);

    @Modifying
    @Query(value = "DELETE asa.* FROM rel_inmate__activity asa " + "              WHERE asa.inmate_id = :inmateId", nativeQuery = true)
    void deleteRelationsWithActivityById(@Param("inmateId") Long inmateId);
}
