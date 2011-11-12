package edu.indiana.d2i.datacatalog.dashboard.xmccat;

import edu.indiana.d2i.datacatalog.dashboard.Constants;
import edu.indiana.d2i.datacatalog.dashboard.api.Point;
import edu.indiana.dde.catalog.catalogclient.CatalogServiceStub;
import edu.indiana.dde.metadata.catalog.domain.CatalogAggregationType;
import edu.indiana.dde.metadata.catalog.types.*;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.*;

public class XMCCatAPIWrapper {
    private static final Log log = LogFactory.getLog(XMCCatAPIWrapper.class);

    public static final String servicePath = "/axis2/services/CatalogService";

    public static final String HEADER_DN_NAMESPACE = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd}";
    public static final String HEADER_DN_NAME = "Username";
    public static final QName HEADER_QNAME = QName.valueOf(HEADER_DN_NAMESPACE + HEADER_DN_NAME);

    private Properties dashboardProperties;

    private String xmccatHost;
    private String xmccatPort;
    private String dn;
    private int clientTimeout;

    public XMCCatAPIWrapper(Properties dashProperties) {
        this.dashboardProperties = dashProperties;
        this.xmccatHost = ((String) dashProperties.get(Constants.PROP_XMCCAT_HOST)).trim();
        this.xmccatPort = ((String) dashProperties.get(Constants.PROP_XMCCAT_PORT)).trim();
        this.dn = ((String) dashProperties.get(Constants.PROP_XMCCAT_DN)).trim();
        this.clientTimeout = Integer.parseInt(((String) dashProperties.get(Constants.PROP_XMCCAT_SERVICE_CLIENT_TIMEOUT)).trim());
    }


    public List<String> getAvailableDataProducts() {
        try {
            List<String> dataProducts = new ArrayList<String>();

            CatalogServiceStub stub = getStub(xmccatHost, xmccatPort, dn, clientTimeout);
            QueryResponseDocument response = stub.query(getRequestForAvailableDataProducts());

            List<GuidResponseType> list = response.getQueryResponse().getQueryResponseSet().getGuidResponseList();

            for (GuidResponseType element : list) {
                dataProducts.add(element.getObjectId());
            }

            return dataProducts;
        } catch (AxisFault axisFault) {
            log.error("Error occurred while invoking XMCCat Service!", axisFault);
        } catch (RemoteException e) {
            log.error("Error occurred while invoking XMCCat Service!", e);
        }

        return Collections.emptyList();
    }

    public List<String> getCollectionsInArea(Collection<Point> polygonVertices) {
        try {
            List<String> collections = new ArrayList<String>();

            CatalogServiceStub stub = getStub(xmccatHost, xmccatPort, dn, clientTimeout);
            QueryResponseDocument response = stub.query(getRequestForCollectionInArea(polygonVertices));

            List<GuidResponseType> list = response.getQueryResponse().getQueryResponseSet().getGuidResponseList();

            for(GuidResponseType ele : list){
                collections.add(ele.getObjectId());
            }

            return collections;
        } catch (AxisFault axisFault) {
            log.error("Error occurred while invoking XMCCat API!", axisFault);
        } catch (RemoteException e) {
            log.error("Error occurred while invoking XMCCat API!", e);
        }

        return Collections.emptyList();
    }

    private ContextQueryRequestDocument getRequestForCollectionInArea(Collection<Point> polygonVertices) {
        ContextQueryRequestDocument contextQueryRequestDocument =
                ContextQueryRequestDocument.Factory.newInstance();
        ContextQueryRequestDocument.ContextQueryRequest contextQueryRequest =
                contextQueryRequestDocument.addNewContextQueryRequest();

        contextQueryRequest.setQueryTarget(getQueryTargetDocumentForCollectionInArea(polygonVertices).getQueryTarget());
        contextQueryRequest.setQueryResultFormat(getResultFormatForCollectionInArea());

        return contextQueryRequestDocument;
    }

    private QueryResultFormatType getResultFormatForCollectionInArea() {
        QueryResultFormatType resultFormat = QueryResultFormatType.Factory.newInstance();
        resultFormat.setCount(0);
        resultFormat.setOffset(0);

        resultFormat.setHierarchyFilter(HierarchyFilterType.TARGET);
        resultFormat.setContentFilter(ContentFilterType.ID_ONLY);

        resultFormat.setResultDeliveryMethod(CatalogDeliveryType.DIRECT);
        resultFormat.setErrorDeliveryMethod(CatalogDeliveryType.DIRECT);

        return resultFormat;
    }

    private QueryTargetDocument getQueryTargetDocumentForCollectionInArea(Collection<Point> polygonVertices) {
        QueryTargetDocument queryTargetDocument = QueryTargetDocument.Factory.newInstance();

        QueryObjectType queryObjectType = queryTargetDocument.addNewQueryTarget();
        queryObjectType.setAggrType(CatalogAggregationType.COLLECTION);

        QueryPropertyType queryPropertyType = queryObjectType.addNewQueryProperty();
        queryPropertyType.setName("spatialBounds");
        queryPropertyType.setSource("LEAD");
        QueryElementType queryElementType = queryPropertyType.addNewQueryElement();
        queryElementType.setName("boundingBox");
        queryElementType.setSource("LEAD");

        QueryPolygonElementType queryPolygonElementType = queryElementType.addNewQueryPolygonElement();
        queryPolygonElementType.setSpatialComparison(SpatialComparisonType.WITHIN);

        QueryXyPointType xyPoint = null;
        int index = 0;
        double xStart = 0.0;
        double yStart = 0.0;
        for (Point p : polygonVertices) {
            if (index == 0) {
                xStart = p.x;
                yStart = p.y;
            }
            xyPoint = queryPolygonElementType.addNewXyPoint();
            xyPoint.setXPos(BigDecimal.valueOf(p.x));
            xyPoint.setYPos(BigDecimal.valueOf(p.y));

            index++;
        }

        // Closing the polygon
        xyPoint = queryPolygonElementType.addNewXyPoint();
        xyPoint.setXPos(BigDecimal.valueOf(xStart));
        xyPoint.setYPos(BigDecimal.valueOf(yStart));

        return queryTargetDocument;
    }

    private ContextQueryRequestDocument getRequestForAvailableDataProducts() {
        ContextQueryRequestDocument requestDocument =
                ContextQueryRequestDocument.Factory.newInstance();

        ContextQueryRequestDocument.ContextQueryRequest request = requestDocument.addNewContextQueryRequest();

        request.setQueryTarget(getTargetDocumentForAvailableDataProductsQuery());
        request.setQueryResultFormat(getQueryResultFormatForAvailableDataProductsQuery());

        return requestDocument;
    }

    private QueryResultFormatType getQueryResultFormatForAvailableDataProductsQuery() {
        QueryResultFormatType resultFormat = QueryResultFormatType.Factory.newInstance();

        resultFormat.setCount(0);
        resultFormat.setOffset(0);

        resultFormat.setHierarchyFilter(HierarchyFilterType.TARGET);
        resultFormat.setContentFilter(ContentFilterType.ID_ONLY);

        resultFormat.setResultDeliveryMethod(CatalogDeliveryType.DIRECT);
        resultFormat.setErrorDeliveryMethod(CatalogDeliveryType.DIRECT);

        return resultFormat;
    }

    private QueryObjectType getTargetDocumentForAvailableDataProductsQuery() {
        QueryTargetDocument queryTargetDocument = QueryTargetDocument.Factory.newInstance();

        QueryObjectType queryObject = queryTargetDocument.addNewQueryTarget();
        queryObject.setAggrType(CatalogAggregationType.DATAPRODUCT);

        return queryTargetDocument.getQueryTarget();
    }

    private CatalogServiceStub getStub(String hostName, String port, String dn, Integer timeout) throws AxisFault {
        String target = "http://" + hostName + ":" + port + servicePath;

        CatalogServiceStub stub = new CatalogServiceStub(target);

        stub._getServiceClient().addStringHeader(HEADER_QNAME, dn);
        stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(timeout);

        return stub;
    }
}
