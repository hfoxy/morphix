package uk.hfox.morphix.mapper;

import org.junit.jupiter.api.Test;
import uk.hfox.morphix.annotations.Entity;
import uk.hfox.morphix.annotations.Lifecycle;
import uk.hfox.morphix.annotations.field.Transient;
import uk.hfox.morphix.annotations.lifecycle.field.AccessedAt;
import uk.hfox.morphix.annotations.lifecycle.field.CreatedAt;
import uk.hfox.morphix.annotations.lifecycle.field.UpdatedAt;
import uk.hfox.morphix.annotations.lifecycle.method.AfterCreate;
import uk.hfox.morphix.annotations.lifecycle.method.BeforeCreate;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.mapper.lifecycle.LifecycleAction;
import uk.hfox.morphix.transform.TestTransformer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MappedEntityTest {

    @Test
    void getTransformer() {
        TestTransformer transformer = new TestTransformer();
        TestMappedEntity entity = new TestMappedEntity(transformer, StandardEntity.class);
        assertEquals(transformer, entity.getTransformer());
    }

    @Test
    void isLifecycle() {
        TestTransformer transformer = new TestTransformer();
        TestMappedEntity entity = new TestMappedEntity(transformer, StandardEntity.class);
        assertFalse(entity.isLifecycle());

        entity = new TestMappedEntity(transformer, LifecycleEntity.class);
        assertTrue(entity.isLifecycle());
    }

    @Test
    void map() {
        TestTransformer transformer = new TestTransformer();
        TestMappedEntity entity = new TestMappedEntity(transformer, StandardEntity.class);
        entity.map();

        entity = new TestMappedEntity(transformer, LifecycleEntity.class);
        entity.map();
    }

    @Test
    void insert() {
        TestTransformer transformer = new TestTransformer();
        TestMappedEntity entity = new TestMappedEntity(transformer, BaseEntity.class);
        entity.map();

        TestMappedEntity brokenEntity = new TestMappedEntity(transformer, ChildEntity.class);
        assertThrows(MorphixEntityException.class, brokenEntity::map);
    }

    @Test
    void call() {
        TestTransformer transformer = new TestTransformer();
        TestMappedEntity entity = new TestMappedEntity(transformer, LifecycleTest.class);
        entity.map();

        LifecycleTest test = new LifecycleTest();
        entity.call(LifecycleAction.BEFORE_CREATE, test);
        assertTrue(test.beforeCreate);

        entity.call(LifecycleAction.AFTER_CREATE, test);
        assertTrue(test.afterCreate);

        assertThrows(IllegalArgumentException.class, () -> entity.call(LifecycleAction.CREATED_AT, test));
        assertThrows(IllegalArgumentException.class, () -> entity.call(null, test));
        assertThrows(IllegalArgumentException.class, () -> entity.call(LifecycleAction.CREATED_AT, null));

        LocalDateTime time = LocalDateTime.now();

        assertNull(test.createdAt);
        entity.set(LifecycleAction.CREATED_AT, test, time);
        assertEquals(time, test.createdAt);

        assertNull(test.accessedAt);
        entity.set(LifecycleAction.ACCESSED_AT, test, time);
        assertEquals(time, test.accessedAt);

        assertNull(test.updatedAt);
        entity.set(LifecycleAction.UPDATED_AT, test, time);
        assertEquals(time, test.updatedAt);

        assertThrows(IllegalArgumentException.class, () -> entity.set(LifecycleAction.BEFORE_CREATE, test, time));
        assertThrows(IllegalArgumentException.class, () -> entity.set(null, test, time));
        assertThrows(IllegalArgumentException.class, () -> entity.set(LifecycleAction.BEFORE_CREATE, null, time));

        InvalidLifecycleTest invalidTest = new InvalidLifecycleTest();
        TestMappedEntity invalidEntity = new TestMappedEntity(transformer, InvalidLifecycleTest.class);
        invalidEntity.map();

        assertNull(invalidTest.createdAt);
        // assertThrows(MorphixFieldException.class, () -> entity.set(LifecycleAction.CREATED_AT, test, time));
        invalidEntity.set(LifecycleAction.CREATED_AT, invalidTest, time);
        System.out.println(invalidTest.createdAt);
    }

    @Entity
    private static class StandardEntity {
    }

    @Entity
    @Lifecycle
    private static class LifecycleEntity {
    }

    @Entity
    @Lifecycle
    private static class BaseEntity {

        @Transient
        private String ignored;

        private String string;

    }

    private static class ChildEntity extends BaseEntity {

        private String string;

    }

    @Entity
    @Lifecycle
    private static class LifecycleTest {

        private boolean beforeCreate;
        private boolean afterCreate;

        @CreatedAt
        private LocalDateTime createdAt;

        @AccessedAt
        private LocalDateTime accessedAt;

        @UpdatedAt
        private LocalDateTime updatedAt;

        @BeforeCreate
        public void beforeCreate() {
            this.beforeCreate = true;
        }

        @AfterCreate
        public void afterCreate() {
            this.afterCreate = true;
        }

    }

    @Entity
    @Lifecycle
    private static class InvalidLifecycleTest {

        @CreatedAt
        private final LocalDateTime createdAt;

        public InvalidLifecycleTest() {
            this.createdAt = null;
        }

    }

}