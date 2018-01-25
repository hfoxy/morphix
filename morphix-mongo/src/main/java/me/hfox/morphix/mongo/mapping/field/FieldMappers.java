/*-
 * ========================LICENSE_START========================
 * Morphix Mongo
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
package me.hfox.morphix.mongo.mapping.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FieldMappers {

    private Map<FieldData, FieldMapper> mappers = new HashMap<>();

    public synchronized void put(FieldData key, FieldMapper value) {
        List<FieldData> update = new ArrayList<>();

        for (Entry<FieldData, FieldMapper> entry : mappers.entrySet()) {
            FieldData data = entry.getKey();
            if (data.equals(key)) {
                update.add(data);
            }
        }

        mappers.put(key, value);
        for (FieldData data : update) {
            mappers.remove(data);
        }
    }

    public synchronized FieldMapper get(FieldData key) {
        for (Entry<FieldData, FieldMapper> entry : mappers.entrySet()) {
            FieldData data = entry.getKey();
            if (data.equals(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

}
