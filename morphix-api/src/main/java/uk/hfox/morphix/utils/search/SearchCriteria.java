package uk.hfox.morphix.utils.search;

/**
 * Represents a search criteria used by {@link Search}
 */
public interface SearchCriteria {

    /**
     * Returns if this search criteria is satisfied with the supplied class
     *
     * @param cls The class to check
     *
     * @return True if the criteria is satisfied, otherwise false
     */
    boolean isSatisfied(Class<?> cls);

}
