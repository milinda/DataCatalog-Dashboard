package edu.indiana.d2i.datacatalog.dashboard.api;

import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import edu.indiana.d2i.datacatalog.dashboard.Constants;
import edu.indiana.d2i.datacatalog.dashboard.Station;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NEXRADRadars extends HttpServlet {
    private static Log log = LogFactory.getLog(NEXRADRadars.class);

    public static final String URL_PARAM_STATION_ID = "stationId";
    private static Map<String, Station> stationDetails = new HashMap<String, Station>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String stationId = req.getParameter(URL_PARAM_STATION_ID);
        String stationIdLastPart = "";

        if(stationId == null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Stations ID null.\"}");
            return;
        }

        if(stationId.startsWith("NWS/NEXRAD2")){
            int lastIndexOfFwdSlash = stationId.lastIndexOf("/");
            stationIdLastPart = stationId.substring(lastIndexOfFwdSlash + 1);
        }

        Station st = stationDetails.get(stationIdLastPart);


        if (st != null) {
            JSONObject station = new JSONObject();
            station.put("type", "Feature");

            JSONObject pointGeometry = new JSONObject();
            pointGeometry.put("type", "Point");
            JSONArray pointCoordinates = new JSONArray();
            pointCoordinates.add(st.getLon());
            pointCoordinates.add(st.getLat());
            pointGeometry.put("coordinates", pointCoordinates);

            station.put("geometry", pointGeometry);
            JSONObject props = new JSONObject();
            props.put("Name", stationId);
            station.put("properties", props);
            log.info(station.toJSONString());
            resp.setContentType("application/json");
            resp.getWriter().write(station.toJSONString());
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().println("<html><body><p>Cannot find radar station <b>" + stationId + "</b>.</p></body></html>");
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();

        String nonFAAKmlPath = getServletContext().getRealPath("/WEB-INF/resources/NonFAA.kml");
        Kml nonFAA = Kml.unmarshal(new File(nonFAAKmlPath));

        Feature doc = nonFAA.getFeature();
        List<Feature> featuresInDoc = ((Document) doc).getFeature();
        List<Feature> featuresInFirstChildFolder = ((Folder) ((Folder) featuresInDoc.get(0)).getFeature().get(0)).getFeature();

        for (Feature f : featuresInFirstChildFolder) {
            String name = f.getName();
            List<Feature> stations = ((Folder) f).getFeature();
            for (Feature station : stations) {
                String desc = station.getDescription();
                Pattern stationId = Pattern.compile("Station\\sID:\\s(.*)\\n");
                Matcher matcher = stationId.matcher(desc);
                String stationIdStr = "";
                if (matcher.find()) {
                    stationIdStr = matcher.group(1);
                }

                Pattern latPattern = Pattern.compile("Latitude:\\s(\\-?\\d+\\.\\d+)\\n");
                Pattern lonPattern = Pattern.compile("Longitude:\\s*(\\-?\\d+\\.\\d+)\\n");

                matcher = latPattern.matcher(desc);
                String latitude = "";
                String longitude = "";
                if (matcher.find()) {
                    latitude = matcher.group(1);
                }

                matcher = lonPattern.matcher(desc);
                if (matcher.find()) {
                    longitude = matcher.group(1);
                }

                LinearRing lr = ((Polygon) ((Placemark) station).getGeometry()).getOuterBoundaryIs().getLinearRing();
                Station stationDetail = null;
                if (stationDetails.get(stationIdStr) == null) {
                    stationDetail = new Station();
                    stationDetails.put(stationIdStr, stationDetail);
                } else {
                    stationDetail = stationDetails.get(stationIdStr);
                }

                if (stationDetail.getLat() == 0.0 && !latitude.equals("")) {
                    double lat = Float.parseFloat(latitude);
                    double trLat = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
                    stationDetail.setLat(lat);
                }

                if (stationDetail.getLon() == 0.0 && !longitude.equals("")) {
                    double lng = Float.parseFloat(longitude);
                    double trLng = lng * 20037508.34 / 180;
                    stationDetail.setLon(lng);
                }

                if (name.equals(Constants.NEXRAD_RADIUS_ONE_THOUSAND)) {
                    stationDetail.setThousand(lr);
                } else if (name.equals(Constants.NEXRAD_RADIUS_TWO_THOUSAND)) {
                    stationDetail.setTwothousand(lr);
                } else if (name.equals(Constants.NEXRAD_RADIUS_THREE_THOUSAND)) {
                    stationDetail.setThreeThousand(lr);
                } else if (name.equals(Constants.NEXRAD_RADIUS_FIVE_THOUSAND)) {
                    stationDetail.setFiveThousand(lr);
                }
            }
        }

    }


}
