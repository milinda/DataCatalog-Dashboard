package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;


import java.io.File;

import edu.indiana.dde.metadata.catalog.domain.CatalogPropertyUpdateDocument;
import edu.indiana.dde.metadata.catalog.types.*;

import edu.indiana.dde.catalog.catalogclient.*;

/**
 * This sample program is used to update an existing 
 * concept in the metadata describing an object.  If
 * the concept can have multiple instances (and does)
 * then the position of the concept must be specified.
 * 
 * Also, the query timestamp can optionally be specified.
 * Each query result contains a timestamp attribute.  If
 * the GUI includes the timstamp of the last update the 
 * user was viewing when they made a change, it will be 
 * compared to the update timestamp for that object to 
 * make sure the user is making the update based on 
 * current metadata.
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class UpdateConcept extends TesterBase {

	public static void main(String[] args) throws Exception {
		int option = 0;
		String errorDelivery = "DIRECT"; 
		String hostName = "localhost";
		String portNum = "8080";
		String myDn = null; //the creator of the new user
		String fileName = null;
		String objectId = null;
		int position = -1; //default of no position
		long lastUpdate = -1;
		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 2; //error delivery
		    	else if (argVal.compareToIgnoreCase("-g") == 0)
		    		option = 1; //object ID
		    	else if (argVal.compareToIgnoreCase("-pos") == 0)
		    		option = 6; //replacement position (for multiple property instance)
		    	else if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 3; //distinguished name
		    	else if (argVal.compareToIgnoreCase("-u") == 0)
		    		option = 8;//last update (from query header)
		    	else if (argVal.compareToIgnoreCase("-f") == 0)
		    		option = 7;//file name
		    	else if (argVal.compareToIgnoreCase("-h") == 0)
		    		option = 4; //host name
		    	else if (argVal.compareToIgnoreCase("-p") == 0)
		    		option = 5; //port 
		    	else //invalid
		    		option = 0;
		    } else if (option > 0) {
		    	if (option == 2) {
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
		    	else if (option == 1)
		    		objectId = argVal;
		    	else if (option == 3)
		    		myDn = argVal;
		    	else if (option == 4) 
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;
		    	else if (option == 6)
		    		position = Integer.parseInt(argVal);
		    	else if (option == 7) 
		    		fileName = argVal;
		    	else if (option == 8) 
		    		lastUpdate = Long.parseLong(argVal);

		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters

		if (fileName == null || myDn == null || objectId == null) {
			usage();
	    	return;
		}
		// Create the XMC Cat client stub and the request document
		CatalogServiceStub stub = getStub(hostName, portNum, myDn);
		UpdatePropertyRequestDocument doc = UpdatePropertyRequestDocument.Factory.newInstance();
		UpdatePropertyRequestDocument.UpdatePropertyRequest request = doc.addNewUpdatePropertyRequest();
		// set the error delivery method (direct or streaming)
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		// create the update
		UpdatePropertyType myUpdate = request.addNewUpdateProperty();
		myUpdate.setObjectId(objectId);
		if (position > 0)
			myUpdate.setPosition(position);
		if (lastUpdate >= 0)
			myUpdate.setLastUpdate(lastUpdate);
		// Load the XML containing the updated concept
		File dataFile = new File(fileName);
		CatalogPropertyUpdateDocument propertyDoc = null;
		try {
			propertyDoc = CatalogPropertyUpdateDocument.Factory.parse(dataFile);
		} catch (Exception e) {
		    System.out.println("An error occurred in parsing the XML update element: " + e);
		    return;
		}
		myUpdate.setCatalogPropertyUpdate(propertyDoc.getCatalogPropertyUpdate() );
		request.setUpdateProperty(myUpdate);
		// Print the request document
		System.out.println(doc.toString() );
		
		//invoke service
		UpdatePropertyResponseDocument myResponse = stub.updateProperty(doc);
		System.out.println(myResponse.getUpdatePropertyResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		stub.cleanup();
		return;
	} //end of main
	
	private static void usage() {
    	System.out.println("Usage: UpdateConcept -f <concept XML document> -g <object GUID> -pos <concept position> -u <last update timestamp> -dn <dn> -ed <error delivery> -h <host> -p <port>\n" + 
 			       "position = 1\n" +
    			   "errors   = DIRECT\n" +
    			   "host     = localhost\n" + 
    			   "port     = 8080\n" +
    			   "Error Delivery Options: STREAMING, DIRECT");
    	return;
        } //end of usage
	
} //end of class UpdateConcept
