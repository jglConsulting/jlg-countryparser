package jlg.countryparser;

import jlg.geography.GeometryFeature;
import jlg.geography.wsg84.GeographicMultiPolygon;
import jlg.geography.wsg84.GeographicPolygon;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.util.*;

public class GeoJsonCountryParser implements CountryFileParser {
    private final String GEO_JSON_POLYGON_TYPE = "Polygon";
    private final String GEO_JSON_MULTIPOLYGON_TYPE = "MultiPolygon";
    private final String GEO_JSON_FEATURE_TYPE_KEY = "type";
    private final String GEO_JSON_GEOMETRY_KEY = "geometry";
    private final String GEO_JSON_COORDINATES_KEY = "coordinates";
    private final String GEO_JSON_COUNTRY_NAME_KEY = "NAME";
    private final String GEO_JSON_FEATURE_PROPERTIES_KEY = "properties";


    @Override
    public List<BaseCountry> parse(String filePath) throws CountryParserException.FileParsingFailed {
        List<BaseCountry> parsedCountries = new LinkedList<>();
        try {
            File file = new File(filePath);
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> parsedFileResult = mapper.readValue(file, Map.class);
            List<LinkedHashMap<String,Object>> extractedFeatures =
                    ( ArrayList<LinkedHashMap<String,Object>>)parsedFileResult.values().toArray()[1];

            //for each country found in the file [features collection]
            for(LinkedHashMap<String,Object> feature:extractedFeatures){
                BaseCountry country = parseCountryFromFeature(feature);
                parsedCountries.add(country);
            }
        }
        catch(Exception ex){
            throw new CountryParserException.FileParsingFailed("Could not parse file " + filePath, ex);
        }

        return parsedCountries;
    }


    private BaseCountry parseCountryFromFeature(LinkedHashMap<String, Object> feature) throws CountryParserException.FileParsingFailed {
        //extract country name
        LinkedHashMap<String,Object> properties = (LinkedHashMap<String,Object>)feature.get(GEO_JSON_FEATURE_PROPERTIES_KEY);
        String countryName = (String)properties.get(GEO_JSON_COUNTRY_NAME_KEY);

        //extract country coordinates
        LinkedHashMap<String,Object> geometry = (LinkedHashMap<String,Object>)feature.get(GEO_JSON_GEOMETRY_KEY);
        String featureType = geometry.get(GEO_JSON_FEATURE_TYPE_KEY).toString();

        switch (featureType){
            case GEO_JSON_POLYGON_TYPE:{
                double[] countryCoordinates = parseSinglePolygonCountryCoordinates(geometry);

                GeometryFeature countryGeometry = new GeographicPolygon(countryCoordinates);
                BaseCountry country = new BaseCountry(countryName,countryGeometry);
                return country;
            }
            case GEO_JSON_MULTIPOLYGON_TYPE:{
                List<List<Double>> countryCoordinates = parseMultiPolygonCountryCoordinates(geometry);
                GeometryFeature countryGeometry = new GeographicMultiPolygon(countryCoordinates);
                BaseCountry country = new BaseCountry(countryName,countryGeometry);
                return country;
            }
            default:{
                throw new CountryParserException.FileParsingFailed("Can not parse WKT feature. Only polygon and multi-polygon are supported. Feature type: " + featureType);
            }
        }
    }

    private double[] parseSinglePolygonCountryCoordinates(LinkedHashMap<String, Object> geometry) {
        List<Double> countryCoordinates = new ArrayList<Double>();
        ArrayList<Object> coordinates = (ArrayList<Object>)((ArrayList<Object>)geometry.get(GEO_JSON_COORDINATES_KEY)).get(0);
        for(Object coordinate:coordinates){
            ArrayList<Double> parsedCoordinate = (ArrayList<Double>)coordinate;
            Double latitude = parsedCoordinate.get(1);
            Double longitude = parsedCoordinate.get(0);
            countryCoordinates.add(latitude);
            countryCoordinates.add(longitude);
        }

        double[] coordinatesAsArray = new double[countryCoordinates.size()];
        for(int i=0; i<coordinates.size(); i++){
            coordinatesAsArray[i] = countryCoordinates.get(i);
        }

        return coordinatesAsArray;
    }

    private List<List<Double>> parseMultiPolygonCountryCoordinates(LinkedHashMap<String, Object> geometry){
        ArrayList<Object> polygons = (ArrayList<Object>)((ArrayList<Object>)geometry.get(GEO_JSON_COORDINATES_KEY));
        List<List<Double>> countryCoordinates = new ArrayList<List<Double>>();
        for(Object polygon: polygons){

            List<Double> countryPartCoordinates = new ArrayList<Double>();
            ArrayList<Double> coordinates = ((ArrayList<ArrayList<Double>>)polygon).get(0);
            for(Object coordinate:coordinates){
                ArrayList<Double> parsedCoordinate = (ArrayList<Double>)coordinate;
                Double latitude = parsedCoordinate.get(0);
                Double longitude = parsedCoordinate.get(1);

                if(longitude < 0){
                    //in geo JSON, for countries with negative longitude, the coordinates are ivnersed
                    countryPartCoordinates.add(latitude);
                    countryPartCoordinates.add(longitude);
                }
                else{
                    countryPartCoordinates.add(longitude);
                    countryPartCoordinates.add(latitude);
                }
            }
            countryCoordinates.add(countryPartCoordinates);
        }

        return countryCoordinates;
    }
}
