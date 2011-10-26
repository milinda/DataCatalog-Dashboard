package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.catalog.catalogclient.*;

/**
 * This sample program is used to execute the query that
 * returns the details for each domain concept specified 
 * in the XMC Cat catalog for the domain schema for which 
 * the catalog is implemented. 
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class ConceptDefinitionsQuery extends TesterBase {

	public static void main(String[] args) throws Exception {
		int option = 0;
		String hostName = "localhost";
		String portNum = "8080";
		String myDn = null;
		String errorDelivery = "DIRECT";
		String resultDelivery = "DIRECT";
		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 3; //distinguished name
		    	else if (argVal.compareToIgnoreCase("-rd") == 0)
		    		option = 1; //result delivery
		    	else if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 2; //error delivery
		    	else if (argVal.compareToIgnoreCase("-h") == 0)
		    		option = 4; //host name
		    	else if (argVal.compareToIgnoreCase("-p") == 0)
		    		option = 5; //port 
		    	else //invalid
		    		option = 0;
		    } else if (option > 0) {
		    	if (option == 3)
		    		myDn = argVal;
		    	else if (option == 4) 
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;
		    	else if (option == 1) {
		    		String priorMethod = resultDelivery;
		    		if (argVal.compareToIgnoreCase("STREAMING") == 0)
		    			resultDelivery = "STREAMING";
		    		else if(argVal.compareToIgnoreCase("DIRECT") == 0)
		    			resultDelivery = "DIRECT";
		    		else {
		    			resultDelivery = priorMethod;
		    			String msg = "The result delivery method specified (" + argVal + 
		    				") is not valid.  Restored prior setting: " + priorMethod;
		    			System.out.println(msg);
		    		}
		    	} //end of result delivery
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
		    	}//end of error delivery
		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters

		if (myDn == null) {
			usage();
	    	return;
		}
		// Create the XMC Cat client stub and the request document
		CatalogServiceStub stub = getStub(hostName, portNum, myDn);
		QueryPropertyDefinitionsRequestDocument doc = QueryPropertyDefinitionsRequestDocument.Factory.newInstance();
		QueryPropertyDefinitionsRequestDocument.QueryPropertyDefinitionsRequest request = doc.addNewQueryPropertyDefinitionsRequest();
		// set the response and error delivery methods specified
		request.setResultDeliveryMethod(CatalogDeliveryType.Enum.forString(resultDelivery) );
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		// Print the request document
		System.out.println(doc.toString() );
		
		//invoke service
		QueryPropertyDefinitionsResponseDocument myResponse = stub.queryPropertyDefinitions(doc);
		System.out.println(myResponse.getQueryPropertyDefinitionsResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		if (resultDelivery.equalsIgnoreCase("STREAMING") ) {
			System.out.println("streaming delivery URL:\n" + myResponse.getQueryPropertyDefinitionsResponse().getResultStreamUrl() );
		}
		stub.cleanup();
		return;

	} //end of main
	
	private static void usage() {
		System.out.println("Usage: ConceptDefinitionsQuery -dn <dn> -rd <result delivery> -ed <error delivery method> -h <host> -p <port>\n" + 
				"Defaults:\n" + 
				"results = DIRECT\n" +
 			    "errors  = DIRECT\n" +
  			    "host    = localhost\n" + 
 			    "port    = 8080\n" + 
 			    "Result and Error Delivery Options: STREAMING, DIRECT");
		return;
	    } //end of usage
	
} //end of class ConceptDefinitionsQuery
