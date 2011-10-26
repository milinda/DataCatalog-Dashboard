package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import javax.xml.namespace.QName;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.catalog.catalogclient.*;

import org.apache.axis2.AxisFault;
//import org.apache.axis2.client.ServiceClient;
//import org.apache.axis2.description.AxisService;

/**
 * This small test program is for the User Exists operation.  See the 
 * usage method or execute with --help as the only parameter.
 *  
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class TestUserExists extends TesterBase {
	
	/**
	 * @param args
	 * @throws AxisFault 
	 */
	public static void main(String[] args) throws Exception {
		String myDn = null;
		String hostName = "localhost";
		String portNum = "8080";
		String errorDelivery = "DIRECT";		
		int option = 0;

		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 1; //user DN
		    	else if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 2; //error delivery method
		    	else if (argVal.compareToIgnoreCase("-h") == 0)
		    		option = 4; //host name
		    	else if (argVal.compareToIgnoreCase("-p") == 0)
		    		option = 5; //port 
		    	else //invalid
		    		option = 0;
		    } else if (option > 0) {
		    	if (option == 1) 
		    		myDn = argVal;
		    	else if (option == 4) 
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;
		    	else if (option == 2) {
		    		String priorMethod = errorDelivery;
		    		if (argVal.compareToIgnoreCase("STREAMING") == 0)
		    			errorDelivery = "STREAMING";
		    		else if(argVal.compareToIgnoreCase("DIRECT") == 0)
		    			errorDelivery = "DIRECT";
		    		else {
		    			errorDelivery = priorMethod;
		    			String msg = "The error delivery method specified (" + argVal + 
		    				") is not valid.  Restored prior setting: " + priorMethod;
		    			System.out.println(msg);
		    		}
		    	}
		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters
		
		if (myDn == null) {
			usage();
			return;
		}

		CatalogServiceStub stub = getStub(hostName, portNum, myDn);
		//create the request
		DoesUserExistRequestDocument doc = DoesUserExistRequestDocument.Factory.newInstance();
		DoesUserExistRequestDocument.DoesUserExistRequest request = doc.addNewDoesUserExistRequest();
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		//invoke service
		DoesUserExistResponseDocument myResponse = stub.doesUserExist(doc);
		boolean result = myResponse.getDoesUserExistResponse().getUserExists();
		if (result)
		    System.out.println("The user exists");
		else
		    System.out.println("The user is lost in cyberspace");
		System.out.println("Response Doc:\n" + myResponse.toString() );
		stub.cleanup();
	} //end of main

	   private static void usage() {
	    	System.out.println("Usage: TestUserExists -dn <user DN> -ed <error delivery method> -h <host> -p <port>\n" + 
	    			   "Defaults:\n" + 
	    			   "errors = DIRECT\n" +
	    			   "host   = localhost\n" + 
	    			   "port   = 8080\n" + 
	    			   "Result and Error Delivery Options: STREAMING, DIRECT");
	    	return;
	        } //end of usage
	   
} //end of class TestUserExists

