package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import junit.framework.TestCase;
import me.hfox.morphix.Morphix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FieldMapperTest extends TestCase {

    private Morphix morphix;

    private List<String> list = Arrays.asList("hello", "world");
    private List<List<String>> multidimensionalList = Arrays.asList(Arrays.asList("goodbye", "sun"), Arrays.asList("hello", "moon"));
    private Map<String, Integer> stringIntegerMap;

    public FieldMapperTest() {
        stringIntegerMap = new HashMap<>();
        stringIntegerMap.put("goodbye", 1);
        stringIntegerMap.put("hello", 5);
    }

    @Override
    protected void setUp() throws Exception {
        morphix = new Morphix();
    }

    public void testArrayMapper() throws Exception {
        ArrayMapper<?> mapper = new ArrayMapper<>(String[].class, FieldMapperTest.class, null, morphix);
        String[] strings = new String[]{"hello", "world"};

        BasicDBList object = mapper.unmarshal(strings);
        for (int i = 0; i < strings.length; i++) {
            String string = (String) object.get(i);
            assertEquals(strings[i], string);
        }

        String[] result = (String[]) mapper.marshal(object);
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], result[i]);
        }

        mapper = new ArrayMapper<>(String[][].class, FieldMapperTest.class, null, morphix);
        String[][] multidimensional = new String[][]{new String[]{"goodbye", "sun"}, new String[]{"hello", "moon"}};

        object = mapper.unmarshal(multidimensional);
        for (int i = 0; i < multidimensional.length; i++) {
            BasicDBList dbList = (BasicDBList) object.get(i);
            String[] array = multidimensional[i];
            for (int j = 0; j < array.length; j++) {
                String string = (String) dbList.get(j);
                assertEquals(array[j], string);
            }
        }

        String[][] multidimensionalResult = (String[][]) mapper.marshal(object);
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
        CollectionMapper mapper = new CollectionMapper(FieldMapperTest.class, getClass().getDeclaredField("list"), morphix);

        BasicDBList object = mapper.unmarshal(list);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), (String) object.get(i));
        }

        List<String> result = (List<String>) mapper.marshal(object);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), result.get(i));
        }

        mapper = new CollectionMapper(FieldMapperTest.class, getClass().getDeclaredField("multidimensionalList"), morphix);

        object = mapper.unmarshal(multidimensionalList);
        for (int i = 0; i < multidimensionalList.size(); i++) {
            BasicDBList dbList = (BasicDBList) object.get(i);
            List<String> list = multidimensionalList.get(i);
            for (int j = 0; j < list.size(); j++) {
                String string = (String) dbList.get(j);
                assertEquals(list.get(j), string);
            }
        }

        List<List<String>> multidimensionalResult = (List<List<String>>) mapper.marshal(object);
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
        EnumMapper<TestEnum> mapper = new EnumMapper<>(TestEnum.class, FieldMapperTest.class, null, morphix);

        Object enumObject = mapper.unmarshal(value);
        assertEquals("SOME", enumObject);

        TestEnum result = mapper.marshal(enumObject);
        assertEquals(value, result);

        try {
            result = mapper.marshal("NOT_VALID");
            // fail("Expected a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException ex) {
            result = null;
        }

        assertEquals(null, result);
    }

    @SuppressWarnings("unchecked")
    public void testMapMapper() throws Exception {
        MapMapper stringIntegerMapper = new MapMapper(getClass(), getClass().getDeclaredField("stringIntegerMap"), morphix);
        ObjectMapper<String> stringObjectMapper = new ObjectMapper<>(String.class, getClass(), null, morphix);
        ObjectMapper<Integer> integerObjectMapper = new ObjectMapper<>(Integer.class, getClass(), null, morphix);

        BasicDBList stringIntegerObject = stringIntegerMapper.unmarshal(stringIntegerMap);
        assertEquals(2, stringIntegerObject.size());

        List<String> keys = new ArrayList<>(stringIntegerMap.keySet());
        for (int i = 0; i < stringIntegerObject.size(); i++) {
            DBObject object = (DBObject) stringIntegerObject.get(i);
            String key = stringObjectMapper.unmarshal(object.get("key"));
            int value = integerObjectMapper.unmarshal(object.get("value"));

            String origKey = keys.get(i);
            int origValue = stringIntegerMap.get(origKey);
            assertEquals(origKey, key);
            assertEquals(origValue, value);
        }

        Map<String, Integer> resultStringIntegerMap = (Map<String, Integer>) stringIntegerMapper.marshal(stringIntegerObject);
        assertEquals(2, resultStringIntegerMap.size());

        int i = 0;
        for (Entry<String, Integer> entry : resultStringIntegerMap.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            String origKey = keys.get(i);
            int origValue = stringIntegerMap.get(origKey);
            assertEquals(origKey, key);
            assertEquals(origValue, value);

            i++;
        }
    }

    public void testObjectMapper() throws Exception {
        String string = "hello world";
        ObjectMapper<String> stringMapper = new ObjectMapper<>(String.class, FieldMapperTest.class, null, morphix);
        String stringObject = stringMapper.unmarshal(string);
        assertEquals(string, stringObject);
        Object stringResult = stringMapper.marshal(stringObject);
        assertEquals(string, stringResult);

        int integer = 12;
        ObjectMapper<Integer> intMapper = new ObjectMapper<>(int.class, FieldMapperTest.class, null, morphix);
        int intObject = intMapper.unmarshal(integer);
        assertEquals(integer, intObject);
        Object intResult = intMapper.marshal(intObject);
        assertEquals(integer, intResult);
    }

    public static enum TestEnum {

        SOME,
        ENUM,
        VALUES

    }

}