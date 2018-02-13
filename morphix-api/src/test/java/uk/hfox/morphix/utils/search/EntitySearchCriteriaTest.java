package uk.hfox.morphix.utils.search;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntitySearchCriteriaTest {

    @Test
    void isSatisfied() {
        EntitySearchCriteria criteria = new EntitySearchCriteria();
        List<Field> fields = Search.getAllFields(BaseClass.class, criteria);
        assertEquals(0, fields.size());

        criteria = new EntitySearchCriteria();
        fields = Search.getAllFields(AnnotatedClass.class, criteria);
        assertEquals(1, fields.size());

        criteria = new EntitySearchCriteria();
        fields = Search.getAllFields(ParentedClass.class, criteria);
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

        private String child;

    }

    @Entity(parents = true)
    private static class ParentedClass extends BaseClass {

        private String child;

    }

}