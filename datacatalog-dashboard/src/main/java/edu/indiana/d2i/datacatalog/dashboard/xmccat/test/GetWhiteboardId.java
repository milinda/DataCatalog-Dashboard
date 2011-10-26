package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.catalog.catalogclient.*;

/**
 * This small test program Returns the global ID of the 
 * default workspace (a.k.a. whiteboard) of the user specified
 * as a parameter (if the user exists and they have a default workspace.  
 * Usage: GetWhiteboardId -dn <dn> -h <host> -p <port>
 * If the host or port are omitted, localhost and 8080 are
 * the respective default values.
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class GetWhiteboardId  extends TesterBase {
	/**
	 * @param args
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
		    		option = 1; //distinguished name
		    	else if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 3; //error delivery
		    	else if (argVal.compareToIgnoreCase("-h") == 0)
		    		option = 4; //host name
		    	else if (argVal.compareToIgnoreCase("-p") == 0)
		    		option = 5; //port 
		    	else //invalid
		    		option = 0;
		    } else if (option > 0) {
		    	if (option == 1)
		    		myDn = argVal;
		    	else if (option == 3) {
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
		    	else if (option == 4) 
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;

		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters
    	
    	if(myDn == null) {
    		usage();
	    	return;
    	}
 
    	CatalogServiceStub stub = getStub(hostName, portNum, myDn);
		//create the request
    	WhiteboardIdRequestDocument doc = WhiteboardIdRequestDocument.Factory.newInstance();
    	WhiteboardIdRequestDocument.WhiteboardIdRequest request = doc.addNewWhiteboardIdRequest();
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		System.out.println("Request Document:\n" + doc.toString() );
		
		//invoke service
		WhiteboardIdResponseDocument myResponse = stub.whiteboardId(doc);
		System.out.println(myResponse.getWhiteboardIdResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		stub.cleanup();
	} //end of main
	
	   private static void usage() {
	    	System.out.println("Usage: GetWhiteboardId -dn <dn> -ed <error delivery method> -h <host> -p <port>\n" + 
	    			   "Defaults:\n" + 
	    			   "errors = DIRECT\n" +
	     			   "host   = localhost\n" + 
	    			   "port   = 8080\n" + 
	    			   "Result and Error Delivery Options: STREAMING, DIRECT");
	    	return;
	   } //end of usage

} //end of class GetWhiteboardId

