package uk.hfox.morphix.mongo.helper;

import uk.hfox.morphix.helper.Helper;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.utils.Conditions;

import static uk.hfox.morphix.utils.Definitions.SNAKE_CASE_SPLIT_REGEX;

public class CollectionHelper extends Helper<MorphixMongoConnector> {

    private boolean snakeCase = true;
    private boolean lowerCase = true;

    public CollectionHelper(MorphixMongoConnector connector) {
        super(connector);
    }

    public boolean isSnakeCase() {
        return snakeCase;
    }

    public void setSnakeCase(boolean snakeCase) {
        this.snakeCase = snakeCase;
    }

    public boolean isLowerCase() {
        return lowerCase;
    }

    public void setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    public String generate(Class<?> clazz) {
        Conditions.notNull(clazz);

        String name = clazz.getSimpleName();
        if (snakeCase) {
            String[] parts = name.split(SNAKE_CASE_SPLIT_REGEX);

            StringBuilder builder = new StringBuilder(name.length() + (parts.length - 1));

            for (int i = 0; i < parts.length - 1; i++) {
                builder.append(parts[i]).append('_');
            }

            builder.append(parts[parts.length - 1]);
            name = builder.toString();
        }

        if (lowerCase) {
            name = name.toLowerCase();
        }

        return name;
    }

}
