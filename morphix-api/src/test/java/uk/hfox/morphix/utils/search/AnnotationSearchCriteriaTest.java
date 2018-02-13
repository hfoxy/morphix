package uk.hfox.morphix.utils.search;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnnotationSearchCriteriaTest {

    @Test
    void isSatisfied() {
        AnnotationSearchCriteria criteria = new AnnotationSearchCriteria(Entity.class);
        List<Field> fields = Search.getAllFields(AnnotatedClass.class, criteria);
        assertEquals(0, fields.size());

        criteria = new AnnotationSearchCriteria(Entity.class);
        fields = Search.getAllFields(ChildAnnotatedClass.class, criteria);
        assertEquals(1, fields.size());

        criteria = new AnnotationSearchCriteria(Entity.class);
        fields = Search.getAllFields(BabyAnnotatedClass.class, criteria);
        assertEquals(2, fields.size());

        SearchCriteria search = criteria;
        assertThrows(IllegalArgumentException.class, () -> Search.getAllFields(null, search));
        assertThrows(IllegalArgumentException.class, () -> Search.getAllFields(BaseClass.class, null));
    }

    private static class BaseClass {

        private String field;

    }

    @Entity
    private static class AnnotatedClass extends BaseClass {
    }

    private static class ChildAnnotatedClass extends AnnotatedClass {

        private String lowerField;

    }

    private static class BabyAnnotatedClass extends ChildAnnotatedClass {

        private String lowestField;

    }

}