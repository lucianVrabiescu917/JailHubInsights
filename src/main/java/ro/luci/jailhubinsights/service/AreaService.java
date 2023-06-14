package ro.luci.jailhubinsights.service;

import java.util.*;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.luci.jailhubinsights.domain.*;
import ro.luci.jailhubinsights.repository.AreaRepository;
import ro.luci.jailhubinsights.repository.InmateRepository;
import ro.luci.jailhubinsights.repository.UserRepository;
import ro.luci.jailhubinsights.security.SecurityUtils;
import ro.luci.jailhubinsights.service.dto.AreaDTO;
import ro.luci.jailhubinsights.service.dto.InmateDTO;
import ro.luci.jailhubinsights.service.dto.PrisonDTO;
import ro.luci.jailhubinsights.service.dto.StaffDTO;
import ro.luci.jailhubinsights.service.mapper.AreaMapper;
import ro.luci.jailhubinsights.service.mapper.StaffMapper;

@NamedNativeQuery(
    name = "GetAllDistinctStaffForArea",
    query = "SELECT DISTINCT i.id, i.first_name, i.last_name " +
    "FROM staff i " +
    "JOIN rel_area__assigned_staff_areas ai ON ai.assigned_staff_areas_id = i.id " +
    "JOIN area a ON a.id = ai.area_id " +
    "LEFT JOIN rel_area__composed_of_areas c ON c.area_id = a.id " +
    "LEFT JOIN area composedOf ON composedOf.id = c.composed_of_areas_id " +
    "LEFT JOIN rel_area__assigned_staff_areas aci ON aci.area_id = composedOf.id " +
    "WHERE a.id = :areaId OR composedOf.id = :areaId",
    resultSetMapping = "StaffMapping"
)
@SqlResultSetMapping(
    name = "StaffMapping",
    entities = {
        @EntityResult(
            entityClass = Staff.class,
            fields = {
                @FieldResult(name = "id", column = "i.id"),
                @FieldResult(name = "firstName", column = "i.first_name"),
                @FieldResult(name = "lastName", column = "i.last_name"),
            }
        ),
    }
)
@Service
@Transactional
public class AreaService {

    private final Logger log = LoggerFactory.getLogger(AreaService.class);

    private final AreaRepository areaRepository;

    private final InmateRepository inmateRepository;
    private final AreaMapper areaMapper;
    private final StaffMapper staffMapper;

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AreaService(
        AreaRepository areaRepository,
        InmateRepository inmateRepository,
        AreaMapper areaMapper,
        UserRepository userRepository,
        StaffMapper staffMapper
    ) {
        this.areaRepository = areaRepository;
        this.inmateRepository = inmateRepository;
        this.areaMapper = areaMapper;
        this.userRepository = userRepository;
        this.staffMapper = staffMapper;
    }

    public Area updateAndReturnEntityWithCurrentPrison(AreaDTO areaDTO) {
        Area area = areaMapper.toEntity(areaDTO);
        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }
        if (currentUser != null) {
            area.setPrison(currentUser.getPrison());
        } else {
            area.setPrison(null);
        }
        return area;
    }

    public AreaDTO save(AreaDTO areaDTO) {
        log.debug("Request to save Area : {}", areaDTO);
        Area area = this.updateAndReturnEntityWithCurrentPrison(areaDTO);
        area = areaRepository.save(area);
        return areaMapper.toDto(area);
    }

    public AreaDTO update(AreaDTO areaDTO) {
        log.debug("Request to update Area : {}", areaDTO);
        Area area = this.updateAndReturnEntityWithCurrentPrison(areaDTO);
        area = areaRepository.save(area);
        return areaMapper.toDto(area);
    }

    public Optional<AreaDTO> partialUpdate(AreaDTO areaDTO) {
        log.debug("Request to partially update Area : {}", areaDTO);

        User currentUser = null;
        Optional<User> currentUserOptional = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
        if (currentUserOptional.isPresent()) {
            currentUser = currentUserOptional.get();
        }

        if (currentUser != null && currentUser.getPrison() != null) {
            Prison prison = currentUser.getPrison();
            areaDTO.setPrison(new PrisonDTO(prison.getId(), prison.getName(), prison.getLocation(), prison.getImage()));
        } else {
            areaDTO.setPrison(null);
        }

        return areaRepository
            .findById(areaDTO.getId())
            .map(existingArea -> {
                areaMapper.partialUpdate(existingArea, areaDTO);

                return existingArea;
            })
            .map(areaRepository::save)
            .map(areaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AreaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Areas");
        return areaRepository.findAll(pageable).map(areaMapper::toDto);
    }

    public Page<AreaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return areaRepository.findAllWithEagerRelationships(pageable).map(areaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<AreaDTO> findOne(Long id) {
        log.debug("Request to get Area : {}", id);
        return areaRepository.findOneWithEagerRelationships(id).map(areaMapper::toDto);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Area : {}", id);
        areaRepository.deleteRelationsWithComposedOfByAreaId(id);
        areaRepository.deleteRelationsWithComposingByAreaId(id);
        areaRepository.deleteRelationsWithStaffByAreaId(id);
        inmateRepository.removeAreaFromInmateByAreaId(id);
        areaRepository.deleteById(id);
    }

    @Transactional
    public List<Long> getAllDistinctStaff(Long id) {
        return areaRepository.getAllStaffIds(id);
    }

    @Transactional
    public List<Long> getAllDistinctInmates(Long id) {
        return areaRepository.getAllInmatesIds(id);
    }
}
