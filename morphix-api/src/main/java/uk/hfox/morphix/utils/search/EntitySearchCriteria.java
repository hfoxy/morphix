package uk.hfox.morphix.utils.search;

import uk.hfox.morphix.annotations.Entity;

/**
 * Searches for the entity annotation and bases its result off of the last entity annotation
 */
public class EntitySearchCriteria implements SearchCriteria {

    private boolean inherited = false;

    @Override
    public boolean isSatisfied(Class<?> cls) {
        Entity entity = cls.getAnnotation(Entity.class);
        if (entity != null) {
            this.inherited = entity.parents();
            return true;
        }

        return this.inherited;
    }

}
