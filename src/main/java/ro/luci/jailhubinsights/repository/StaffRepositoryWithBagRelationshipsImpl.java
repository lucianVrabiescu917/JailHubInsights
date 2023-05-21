package ro.luci.jailhubinsights.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ro.luci.jailhubinsights.domain.Staff;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class StaffRepositoryWithBagRelationshipsImpl implements StaffRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Staff> fetchBagRelationships(Optional<Staff> staff) {
        return staff.map(this::fetchActivities);
    }

    @Override
    public Page<Staff> fetchBagRelationships(Page<Staff> staff) {
        return new PageImpl<>(fetchBagRelationships(staff.getContent()), staff.getPageable(), staff.getTotalElements());
    }

    @Override
    public List<Staff> fetchBagRelationships(List<Staff> staff) {
        return Optional.of(staff).map(this::fetchActivities).orElse(Collections.emptyList());
    }

    Staff fetchActivities(Staff result) {
        return entityManager
            .createQuery("select staff from Staff staff left join fetch staff.activities where staff is :staff", Staff.class)
            .setParameter("staff", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Staff> fetchActivities(List<Staff> staff) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, staff.size()).forEach(index -> order.put(staff.get(index).getId(), index));
        List<Staff> result = entityManager
            .createQuery("select distinct staff from Staff staff left join fetch staff.activities where staff in :staff", Staff.class)
            .setParameter("staff", staff)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
