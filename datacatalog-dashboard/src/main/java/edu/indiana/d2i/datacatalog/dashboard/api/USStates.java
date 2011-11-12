/**
 *  Copyright (c) 2011, Data to Insight Center. (http://pti.iu.edu/d2i) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.indiana.d2i.datacatalog.dashboard.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class USStates extends HttpServlet {
    private static Log log = LogFactory.getLog(USStates.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // TODO: Cache the states.
        response.setContentType("application/json");

        try {
            String statesPath = getServletContext().getRealPath("/WEB-INF/resources/states.xml");
            response.getWriter().write(getStates(statesPath).toString());
        } catch (IOException e) {
            log.error("Error while reading states file.", e);
            throw e;
        } catch (ParserConfigurationException e) {
            log.error("Error while reading states file.", e);
            throw new ServletException(e);
        } catch (SAXException e) {
            log.error("Error while reading states file.", e);
            throw new ServletException(e);
        }
    }

    public static String getStates(String statesFilePath) throws ParserConfigurationException, IOException, SAXException {
        JSONObject statesFeatureCollection = new JSONObject();
        statesFeatureCollection.put("type", "FeatureCollection");
        JSONArray features = new JSONArray();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document dom = documentBuilder.parse(new FileInputStream(new File(statesFilePath)));
            Element docElement = dom.getDocumentElement();
            NodeList states = docElement.getElementsByTagName("state");
            for (int i = 0; i < states.getLength(); i++) {
                Node state = states.item(i);
                JSONObject stateObj = new JSONObject();
                stateObj.put("type", "Feature");
                JSONObject geometry = new JSONObject();
                geometry.put("type", "Polygon");
                JSONArray coordinates = new JSONArray();
                JSONArray coordinateSub = new JSONArray();
                NodeList points = ((Element) state).getElementsByTagName("point");
                for (int j = 0; j < points.getLength(); j++) {
                    Node point = points.item(j);
                    JSONArray pointObj = new JSONArray();
                    float lat = Float.parseFloat(((Element) point).getAttribute("lat"));
                    float lng = Float.parseFloat(((Element) point).getAttribute("lng"));
                    double trLng = lng * 20037508.34 / 180;
                    double trLat = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
                    pointObj.add(lng);
                    pointObj.add(lat);
                    coordinateSub.add(pointObj);
                }
                geometry.put("coordinates", coordinates);
                coordinates.add(coordinateSub);
                stateObj.put("geometry", geometry);
                JSONObject name = new JSONObject();
                name.put("Name", ((Element) state).getAttribute("name"));
                name.put("colour", "#FFF901");
                stateObj.put("properties", name);
                features.add(stateObj);
            }
            statesFeatureCollection.put("features", features);
            return statesFeatureCollection.toJSONString();
        } catch (ParserConfigurationException e) {
            log.error("Error while processing states.xml.", e);
            throw e;
        } catch (FileNotFoundException e) {
            log.error("Error while processing states.xml.", e);
            throw e;
        } catch (SAXException e) {
            log.error("Error while processing states.xml.", e);
            throw e;
        } catch (IOException e) {
            log.error("Error while processing states.xml.", e);
            throw e;
        }
    }

}
