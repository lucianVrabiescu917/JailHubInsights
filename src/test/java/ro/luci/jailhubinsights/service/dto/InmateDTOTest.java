package ro.luci.jailhubinsights.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ro.luci.jailhubinsights.web.rest.TestUtil;

class InmateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InmateDTO.class);
        InmateDTO inmateDTO1 = new InmateDTO();
        inmateDTO1.setId(1L);
        InmateDTO inmateDTO2 = new InmateDTO();
        assertThat(inmateDTO1).isNotEqualTo(inmateDTO2);
        inmateDTO2.setId(inmateDTO1.getId());
        assertThat(inmateDTO1).isEqualTo(inmateDTO2);
        inmateDTO2.setId(2L);
        assertThat(inmateDTO1).isNotEqualTo(inmateDTO2);
        inmateDTO1.setId(null);
        assertThat(inmateDTO1).isNotEqualTo(inmateDTO2);
    }
}
