package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import junit.framework.TestCase;
import me.hfox.morphix.Morphix;

import java.util.Arrays;
import java.util.List;

public class FieldMapperTest extends TestCase {

    private Morphix morphix;

    private List<String> list = Arrays.asList("hello", "world");

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
    }

}