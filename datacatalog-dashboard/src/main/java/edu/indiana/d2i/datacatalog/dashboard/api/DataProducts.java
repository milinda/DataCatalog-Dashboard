package edu.indiana.d2i.datacatalog.dashboard.api;

import edu.indiana.d2i.datacatalog.dashboard.Constants;
import edu.indiana.d2i.datacatalog.dashboard.api.beans.*;
import edu.indiana.d2i.datacatalog.dashboard.api.beans.Collections;
import edu.indiana.d2i.datacatalog.dashboard.xmccat.XMCCatAPIWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeoFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.PolicyNode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Path("/dataproducts")
public class DataProducts {
    private static final Log log = LogFactory.getLog(DataProducts.class);

    @Context
    ServletContext context;

    @POST
    @Path("inArea")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Collections getDataProductsInTheSelectedArea(Polygon polygon) throws IOException {
        String confFilePath = context.getRealPath("/WEB-INF/conf/dashboard-conf.properties");
        Properties dashboardProps = new Properties();

        try {
            dashboardProps.load(new FileInputStream(new File(confFilePath)));
        } catch (IOException e) {
            log.error("Cannot create dashboard properties object!!", e);
            throw e;
        }
        XMCCatAPIWrapper xmcCatAPIWrapper = new XMCCatAPIWrapper(dashboardProps);

        Collections collections = new edu.indiana.d2i.datacatalog.dashboard.api.beans.Collections();
        collections.setCollections(xmcCatAPIWrapper.getCollectionsInArea(polygon.getPoints()));

        return collections;
    }

    @GET
    @Path("summary")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCollectionsAndFileSummary() throws IOException {
        JSONObject summary = new JSONObject();
        // Fill DB query

        summary.put("collections", getCollectionCount());
        summary.put("files", getFileCount());
        return summary.toJSONString();
    }

    private int getCollectionCount() throws IOException {
        Connection conn = null;

        String confFilePath = context.getRealPath("/WEB-INF/conf/dashboard-conf.properties");
        Properties dashboardProps = new Properties();

        try {
            dashboardProps.load(new FileInputStream(new File(confFilePath)));
        } catch (IOException e) {
            log.error("Cannot create dashboard properties object!!", e);
            throw e;
        }

        try {
            String userName = (String) dashboardProps.get(Constants.PROP_DATACAT_DB_USER);
            String password = (String) dashboardProps.get(Constants.PROP_DATACAT_DB_PW);
            //String url = "jdbc:mysql://coffeetree.cs.indiana.edu:3306/datacat";
            String url = (String) dashboardProps.get(Constants.PROP_DATACAT_DB_URL);
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(`Collection_id`) FROM `mcs_collection`");
            rs.next();
            int collectionCount = rs.getInt(1);
            rs.close();
            return collectionCount;
        } catch (Exception e) {
            log.error("Cannot connect to database server", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) { /* ignore close errors */ }
            }
        }

        return 0;
    }

    private int getFileCount() throws IOException {
        Connection conn = null;

        String confFilePath = context.getRealPath("/WEB-INF/conf/dashboard-conf.properties");
        Properties dashboardProps = new Properties();

        try {
            dashboardProps.load(new FileInputStream(new File(confFilePath)));
        } catch (IOException e) {
            log.error("Cannot create dashboard properties object!!", e);
            throw e;
        }

        try {
            String userName = (String) dashboardProps.get(Constants.PROP_DATACAT_DB_USER);
            String password = (String) dashboardProps.get(Constants.PROP_DATACAT_DB_PW);
            //String url = "jdbc:mysql://coffeetree.cs.indiana.edu:3306/datacat";
            String url = (String) dashboardProps.get(Constants.PROP_DATACAT_DB_URL);
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(`Data_id`) FROM `mcs_logical_file`");
            rs.next();
            int collectionCount = rs.getInt(1);
            rs.close();
            return collectionCount;
        } catch (Exception e) {
            log.error("Cannot connect to database server", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) { /* ignore close errors */ }
            }
        }

        return 0;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AvailableDataProducts getDataProducts() throws IOException {
        String confFilePath = context.getRealPath("/WEB-INF/conf/dashboard-conf.properties");
        Properties dashboardProps = new Properties();

        try {
            dashboardProps.load(new FileInputStream(new File(confFilePath)));
        } catch (IOException e) {
            log.error("Cannot create dashboard properties object!!", e);
            throw e;
        }

        XMCCatAPIWrapper xmcCatAPIWrapper = new XMCCatAPIWrapper(dashboardProps);
        List<String> dataProducts = xmcCatAPIWrapper.getAvailableDataProducts();

        AvailableDataProducts availableDataProducts = new AvailableDataProducts();

        for (String dataProduct : dataProducts) {
            DataProduct dp = new DataProduct();
            dp.setName(dataProduct);
            availableDataProducts.addDataProduct(dp);
        }

        return availableDataProducts;
    }


}
