package jlg.countryparser;

public class CountryParserException {
    public static class FileParsingFailed extends Exception{
        public FileParsingFailed(){
            super();
        }

        public FileParsingFailed(String msg){
            super(msg);
        }

        public FileParsingFailed(String msg, Exception e){
            super(msg, e);
        }
    }

    public static class InvalidGeoJsonFeatureType extends Exception{
        public InvalidGeoJsonFeatureType(){
            super();
        }

        public InvalidGeoJsonFeatureType(String msg){
            super(msg);
        }

        public InvalidGeoJsonFeatureType(String msg, Exception e){
            super(msg, e);
        }
    }
}
