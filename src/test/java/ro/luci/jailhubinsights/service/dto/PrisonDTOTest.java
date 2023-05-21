package ro.luci.jailhubinsights.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ro.luci.jailhubinsights.web.rest.TestUtil;

class PrisonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrisonDTO.class);
        PrisonDTO prisonDTO1 = new PrisonDTO();
        prisonDTO1.setId(1L);
        PrisonDTO prisonDTO2 = new PrisonDTO();
        assertThat(prisonDTO1).isNotEqualTo(prisonDTO2);
        prisonDTO2.setId(prisonDTO1.getId());
        assertThat(prisonDTO1).isEqualTo(prisonDTO2);
        prisonDTO2.setId(2L);
        assertThat(prisonDTO1).isNotEqualTo(prisonDTO2);
        prisonDTO1.setId(null);
        assertThat(prisonDTO1).isNotEqualTo(prisonDTO2);
    }
}
