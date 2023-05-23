package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.luci.jailhubinsights.domain.Area;

/**
 * Spring Data JPA repository for the Area entity.
 *
 * When extending this class, extend AreaRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface AreaRepository extends AreaRepositoryWithBagRelationships, JpaRepository<Area, Long>, JpaSpecificationExecutor<Area> {
    default Optional<Area> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Area> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Area> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Modifying
    @Query(
        value = "DELETE ca.* FROM rel_area__composed_of_areas ca " + "        WHERE ca.composed_of_areas_id = :areaId",
        nativeQuery = true
    )
    void deleteRelationsWithComposedOfByAreaId(@Param("areaId") Long areaId);

    @Modifying
    @Query(value = "DELETE ca.* FROM rel_area__composed_of_areas ca " + "        WHERE ca.area_id = :areaId", nativeQuery = true)
    void deleteRelationsWithComposingByAreaId(@Param("areaId") Long areaId);

    @Modifying
    @Query(value = "DELETE asa.* FROM rel_area__assigned_staff_areas asa " + "        WHERE asa.area_id = :areaId", nativeQuery = true)
    void deleteRelationsWithStaffByAreaId(@Param("areaId") Long areaId);
}
