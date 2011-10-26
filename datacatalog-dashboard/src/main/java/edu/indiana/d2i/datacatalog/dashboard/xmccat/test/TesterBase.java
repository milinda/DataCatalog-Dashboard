package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import javax.xml.namespace.QName;
import edu.indiana.dde.catalog.catalogclient.CatalogServiceStub;

/**
 * This abstract base class is extended by all of the test programs.  The constants
 * for the QName used to pass the user's DN in the header of the request is handled
 * in this super class.  This class has only one method - getStub, which is used to
 * get the XMC Cat client stub.
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public abstract class TesterBase {
	// path to service after host and port
	public static final String servicePath = "/axis2/services/CatalogService";
	
	//Operation User DN Header Constants
	// These constants are used to build the QName to extract the user's DN from the soap header of the request
	public static final String HEADER_DN_NAMESPACE = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd}";
	public static final String HEADER_DN_NAME = "Username";
	public static final QName HEADER_QNAME = QName.valueOf(HEADER_DN_NAMESPACE + HEADER_DN_NAME);
	
	public TesterBase() {} //nothing yet in the super constructor
	
	
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
	
} //end of TesterBase
