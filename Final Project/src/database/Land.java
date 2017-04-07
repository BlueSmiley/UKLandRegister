package database;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Land
{

    public static final String DETACHED = "Detached";
    public static final String SEMI_DETACHED = "Semi-Detached";
    public static final String TERRACED = "Terraced";
    public static final String FLATS_MAISONETTES = "Flats/Maisonettes";
    public static final String OTHER = "Other";

    public static final String NEWLY_BUILT = "Newly Built";
    public static final String ESTABLISHED = "Established";

    private int price;
    private String dateOfSale, postcode, propertyType, condition, numberOrName, street, locality, town, district, county;
    private double latitude, longitude;

    public Land(int price, String date, String postcode, double latitude, double longitude,
                String type, String condition, String numberOrName, String street, String locality,
                String town, String district, String county)
    {
        this.price = price;
        this.dateOfSale = date;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.propertyType = type;
        this.condition = condition;
        this.numberOrName = numberOrName;
        this.street = street;
        this.locality = locality;
        this.town = town;
        this.district = district;
        this.county = county;
    }

    public HashMap<String, Object> getProperties()
    {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("Price", "£" + String.valueOf(price));
        map.put("Date Of Sale", Queries.formatDatabaseDate(dateOfSale));
        map.put("Postcode", postcode);
        map.put("Type", propertyType);
        map.put("Condition", condition);
        map.put("Number/Name", numberOrName);
        map.put("Street", street);
        map.put("Locality", locality);
        map.put("Town", town);
        map.put("District", district);
        map.put("County", county);
        return map;
    }

    @Override
    public String toString()
    {
        return "" + price + ",\n" + dateOfSale + ",\n" + postcode + ",\n" + propertyType
                + ",\n" + condition + ",\n" + numberOrName + " " + street + (!street.equals("") ?
                ",\n" :
                "") + locality + (!locality.equals("") ? ",\n" : "") + town + (!town.equals("") ?
                ",\n" :
                "") + district + (!district.equals("") ? ",\n" : "") + county + ". ";
    }

    public String getAddress()
    {
        String address = "";
        address += numberOrName + ", ";
        address += street + ", ";
        address += locality + ", ";
        address += town + ", ";
        address += district + ", ";
        address += county;

        return address;
    }

    public int getPrice()
    {
        return this.price;
    }

    public String getSaleDate()
    {
        return this.dateOfSale;
    }

    public String getPostCode()
    {
        return this.postcode;
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    public String getPropertyType()
    {
        return this.propertyType;
    }

    public String getOldOrNew()
    {
        return this.condition;
    }

    public String getNumberOrName()
    {
        return this.numberOrName;
    }

    public String getStreet()
    {
        return this.street;
    }

    public String getLocality()
    {
        return this.locality;
    }

    public String getTown()
    {
        return this.town;
    }

    public String getDistrict()
    {
        return this.district;
    }

    public String getCounty()
    {
        return this.county;
    }
}
