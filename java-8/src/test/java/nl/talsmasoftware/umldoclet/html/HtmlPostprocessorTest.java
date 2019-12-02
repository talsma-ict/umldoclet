package nl.talsmasoftware.umldoclet.html;

import nl.talsmasoftware.umldoclet.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class HtmlPostprocessorTest {

    private Configuration mockConfiguration;
    private HtmlPostprocessor postprocessor;

    @BeforeEach
    void setUp() {
        mockConfiguration = mock(Configuration.class);
        postprocessor = new HtmlPostprocessor(mockConfiguration);
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(mockConfiguration);
    }

    @Test
    void testCreatePostprocessorWithoutConfiguration() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new HtmlPostprocessor(null));
        assertThat(npe.getMessage(), notNullValue());
    }

    @Test
    void testNonExistingDestinationDirectory() {
        when(mockConfiguration.destinationDirectory()).thenReturn("/tmp/non-existing-directory");
        RuntimeException rte = assertThrows(RuntimeException.class, postprocessor::postProcessHtml);

        assertThat(rte, instanceOf(IllegalStateException.class));
        assertThat(rte.getMessage(), containsStringIgnoringCase("destination directory"));

        verify(mockConfiguration).destinationDirectory();
    }

}
