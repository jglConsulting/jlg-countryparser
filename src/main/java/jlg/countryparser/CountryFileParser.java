package jlg.countryparser;

import java.util.List;

public interface CountryFileParser {
    public List<BaseCountry> parse(String filePath) throws CountryParserException.FileParsingFailed;
}
