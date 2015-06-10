package jlg.countryparser;

import jlg.geography.GeometryFeature;

import static jlg.codecontract.CodeContract.verifyNotNull;

/**
 * Basic representation of a country. Can be inherited to addapt to the need of
 * various applications
 */
public class BaseCountry {
    protected String name;
    protected GeometryFeature geometryFeature;

    public BaseCountry(String name, GeometryFeature geometryFeature){
        verifyNotNull(name, "Can not create country with NULL name.");
        verifyNotNull(geometryFeature, "Can not create country with NULL geometry.");

        this.name = name;
        this.geometryFeature = geometryFeature;
    }

    public String getName() {
        return name;
    }

    public GeometryFeature getGeometryFeature() {
        return geometryFeature;
    }
}
