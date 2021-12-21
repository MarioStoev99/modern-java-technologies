package bg.sofia.uni.fmi.mjt.tagger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TaggerTest {

    private Reader input;
    private Tagger tagger;

    @Before
    public void setUp() throws IOException {
        String cities = "Sofia,Bulgaria\n" + "Plovdiv,Bulgaria\n" + "Varna,Bulgaria\n" + "Burgas,Bulgaria";
        Reader cityReader = new StringReader(cities);
        String file = "Plovdiv\'s old town is a major tourist attraction. It is the second largest city in Bulgaria, after the capital ,Sofia. Sofia is the best town in Bulgaria.";

        input = new StringReader(file);
        tagger = new Tagger(cityReader);
    }

    @Test
    public void testTagCitiesComplex() throws IOException {
        Writer writer = new StringWriter();
        Reader file = new StringReader("....pLoVeDiv. The flight distance between VaRNA and soFIA is three hundred and sixty four km. pLOVdiv.....");
        tagger.tagCities(file, writer);

        String expected = "....pLoVeDiv. The flight distance between <city country=\"Bulgaria\">VaRNA</city> and <city country=\"Bulgaria\">soFIA</city> is three hundred and sixty four km. <city country=\"Bulgaria\">pLOVdiv</city>.....";
        assertEquals("Plovediv is not Plovdiv ;)", expected, writer.toString());
    }

    @Test
    public void testTagCitiesSimple() throws IOException {
        String expected = "<city country=\"Bulgaria\">Plovdiv</city>'s old town is a major tourist attraction. It is the second largest city in Bulgaria, after the capital ,<city country=\"Bulgaria\">Sofia</city>. <city country=\"Bulgaria\">Sofia</city> is the best town in Bulgaria.";

        Writer writer = new StringWriter();
        tagger.tagCities(input, writer);

        assertEquals(expected, writer.toString());
    }

    @Test
    public void testTagCitiesEmptyString() throws IOException {
        String expected = "";
        Writer writer = new StringWriter();
        tagger.tagCities(new StringReader(""), writer);

        assertEquals(expected, writer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTagCitiesNull() throws IOException {
        tagger.tagCities(null, null);
    }

    @Test
    public void testGetAllTaggedCities() throws IOException {
        tagger.tagCities(input, new StringWriter());

        Collection<String> actual = tagger.getAllTaggedCities();

        Collection<String> expected = new ArrayList<>();
        expected.add("Sofia");
        expected.add("Plovdiv");

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void testGetAllTagsCount() throws IOException {
        tagger.tagCities(input, new StringWriter());

        long actual = tagger.getAllTagsCount();
        assertEquals(3, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNMostTaggedCitiesNegativeNumber() {
        tagger.getNMostTaggedCities(-1);
    }

    @Test
    public void testGetNMostTaggedCities() throws IOException {
        tagger.tagCities(input, new StringWriter());

        Collection<String> actual = tagger.getNMostTaggedCities(1);

        Collection<String> expected = new ArrayList<>(1);
        expected.add("Sofia");

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

}
