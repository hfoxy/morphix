/*-
 * ========================LICENSE_START========================
 * Morphix MongoDB
 * %%
 * Copyright (C) 2017 - 2018 Harry Fox
 * %%
 * This file is part of Morphix, licensed under the MIT License (MIT).
 *
 * Copyright 2018 Harry Fox <https://hfox.uk/>
 * Copyright 2018 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ========================LICENSE_END========================
 */
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
