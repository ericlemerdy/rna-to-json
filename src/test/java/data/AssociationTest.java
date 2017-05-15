package data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class AssociationTest {

    @Test
    public void should_map_to_json() throws JsonProcessingException {
        Association association = Association.builder().codeGestionnaire("ABC").build();

        String associationAsJson = new ObjectMapper()
                .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
                .writeValueAsString(association);

        assertThatJson(associationAsJson).node("codeGestionnaire").isEqualTo("ABC");
    }
}