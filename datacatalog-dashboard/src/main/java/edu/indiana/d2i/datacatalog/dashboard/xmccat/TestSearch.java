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

package edu.indiana.d2i.datacatalog.dashboard.xmccat;

import edu.indiana.dde.catalog.catalogclient.CatalogServiceStub;
import edu.indiana.dde.metadata.catalog.domain.CatalogAggregationType;
import edu.indiana.dde.metadata.catalog.types.*;
import org.apache.xmlbeans.XmlString;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.xml.namespace.QName;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestSearch {
    public static final String servicePath = "/axis2/services/CatalogService";

    //Operation User DN Header Constants
    // These constants are used to build the QName to extract the user's DN from the soap header of the request
    public static final String HEADER_DN_NAMESPACE = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd}";
    public static final String HEADER_DN_NAME = "Username";
    public static final QName HEADER_QNAME = QName.valueOf(HEADER_DN_NAMESPACE + HEADER_DN_NAME);

    public static final String hostName = "coffeetree.cs.indiana.edu";
    //public static final String hostName = "localhost";
    public static final String port = "10081";
    public static final String delivery = "DIRECT";
    public static final String dn = "public";

    public static void main(String[] args) {
        TestSearch ts = new TestSearch();
        ts.checkStatus();
        ts.testContextQuery();
    }

    public void checkStatus() {
        String dn = "public";

        try {
            CatalogServiceStub stub = getStub(hostName, port, dn);

            CheckStatusRequestDocument doc = CheckStatusRequestDocument.Factory.newInstance();
            CheckStatusRequestDocument.CheckStatusRequest request = doc.addNewCheckStatusRequest();
            request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(delivery));
            CheckStatusResponseDocument myResponse = stub.checkStatus(doc);
            System.out.println(myResponse.getCheckStatusResponse().getCatalogOperationStatus().getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllWorkspace() {
        try {
            CatalogServiceStub stub = getStub(hostName, port, dn);
            AllWorkspaceQueryRequestDocument requestDocument = AllWorkspaceQueryRequestDocument.Factory.newInstance();
            AllWorkspaceQueryRequestDocument.AllWorkspaceQueryRequest request = requestDocument.addNewAllWorkspaceQueryRequest();
            QueryResultFormatType resultFormatType = request.addNewQueryResultFormat();
            resultFormatType.setCount(0);
            resultFormatType.setOffset(0);
            resultFormatType.setHierarchyFilter(HierarchyFilterType.TARGET);
            resultFormatType.setContentFilter(ContentFilterType.ID_ONLY);

            resultFormatType.setResultDeliveryMethod(CatalogDeliveryType.DIRECT);
            resultFormatType.setErrorDeliveryMethod(CatalogDeliveryType.DIRECT);

            QueryResponseDocument responseDocument = stub.allWorkspaceQuery(requestDocument);

            FileWriter fstream = new FileWriter("out.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(responseDocument.toString());
            out.close();
            //System.out.println(responseDocument.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAvailableDataProducts() {
        try {
            CatalogServiceStub stub = getStub(hostName, port, dn);

            ContextQueryRequestDocument contextQueryRequestDocument =
                    ContextQueryRequestDocument.Factory.newInstance();
            ContextQueryRequestDocument.ContextQueryRequest contextQueryRequest = contextQueryRequestDocument.addNewContextQueryRequest();
            contextQueryRequest.setQueryTarget(getAvailableDataProductsTargetDocument());

            QueryResultFormatType resultFormat = QueryResultFormatType.Factory.newInstance();
            resultFormat.setCount(0);
            resultFormat.setOffset(0);

            resultFormat.setHierarchyFilter(HierarchyFilterType.TARGET);
            resultFormat.setContentFilter(ContentFilterType.ID_ONLY);

            resultFormat.setResultDeliveryMethod(CatalogDeliveryType.DIRECT);
            resultFormat.setErrorDeliveryMethod(CatalogDeliveryType.DIRECT);

            contextQueryRequest.setQueryResultFormat(resultFormat);

            System.out.println(contextQueryRequestDocument.toString());
            QueryResponseDocument response = stub.query(contextQueryRequestDocument);

            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QueryObjectType getAvailableDataProductsTargetDocument(){
        QueryTargetDocument queryTargetDocument = QueryTargetDocument.Factory.newInstance();

        QueryObjectType queryObject = queryTargetDocument.addNewQueryTarget();
        queryObject.setAggrType(CatalogAggregationType.DATAPRODUCT);

        return queryTargetDocument.getQueryTarget();
    }

    public void testContextQuery() {
        try {
            CatalogServiceStub stub = getStub(hostName, port, dn);
            ContextQueryRequestDocument contextQueryRequestDocument =
                    ContextQueryRequestDocument.Factory.newInstance();
            ContextQueryRequestDocument.ContextQueryRequest contextQueryRequest = contextQueryRequestDocument.addNewContextQueryRequest();
            contextQueryRequest.setQueryTarget(getTarget().getQueryTarget());

            QueryResultFormatType resultFormatType = QueryResultFormatType.Factory.newInstance();
            resultFormatType.setOffset(0);
            resultFormatType.setCount(10);

            resultFormatType.setHierarchyFilter(HierarchyFilterType.CHILDREN);
            resultFormatType.setContentFilter(ContentFilterType.ID_ONLY);

            resultFormatType.setResultDeliveryMethod(CatalogDeliveryType.DIRECT);
            resultFormatType.setErrorDeliveryMethod(CatalogDeliveryType.DIRECT);

            contextQueryRequest.setQueryResultFormat(resultFormatType);

            System.out.println(contextQueryRequestDocument.toString());
            QueryResponseDocument response = stub.query(contextQueryRequestDocument);

            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QueryTargetDocument getSearchNameTarget() throws ParseException {
        QueryTargetDocument queryTargetDocument = QueryTargetDocument.Factory.newInstance();

        QueryObjectType queryObjectType = queryTargetDocument.addNewQueryTarget();
        queryObjectType.setAggrType(CatalogAggregationType.COLLECTION);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd");

        Calendar end = Calendar.getInstance();
        end.setTime(formatter.parse("2011/11/22"));

        Calendar start = Calendar.getInstance();
        start.setTime(formatter.parse("2011/11/21"));

        //queryObjectType.setCreateDateEnd(end);
        //queryObjectType.setCreateDateStart(start);

        QueryPropertyType queryPropertyType = queryObjectType.addNewQueryProperty();

        return queryTargetDocument;
    }


    private QueryTargetDocument getTarget() throws ParseException {
        QueryTargetDocument queryTargetDocument = QueryTargetDocument.Factory.newInstance();

        QueryObjectType queryObjectType = queryTargetDocument.addNewQueryTarget();
        queryObjectType.setAggrType(CatalogAggregationType.FILE);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd");

        Calendar end = Calendar.getInstance();
        end.setTime(formatter.parse("2011/11/21"));

        Calendar start = Calendar.getInstance();
        start.setTime(formatter.parse("2011/11/22"));

        //queryObjectType.setCreateDateEnd(end);
        //queryObjectType.setCreateDateStart(start);


        QueryPropertyType queryPropertyType = queryObjectType.addNewQueryProperty();
        queryPropertyType.setName("bounding");
        queryPropertyType.setSource("LEAD");
        QueryElementType queryElementType = queryPropertyType.addNewQueryElement();
        queryElementType.setName("boundingBox");
        queryElementType.setSource("LEAD");
        QueryPolygonElementType queryPolygonElementType = queryElementType.addNewQueryPolygonElement();

        QueryXyPointType xyPoint = queryPolygonElementType.addNewXyPoint();
        xyPoint.setXPos(BigDecimal.valueOf(-120));
        xyPoint.setYPos(BigDecimal.valueOf(51));

        xyPoint = queryPolygonElementType.addNewXyPoint();
        xyPoint.setXPos(BigDecimal.valueOf(-125));
        xyPoint.setYPos(BigDecimal.valueOf(51));

        xyPoint = queryPolygonElementType.addNewXyPoint();
        xyPoint.setXPos(BigDecimal.valueOf(-125));
        xyPoint.setYPos(BigDecimal.valueOf(42));

        xyPoint = queryPolygonElementType.addNewXyPoint();
        xyPoint.setXPos(BigDecimal.valueOf(-120));
        xyPoint.setYPos(BigDecimal.valueOf(42));

        queryPolygonElementType.setSpatialComparison(SpatialComparisonType.CONTAINS);

        return queryTargetDocument;
    }

    /**
     * This method is called to create the stub. If any exceptions are thrown, they are
     * passed up to the method that called getStub.
     *
     * @param hostName String with the name of the host where the service is located.
     * @param portNum  String with the port number on the host.
     * @param myDn     String with the user's DN if the DN should be put into the
     *                 stub's header.  If not, then pass null for this parameter.
     * @return CatalogServiceStub
     * @throws Exception
     */
    public static CatalogServiceStub getStub(String hostName, String portNum, String myDn) throws Exception {
        String target = "http://" + hostName + ":" + portNum + servicePath;
        CatalogServiceStub stub = new CatalogServiceStub(target);
        if (stub != null) {
            stub._getServiceClient().addStringHeader(HEADER_QNAME, myDn);
            stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(240000);
        }
        return stub;
    }
}
