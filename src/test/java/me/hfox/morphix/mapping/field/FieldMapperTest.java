package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import junit.framework.TestCase;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.examples.EmbeddedClass;
import me.hfox.morphix.examples.TestClass;
import me.hfox.morphix.mapping.MappingData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FieldMapperTest extends TestCase {

    private Morphix morphix;

    private TestClass testClass;
    private List<String> list = Arrays.asList("hello", "world");
    private List<List<String>> multidimensionalList = Arrays.asList(Arrays.asList("goodbye", "sun"), Arrays.asList("hello", "moon"));
    private Map<String, Integer> map;
    private Map<String, Map<String, List<Integer>>> stringMapMap;

    @Override
    protected void setUp() throws Exception {
        morphix = new Morphix(null, null);

        map = new HashMap<>();
        map.put("goodbye", 1);
        map.put("hello", 5);

        stringMapMap = new HashMap<>();
        Map<String, List<Integer>> helloMap = new HashMap<>();
        helloMap.put("world", Arrays.asList(5, 7, 9, 2, 3));
        helloMap.put("earth", Arrays.asList(2, 6, 12, 476));
        stringMapMap.put("hello", helloMap);

        Map<String, List<Integer>> goodbyeMap = new HashMap<>();
        goodbyeMap.put("solar system", Arrays.asList(12, 98, 7, 5));
        goodbyeMap.put("penguins", Arrays.asList(8, 4, 9, 23, 189));
        stringMapMap.put("goodbye", goodbyeMap);

        testClass = new TestClass("hello world", Arrays.asList("hello", "world"), new EmbeddedClass("I'm embedded"));
    }

    public void testArrayMapper() throws Exception {
        MappingData mappingData = new MappingData();
        ArrayMapper<?> mapper = new ArrayMapper<>(mappingData, String[].class, FieldMapperTest.class, null, morphix);
        String[] strings = new String[]{"hello", "world"};

        BasicDBList object = mapper.marshal(strings);
        for (int i = 0; i < strings.length; i++) {
            String string = (String) object.get(i);
            assertEquals(strings[i], string);
        }

        String[] result = (String[]) mapper.unmarshal(object);
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], result[i]);
        }

        mapper = new ArrayMapper<>(mappingData, String[][].class, FieldMapperTest.class, null, morphix);
        String[][] multidimensional = new String[][]{new String[]{"goodbye", "sun"}, new String[]{"hello", "moon"}};

        object = mapper.marshal(multidimensional);
        for (int i = 0; i < multidimensional.length; i++) {
            BasicDBList dbList = (BasicDBList) object.get(i);
            String[] array = multidimensional[i];
            for (int j = 0; j < array.length; j++) {
                String string = (String) dbList.get(j);
                assertEquals(array[j], string);
            }
        }

        String[][] multidimensionalResult = (String[][]) mapper.unmarshal(object);
        for (int i = 0; i < multidimensional.length; i++) {
            String[] array = multidimensional[i];
            String[] resultArray = multidimensionalResult[i];
            for (int j = 0; j < array.length; j++) {
                assertEquals(array[i], resultArray[i]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void testCollectionMapper() throws Exception {
        MappingData mappingData = new MappingData();
        CollectionMapper mapper = new CollectionMapper(mappingData, FieldMapperTest.class, getClass().getDeclaredField("list"), morphix);

        BasicDBList object = mapper.marshal(list);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), (String) object.get(i));
        }

        List<String> result = (List<String>) mapper.unmarshal(object);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), result.get(i));
        }

        mapper = new CollectionMapper(mappingData, FieldMapperTest.class, getClass().getDeclaredField("multidimensionalList"), morphix);

        object = mapper.marshal(multidimensionalList);
        for (int i = 0; i < multidimensionalList.size(); i++) {
            BasicDBList dbList = (BasicDBList) object.get(i);
            List<String> list = multidimensionalList.get(i);
            for (int j = 0; j < list.size(); j++) {
                String string = (String) dbList.get(j);
                assertEquals(list.get(j), string);
            }
        }

        List<List<String>> multidimensionalResult = (List<List<String>>) mapper.unmarshal(object);
        for (int i = 0; i < multidimensionalList.size(); i++) {
            List<String> list = multidimensionalList.get(i);
            List<String> resultList = multidimensionalResult.get(i);
            for (int j = 0; j < list.size(); j++) {
                assertEquals(list.get(i), resultList.get(i));
            }
        }
    }

    public void testEnumMapper() throws Exception {
        TestEnum value = TestEnum.SOME;
        EnumMapper<TestEnum> mapper = new EnumMapper<>(new MappingData(), TestEnum.class, FieldMapperTest.class, null, morphix);

        Object enumObject = mapper.marshal(value);
        assertEquals("SOME", enumObject);

        TestEnum result = mapper.unmarshal(enumObject);
        assertEquals(value, result);

        try {
            result = mapper.unmarshal("NOT_VALID");
            // fail("Expected a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException ex) {
            result = null;
        }

        assertEquals(null, result);
    }

    @SuppressWarnings("unchecked")
    public void testMapMapper() throws Exception {
        MappingData mapper = new MappingData();
        MapMapper stringIntegerMapper = new MapMapper(mapper, getClass(), getClass().getDeclaredField("map"), morphix);
        ObjectMapper<String> stringMapper = new ObjectMapper<>(mapper, String.class, getClass(), null, morphix);
        ObjectMapper<Integer> integerMapper = new ObjectMapper<>(mapper, Integer.class, getClass(), null, morphix);

        BasicDBList stringIntegerObject = stringIntegerMapper.marshal(map);
        assertEquals(map.size(), stringIntegerObject.size());

        List<String> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < stringIntegerObject.size(); i++) {
            DBObject object = (DBObject) stringIntegerObject.get(i);
            String key = stringMapper.marshal(object.get("key"));
            int value = integerMapper.marshal(object.get("value"));

            String origKey = keys.get(i);
            int origValue = map.get(origKey);
            assertEquals(origKey, key);
            assertEquals(origValue, value);
        }

        Map<String, Integer> resultStringIntegerMap = (Map<String, Integer>) stringIntegerMapper.unmarshal(stringIntegerObject);
        assertEquals(map.size(), resultStringIntegerMap.size());

        int index = 0;
        for (Entry<String, Integer> entry : resultStringIntegerMap.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            String origKey = keys.get(index);
            int origValue = map.get(origKey);
            assertEquals(origKey, key);
            assertEquals(origValue, value);

            index++;
        }

        MapMapper mapMapper = new MapMapper(mapper, getClass(), getClass().getDeclaredField("stringMapMap"), morphix);

        BasicDBList dbList = mapMapper.marshal(stringMapMap);
        assertEquals(stringMapMap.size(), dbList.size());

        List<String> stringMapMapKeys = new ArrayList<>(stringMapMap.keySet());
        for (int i = 0; i < dbList.size(); i++) {
            DBObject fMapObject = (DBObject) dbList.get(i); // fMap = first map
            String fMapKey = stringMapper.marshal(fMapObject.get("key"));
            BasicDBList fMapValue = (BasicDBList) fMapObject.get("value");
            Map<String, List<Integer>> fMapValueMap = stringMapMap.get(fMapKey);

            String fMapOrigKey = stringMapMapKeys.get(i);
            assertEquals(fMapOrigKey, fMapKey);

            List<String> stringListMapKeys = new ArrayList<>(fMapValueMap.keySet());
            for (int j = 0; j < fMapValue.size(); j++) {
                DBObject sMapObject = (DBObject) fMapValue.get(j); // sMap = second map
                String sMapKey = stringMapper.marshal(sMapObject.get("key"));
                BasicDBList sMapValue = (BasicDBList) sMapObject.get("value");
                List<Integer> sMapValueList = fMapValueMap.get(sMapKey);

                String sMapOrigKey = stringListMapKeys.get(j);
                assertEquals(sMapOrigKey, sMapKey);

                for (int k = 0; k < sMapValueList.size(); k++) {
                    int value = integerMapper.marshal(sMapValue.get(k));
                    assertEquals((int) sMapValueList.get(k), value);
                }
            }
        }

        Map<String, Map<String, List<Integer>>> resultMap = mapMapper.unmarshal(dbList);
        assertEquals(stringMapMap.size(), resultMap.size());

        List<String> fMapKeys = new ArrayList<>(resultMap.keySet());
        for (int i = 0; i < stringMapMapKeys.size(); i++) {
            String origMapKey = stringMapMapKeys.get(i);
            String fMapKey = fMapKeys.get(i);
            assertEquals(origMapKey, fMapKey);

            Map<String, List<Integer>> origMap = stringMapMap.get(origMapKey);
            Map<String, List<Integer>> map = resultMap.get(origMapKey);
            assertEquals(origMap.size(), map.size());

            List<String> origMapKeys = new ArrayList<>(origMap.keySet());
            List<String> sMapKeys = new ArrayList<>(map.keySet());
            for (int j = 0; j < sMapKeys.size(); j++) {
                String origKey = origMapKeys.get(j);
                String key = sMapKeys.get(j);
                assertEquals(origKey, key);

                List<Integer> origList = origMap.get(origKey);
                List<Integer> list = map.get(origKey);
                assertEquals(origList.size(), list.size());

                for (int k = 0; k < origList.size(); k++) {
                    int origEntry = origList.get(k);
                    int entry = list.get(k);
                    assertEquals(origEntry, entry);
                }
            }
        }
    }

    public void testObjectMapper() throws Exception {
        testObject(String.class, "hello world");
        testObject(Integer.class, 12);
        testObject(Double.class, 15.87);
        testObject(Float.class, 187.72F);
        testObject(Short.class, (short) 5);
        testObject(Long.class, 1276L);
        testObject(Byte.class, (byte) 8);

        testNumber(Integer.class, 178.12, 178);
        testNumber(Integer.class, 178.97, 178);
    }

    public <T> void testObject(Class<T> cls, T object) {
        ObjectMapper<T> stringMapper = new ObjectMapper<>(new MappingData(), cls, FieldMapperTest.class, null, morphix);
        T unmarshal = stringMapper.marshal(object);
        assertEquals(object, unmarshal);
        Object marshal = stringMapper.unmarshal(unmarshal);
        assertEquals(object, marshal);
    }

    public <T extends Number> void testNumber(Class<T> cls, Number object, T expect) {
        ObjectMapper<T> stringMapper = new ObjectMapper<>(new MappingData(), cls, FieldMapperTest.class, null, morphix);
        T unmarshal = stringMapper.marshal(object);
        assertEquals(expect, unmarshal);
        Object marshal = stringMapper.unmarshal(unmarshal);
        assertEquals(expect, marshal);
    }

    public void testEntityMapper() throws Exception {
        EntityMapper<TestClass> mapper = new EntityMapper<>(new MappingData(), TestClass.class, getClass(), getClass().getDeclaredField("testClass"), morphix);

        DBObject dbObject = (DBObject) mapper.marshal(testClass);
        assertEquals(testClass.getTest(), (String) dbObject.get("test"));

        BasicDBList list = (BasicDBList) dbObject.get("strings");
        assertEquals(testClass.getStrings().size(), list.size());
        for (int i = 0; i < testClass.getStrings().size(); i++) {
            assertEquals(testClass.getStrings().get(i), (String) list.get(i));
        }

        DBObject embedded = (DBObject) dbObject.get("embedded");
        assertEquals(testClass.getEmbedded().getEmbedded(), (String) embedded.get("some_string"));

        TestClass remapped = mapper.unmarshal(dbObject);
        assertEquals(testClass.getTest(), remapped.getTest());

        assertEquals(testClass.getStrings().size(), remapped.getStrings().size());
        for (int i = 0; i < testClass.getStrings().size(); i++) {
            assertEquals(testClass.getStrings().get(i), remapped.getStrings().get(i));
        }

        assertEquals(testClass.getEmbedded().getEmbedded(), remapped.getEmbedded().getEmbedded());
    }

    public static enum TestEnum {

        SOME,
        ENUM,
        VALUES

    }

}