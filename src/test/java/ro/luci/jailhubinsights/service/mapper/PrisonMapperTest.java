package ro.luci.jailhubinsights.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrisonMapperTest {

    private PrisonMapper prisonMapper;

    @BeforeEach
    public void setUp() {
        prisonMapper = new PrisonMapperImpl();
    }
}
