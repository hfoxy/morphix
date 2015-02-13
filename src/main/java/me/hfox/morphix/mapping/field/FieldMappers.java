package me.hfox.morphix.mapping.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FieldMappers {

    private Map<FieldData, FieldMapper> mappers = new HashMap<>();

    public void put(FieldData key, FieldMapper value) {
        List<FieldData> update = new ArrayList<>();

        for (Entry<FieldData, FieldMapper> entry: mappers.entrySet()) {
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

    public FieldMapper get(FieldData key) {
        for (Entry<FieldData, FieldMapper> entry: mappers.entrySet()) {
            FieldData data = entry.getKey();
            if (data.equals(key)) {
                return entry.getValue();
            }
        }

        return null;
    }

}
