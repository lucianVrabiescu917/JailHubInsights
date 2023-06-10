package ro.luci.jailhubinsights.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.luci.jailhubinsights.domain.Staff;

@Repository
public interface StaffRepository extends StaffRepositoryWithBagRelationships, JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {
    default Optional<Staff> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Staff> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Staff> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
