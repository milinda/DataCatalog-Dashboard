package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import java.io.File;
import java.util.ArrayList;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.metadata.catalog.domain.*;
import edu.indiana.dde.catalog.catalogclient.*;

/**
 * This small test program can be used to add one or more 
 * concepts to the existing metadata describing a digital object.
 * Each XML file passed as a parameter for a concept to be added 
 * should be the full XML for a catalogPropertyAdd element from the
 * XMC Cat types schema.
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class AddConcept extends TesterBase {

	public static void main(String[] args) throws Exception {
		int option = 0;
		String guid = null;
		String hostName = "localhost";
		String portNum = "8080";
		String myDn = null;
		String delivery = "DIRECT";
		ArrayList<String> fileNames = new ArrayList<String>();
		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-g") == 0)
		    		option = 2; //guid
		    	else if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 3; //distinguished name
		    	else if (argVal.compareToIgnoreCase("-f") == 0)
		    		option = 7;//file name
		    	else if (argVal.compareToIgnoreCase("-h") == 0)
		    		option = 4; //host name
		    	else if (argVal.compareToIgnoreCase("-p") == 0)
		    		option = 5; //port 
		    	else if (argVal.compareToIgnoreCase("-d") == 0)
		    		option = 6; //error delivery method
		    	else //invalid
		    		option = 0;
		    } else if (option > 0) {
		    	if (option == 2)
		    		guid = argVal;
		    	else if (option == 3)
		    		myDn = argVal;
		    	else if (option == 4) 
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;
		    	else if (option == 7) 
		    		fileNames.add(argVal);
		    	else if (option == 6) {
		    		String priorMethod = delivery;
		    		if (argVal.compareToIgnoreCase("STREAMING") == 0)
		    			delivery = "STREAMING";
		    		else if(argVal.compareToIgnoreCase("DIRECT") == 0)
		    			delivery = "DIRECT";
		    		else {
		    			delivery = priorMethod;
		    			String msg = "The delivery method specified (" + argVal + 
		    				") is not valid.  Restored prior setting: " + priorMethod;
		    			System.out.println(msg);
		    		}
		    	}
		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters

		if (fileNames.size() == 0 || myDn == null || guid == null) {
			usage();
	    	return;
		}
		// create the XMC Cat client stub and the request document
		CatalogServiceStub stub = getStub(hostName, portNum, myDn);
		AddPropertyRequestDocument doc = AddPropertyRequestDocument.Factory.newInstance();
		AddPropertyRequestDocument.AddPropertyRequest request = doc.addNewAddPropertyRequest();
		// set the error delivery method (direct or streaming)
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(delivery));
		// Identify the data object to be updated
		CatalogPropertyType property = request.addNewCatalogProperty();
		property.setObjectId(guid);
		// The user can add a single new concept or multiple 
		// concepts as separate files.  Concepts were previously 
		// known as properties.
		ArrayList<CatalogPropertyAddType> catalogPropertyAddArray = new ArrayList<CatalogPropertyAddType>(fileNames.size() );
		// add the concepts
		for (String filename: fileNames) {
			try {
				File dataFile = new File(filename);
			    CatalogPropertyAddDocument addDoc = CatalogPropertyAddDocument.Factory.parse(dataFile);
			    catalogPropertyAddArray.add(addDoc.getCatalogPropertyAdd() );
			} catch (Exception e) {
			    System.out.println("There was an error loading the data file: " + filename + 
			    		" and the concepts were not added: " + e);
			    return;
			}
		} //loop through the files
		// put the concepts into the add document
		CatalogPropertyAddType[] addTypeArray = new CatalogPropertyAddType[catalogPropertyAddArray.size()];
		for (int i=0; i < catalogPropertyAddArray.size(); i++) {
			addTypeArray[i] = catalogPropertyAddArray.get(i);
		}
		property.setCatalogPropertyAddArray(addTypeArray);
		catalogPropertyAddArray.clear();
		
		// invoke service
		AddPropertyResponseDocument myResponse = stub.addProperty(doc);
		System.out.println(myResponse.getAddPropertyResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		stub.cleanup();
		return;
	} //end of main
	
	   private static void usage() {
		   System.out.println("Usage: AddConcept -f <properties document(s)> -g <GUID> -dn <dn> -d <delivery method> -h <host> -p <port>\n" + 
				   "Defaults:\n" + 
				   "delivery = DIRECT\n" +
				   "host = localhost\n" + 
				   "port = 8080\n" +
				   "delivery method options for errors:\n" +
				   "DIRECT    - included in response\n" +
				   "STREAMING - response includes URL for retrieving errors");
		   return;
	   } //end of usage
	
} //end of class AddConcept
