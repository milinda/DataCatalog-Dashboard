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

import javax.xml.namespace.QName;

public class TestSearch {
    public static final String servicePath = "/axis2/services/CatalogService";

	//Operation User DN Header Constants
	// These constants are used to build the QName to extract the user's DN from the soap header of the request
	public static final String HEADER_DN_NAMESPACE = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd}";
	public static final String HEADER_DN_NAME = "Username";
	public static final QName HEADER_QNAME = QName.valueOf(HEADER_DN_NAMESPACE + HEADER_DN_NAME);

    /**
	 * This method is called to create the stub. If any exceptions are thrown, they are
	 * passed up to the method that called getStub.
	 * @param hostName   String with the name of the host where the service is located.
	 * @param portNum    String with the port number on the host.
	 * @param myDn       String with the user's DN if the DN should be put into the
	 *                   stub's header.  If not, then pass null for this parameter.
	 * @return           CatalogServiceStub
	 * @throws Exception
	 */
	public static CatalogServiceStub getStub(String hostName, String portNum, String myDn) throws Exception {
		String target = "http://" + hostName + ":" + portNum + servicePath;
		CatalogServiceStub stub = new CatalogServiceStub(target);
		if (myDn != null)
			stub._getServiceClient().addStringHeader(HEADER_QNAME, myDn);
		return (stub);
	} //end of getStub
}
