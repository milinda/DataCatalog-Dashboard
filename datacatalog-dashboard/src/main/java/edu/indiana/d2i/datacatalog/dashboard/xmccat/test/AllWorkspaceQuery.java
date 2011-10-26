package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.metadata.catalog.domain.*;
import edu.indiana.dde.catalog.catalogclient.*;

/**
 * This small test program retrieves the entire metadata describing
 * all of the data in a user's workspace.  If the user has extensive
 * metadata, it is best to use the streaming option instead of the 
 * direct option to return the results.  The content filter defaults to 
 * only the ID of each object, but the FULL_SCHEMA filter can be 
 * specified. For the actual operation in XMC Cat, the concept and 
 * element filter can also be used, but they are not options in this
 * example program.
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class AllWorkspaceQuery extends TesterBase {

	public static void main(String[] args) throws Exception {
		int option = 0;
		String myDn = null;		
		String hostName = "localhost";
		String portNum = "8080";
		String cFilter = "ID_ONLY";
		String resultDelivery = "STREAMING";
		String errorDelivery = "DIRECT";
		int count = 0;
		int offset = 0;
		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-cf") == 0)
		    		option = 7; //Content Filter
		    	else if (argVal.compareToIgnoreCase("-cnt") == 0)
		    		option = 10; // count 
		    	else if (argVal.compareToIgnoreCase("-off") == 0)
		    		option = 11; // offset 
		    	else if (argVal.compareToIgnoreCase("-rd") == 0)
		    		option = 8; // Result Delivery
		    	else if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 9; // Error Delivery
		    	else if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 3; //distinguished name
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
		    	else if (option == 7)
		    		cFilter = argVal;
		    	else if (option == 8)
		    		resultDelivery = argVal;
		    	else if (option == 9)
		    		errorDelivery = argVal;
		    	else if (option == 10)
		    		count = Integer.parseInt(argVal);
		    	else if (option == 11)
		    		offset = Integer.parseInt(argVal);
		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters

		if (myDn == null) {
			usage();
	    	return;
		}
		//Create the XMC Cat client stup and the request document
		CatalogServiceStub stub = getStub(hostName, portNum, myDn);
		AllWorkspaceQueryRequestDocument doc = AllWorkspaceQueryRequestDocument.Factory.newInstance();
		AllWorkspaceQueryRequestDocument.AllWorkspaceQueryRequest request = doc.addNewAllWorkspaceQueryRequest();
		QueryResultFormatType resultFormat = request.addNewQueryResultFormat();
		resultFormat.setOffset(offset);
		resultFormat.setCount(count);
		// For this test program, we always get the full subtree
		resultFormat.setHierarchyFilter(HierarchyFilterType.Enum.forString("SUBTREE") );
		// Apply the content filter - default is only the object ID
		System.out.println("Applying content filter: " + cFilter);
		resultFormat.setContentFilter(ContentFilterType.Enum.forString(cFilter) );
		// Set result and error delivery methods
		// For large workspaces, the streaming response should be used 
		resultFormat.setResultDeliveryMethod(CatalogDeliveryType.Enum.forString(resultDelivery) );
		resultFormat.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		// print the request document
		System.out.println(doc.toString() );
		
		//invoke service
		QueryResponseDocument myResponse = stub.allWorkspaceQuery(doc);
		System.out.println(myResponse.getQueryResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		if (myResponse.validate() ) {
			System.out.println("Valid");
			if (resultDelivery.equalsIgnoreCase("STREAMING") ) {
				System.out.println("streaming delivery URL:\n" + myResponse.getQueryResponse().getResultStreamUrl() );
			}
		} else
			System.out.println("Sorry Charlie - Response Was Not Valid");
		stub.cleanup();
		return;
	} //end of main
	
	private static void usage() {
		System.out.println("Usage: AllWorkspaceQuery -dn <distinguished name> -cf <optional content filter> -cnt <optional count> -off <optional offset> -rd <optional result delivery> -ed <optional error delivery> -h <host> -p <port>\n" + 
				   "Defaults:\n" + 
				   "cf   = ID_ONLY\n" +
				   "cnt  = 0 (no limit)\n" +
				   "off   = 0\n" +
				   "rd   = STREAMING\n" + 
				   "ed   = DIRECT\n" + 
				   "host = localhost\n" + 
				   "port = 8080\n" + 
				   "Content Filter Options: ID_ONLY, FULL_SCHEMA\n" + 
				   "Result and Error Delivery Options: STREAMING, DIRECT");
		return;
	    } //end of usage
	
} //end of class AllWorkspaceQuery
