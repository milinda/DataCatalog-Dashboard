package edu.indiana.d2i.datacatalog.dashboard;

import de.micromata.opengis.kml.v_2_2_0.LinearRing;

public class Station {

    private double lat = 0.0;
    private double lon = 0.0;
    private LinearRing thousand;
    private LinearRing twothousand;
    private LinearRing threeThousand;
    private LinearRing fiveThousand;

    public Station(double lat, double lon, LinearRing thousand, LinearRing twothousand, LinearRing threeThousand, LinearRing fiveThousand) {
        this.lat = lat;
        this.lon = lon;
        this.thousand = thousand;
        this.twothousand = twothousand;
        this.threeThousand = threeThousand;
        this.fiveThousand = fiveThousand;
    }

    public Station(){

    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setThousand(LinearRing thousand) {
        this.thousand = thousand;
    }

    public void setTwothousand(LinearRing twothousand) {
        this.twothousand = twothousand;
    }

    public void setThreeThousand(LinearRing threeThousand) {
        this.threeThousand = threeThousand;
    }

    public void setFiveThousand(LinearRing fiveThousand) {
        this.fiveThousand = fiveThousand;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public LinearRing getThousand() {
        return thousand;
    }

    public LinearRing getTwothousand() {
        return twothousand;
    }

    public LinearRing getThreeThousand() {
        return threeThousand;
    }

    public LinearRing getFiveThousand() {
        return fiveThousand;
    }
}
