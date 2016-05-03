package course.examples.lgbtsafespaces;

/**
 * Created by Nicolas on 4/17/2016.
 */
public class LGBTLocation {
    String locationName;
    double lng;
    double lat;
    boolean verifiedSafeSpace;
    boolean genderNeutralBathroom;
    boolean crisisCenter;
    boolean shelter;
    boolean friendlyBusiness;

    public LGBTLocation(String locationName, double lng, double lat) {
        this.locationName = locationName;
        this.lng = lng;
        this.lat = lat;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public boolean isVerifiedSafeSpace() {
        return verifiedSafeSpace;
    }

    public boolean isGenderNeutralBathroom() {
        return genderNeutralBathroom;
    }

    public boolean isCrisisCenter() {
        return crisisCenter;
    }

    public boolean isShelter() {
        return shelter;
    }

    public boolean isFriendlyBusiness() {
        return friendlyBusiness;
    }
}
