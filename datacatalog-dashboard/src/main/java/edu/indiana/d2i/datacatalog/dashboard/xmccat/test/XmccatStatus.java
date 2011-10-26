package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import java.util.ArrayList;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.catalog.catalogclient.*;

/**
 * This method tests the checkStatus operation.  The host, port, and error delivery 
 * are all configurable options.  The results themselves (the status of the service)
 * is always a direct response included in the body of the response.
 * 
 * @author Scott Jensen scjensen@cs.indiana.edu
 *
 */
public class XmccatStatus extends TesterBase {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String hostName = "localhost";
		String portNum = "8080";
		String delivery = "DIRECT";
		String dn = null;
		int option = 0;
		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 1; //user DN
		    	else if (argVal.compareToIgnoreCase("-h") == 0)
		    		option = 4; //host name
		    	else if (argVal.compareToIgnoreCase("-p") == 0)
		    		option = 5; //port 
		    	else if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 6; //error delivery method
		    	else //invalid
		    		option = 0;
		    } else if (option > 0) {
		    	if (option == 1)
		    		dn = argVal;
		    	else if (option == 4)
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;
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

		CatalogServiceStub stub = getStub(hostName, portNum, dn);
		CheckStatusRequestDocument doc = CheckStatusRequestDocument.Factory.newInstance();
		CheckStatusRequestDocument.CheckStatusRequest request = doc.addNewCheckStatusRequest();
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(delivery));
		System.out.println("Request Document:\n" + doc.toString() );
		
		//invoke service
		CheckStatusResponseDocument myResponse = stub.checkStatus(doc);
		System.out.println("Response Document:\n" + myResponse.toString() );
		XmlOptions validateOptions = new XmlOptions();
		ArrayList errorList = new ArrayList();
		validateOptions.setErrorListener(errorList);
		
		if (myResponse.validate(validateOptions) )
			System.out.println("Valid");
		else {
			System.out.println("Sorry Charlie - Not Valid");
			for (int i = 0; i < errorList.size(); i++)
		      {
		          XmlError error = (XmlError)errorList.get(i);
		          System.out.println("Message: " + error.getMessage() );
		          System.out.println("Location of invalid XML: " + 
		              error.getCursorLocation().xmlText() + "\n");
		      }
		}
		stub.cleanup();
	} //end of main
	
	private static void usage() {
		System.out.println("Usage: XmccatStatus -dn <distinguished name> -ed <error delivery method> -h <host> -p <port>\n" + 
				"Defaults (all parameters are optional):\n" + 
				"delivery = DIRECT\n" +
				"host = localhost\n" + 
				"port = 8080\n" +
				"delivery method options for errors:\n" +
				"DIRECT    - included in response\n" +
		"STREAMING - response includes URL for retrieving errors");
		return;
	} //end of usage
}  //end of XmccatStatus
