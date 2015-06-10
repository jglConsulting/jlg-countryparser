package jlg.countryparser.test;

import jlg.countryparser.BaseCountry;
import jlg.countryparser.CountryFileParser;
import jlg.countryparser.CountryParserException;
import jlg.countryparser.GeoJsonCountryParser;
import jlg.geography.GeometryFeature;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GeoJsonParserTest {

    @Test
    public void should_parse_simple_countries_from_files(){
        //arrange
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = classLoader.getResource("test_import_countries_multiple_geojson.json").getPath();
        CountryFileParser countryParser = new GeoJsonCountryParser();

        //act
        List<BaseCountry> parsedCountries = null;
        try {
            parsedCountries = countryParser.parse(pathToFile);
        }
        catch (CountryParserException.FileParsingFailed e) {
            fail();
        }

        //assert
        int expectedCountries = 2;
        assertEquals(expectedCountries,parsedCountries.size());
        assertEquals("Albania",parsedCountries.get(0).getName());
        assertEquals("Andorra",parsedCountries.get(1).getName());
    }

    @Test(expected = CountryParserException.FileParsingFailed.class)
    public void when_exception_is_encountered_should_throw_parsing_exception() throws CountryParserException.FileParsingFailed {
        //arrange
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = classLoader.getResource("test_import_countries_bad_format_geojson.json").getPath();
        CountryFileParser countryParser = new GeoJsonCountryParser();

        //act
        List<BaseCountry> parsedCountries = countryParser.parse(pathToFile);;
    }

    @Test
    public void should_parse_single_polygon_countries_from_geoJson_file(){
        //arrange
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = classLoader.getResource("test_import_country_simple_geojson.json").getPath();
        CountryFileParser countryParser = new GeoJsonCountryParser();

        //act
        List<BaseCountry> parsedCountries = null;
        try {
            parsedCountries = countryParser.parse(pathToFile);
        }
        catch (CountryParserException.FileParsingFailed e) {
            fail();
        }

        //assert
        BaseCountry parsedCountry = parsedCountries.get(0);
        int expectedCountries = 1;
        String expectedWkt = "POLYGON((19.39732 42.31707,19.469709 42.39999,19.39732 42.31707))";

        assertEquals(expectedCountries,parsedCountries.size());
        assertEquals("Albania",parsedCountry.getName());
    }

    @Test
    public void should_parse_multi_polygon_countries_from_geoJson_file(){
        //arrange
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = classLoader.getResource("test_import_country_complex_geojson.json").getPath();
        CountryFileParser countryParser = new GeoJsonCountryParser();

        //act
        List<BaseCountry> parsedCountries = null;
        try {
            parsedCountries = countryParser.parse(pathToFile);
        }
        catch (CountryParserException.FileParsingFailed e) {
            fail();
        }

        //assert
        BaseCountry parsedCountry = parsedCountries.get(0);
        int expectedCountries = 1;
        String expectedWkt = "MULTIPOLYGON(((19.39732 42.31707,19.469709 42.39999,19.39732 42.31707))," +
                "((22.39732 42.31707,22.469709 42.39999,22.39732 42.31707)))";

        assertEquals(expectedCountries,parsedCountries.size());
        assertEquals("Croatia",parsedCountry.getName());
    }

    @Test
    public void should_parse_large_file_with_europe_countries() {
        //arrange
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = classLoader.getResource("europe_light.json").getPath();
        CountryFileParser countryParser = new GeoJsonCountryParser();

        //act
        List<BaseCountry> parsedCountries = null;
        try {
            parsedCountries = countryParser.parse(pathToFile);
        }
        catch (CountryParserException.FileParsingFailed e) {
            fail();
        }

        //assert
        int expectedCountries = 48;
        assertEquals(expectedCountries,parsedCountries.size());
    }

    @Test
    public void should_parse_countries_using_derived_country_objects(){
        //arrange
        ClassLoader classLoader = getClass().getClassLoader();
        String pathToFile = classLoader.getResource("test_import_country_simple_geojson.json").getPath();
        CountryFileParser countryParser = new GeoJsonCountryParser();

        //act
        List<CustomCountry> parsedCountries = null;
        try {
            //use lambda expressions to convert to desired custom type
            parsedCountries = countryParser.parse(pathToFile)
                    .stream()
                    .map(x -> new CustomCountry(x.getName(), x.getGeometryFeature()))
                    .collect(Collectors.toList());
        }
        catch (CountryParserException.FileParsingFailed e) {
            fail();
        }

        //assert
        CustomCountry parsedCountry = parsedCountries.get(0);
        int expectedCountries = 1;
        String expectedWkt = "POLYGON((19.39732 42.31707,19.469709 42.39999,19.39732 42.31707))";

        assertEquals(expectedCountries,parsedCountries.size());
        assertEquals("Albania",parsedCountry.getName());
        assertEquals("AL", parsedCountry.getAbbreviation());
        assertEquals(0, parsedCountry.getId());
    }

    class CustomCountry extends BaseCountry{
        private int id;    // for database usage
        private String abbreviation;

        public CustomCountry(String name, GeometryFeature geometryFeature) {
            super(name, geometryFeature);
            this.abbreviation = this.name.substring(0,2).toUpperCase();
        }

        public String getAbbreviation(){
            return abbreviation;
        }

        public int getId() {
            return id;
        }
    }
}
