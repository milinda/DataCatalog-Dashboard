package edu.indiana.d2i.datacatalog.dashboard;

import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestKml {
    public static final String thousand = "1000m\n";
    public static final String twothousand = "2000m\n";
    public static final String threethousand = "3000m\n";
    public static final String fivethousand = "5000m\n";

    private static Map<String, Station> stationDetails = new HashMap<String, Station>();

    public static void main(String[] args) throws IOException {
        Kml nonFaa = Kml.unmarshal(new File("/Users/milindapathirage/Workspace/IU/data-catalog/dashboard/DataCatalog-Dashboard/datacatalog-dashboard/src/main/resources/NonFAA.kml"));
        Feature doc = nonFaa.getFeature();
        List<Feature> featuresInDoc = ((Document) doc).getFeature();
        List<Feature> featuresInFirstChildFolder = ((Folder) ((Folder) featuresInDoc.get(0)).getFeature().get(0)).getFeature();

        for (Feature f : featuresInFirstChildFolder) {
            String name = f.getName();
            List<Feature> stations = ((Folder) f).getFeature();
            for (Feature station : stations) {
                String desc = station.getDescription();
                Pattern stattionId = Pattern.compile("Station\\sID:\\s(.*)\\n");
                Matcher matcher = stattionId.matcher(desc);
                String stationIdStr = "";
                if (matcher.find()) {
                    stationIdStr = matcher.group(1);
                }

                Pattern latPattern = Pattern.compile("Latitude:\\s(\\d+\\.\\d+)\\n");
                Pattern lonPattern = Pattern.compile("Longitude:\\s*(\\-?\\d+\\.\\d+).*");

                matcher = latPattern.matcher(desc);
                String latitude = "";
                String longitude = "";
                if(matcher.find()){
                    latitude = matcher.group(1);
                }

                Matcher m = lonPattern.matcher(desc);
                if(m.find()){
                    longitude = m.group(1);
                }

                LinearRing lr = ((Polygon) ((Placemark) station).getGeometry()).getOuterBoundaryIs().getLinearRing();
                Station stationDetail = null;
                if (stationDetails.get(stationIdStr) == null) {
                    stationDetail = new Station();
                    stationDetails.put(stationIdStr, stationDetail);
                } else {
                    stationDetail = stationDetails.get(stationIdStr);
                }

                if(stationDetail.getLat() == 0.0){
                    stationDetail.setLat(Double.parseDouble(latitude));
                }

                if(stationDetail.getLon() == 0.0){
                    stationDetail.setLon(Double.parseDouble(longitude));
                }

                if (name.equals(thousand)) {
                    stationDetail.setThousand(lr);
                } else if (name.equals(twothousand)) {
                    stationDetail.setTwothousand(lr);
                } else if (name.equals(threethousand)) {
                    stationDetail.setThreeThousand(lr);
                } else if (name.equals(fivethousand)) {
                    stationDetail.setFiveThousand(lr);
                }
            }
        }

        System.out.println("Done...");
    }
}
