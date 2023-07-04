package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.luci.jailhubinsights.domain.Area;
import ro.luci.jailhubinsights.domain.Prison;
import ro.luci.jailhubinsights.domain.Staff;
import ro.luci.jailhubinsights.domain.enumeration.StaffType;

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

    @Query(
        value = "SELECT distinct s.id " +
        "FROM staff s " +
        "          JOIN rel_area__assigned_staff_areas ai ON ai.assigned_staff_areas_id = s.id " +
        "          LEFT JOIN area ap on ai.area_id = ap.id " +
        "          left join rel_area__composed_of_areas cca on ai.area_id = cca.composed_of_areas_id " +
        "WHERE  ap.id = :areaId or cca.area_id = :areaId ",
        nativeQuery = true
    )
    List<Long> getAllStaffIds(@Param("areaId") Long areaId);

    @Query(
        value = "SELECT distinct s.id " +
        "FROM inmate s " +
        "          JOIN rel_area_inmates ai ON ai.inmate_id = s.id " +
        "          LEFT JOIN area ap on ai.area_id = ap.id " +
        "          left join rel_area__composed_of_areas cca on ai.area_id = cca.composed_of_areas_id " +
        "WHERE  ap.id = :areaId or cca.area_id = :areaId ",
        nativeQuery = true
    )
    List<Long> getAllInmatesIds(@Param("areaId") Long areaId);
}
