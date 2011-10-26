package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import java.io.File;

import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.catalog.catalogclient.*;

public class AddUser extends TesterBase {

	public static void main(String[] args) throws Exception {
		int option = 0;
		String replacementDn = null;
		String replacementName = null;
		String errorDelivery = "DIRECT";
		String hostName = "localhost";
		String portNum = "8080";
		String myDn = null; //the creator of the new user
		String fileName = null;
		
		for (int j = 0; j < args.length; j++) {
		    String argVal = args[j];
		    if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
		    	usage();
		    	return;
		    } else if (argVal.startsWith("-")) {
		    	if (argVal.compareToIgnoreCase("-ed") == 0)
		    		option = 2; //error delivery
		    	else if (argVal.compareToIgnoreCase("-rdn") == 0)
		    		option = 1; //replacement DN
		    	else if (argVal.compareToIgnoreCase("-rn") == 0)
		    		option = 6; //replacement name
		    	else if (argVal.compareToIgnoreCase("-dn") == 0)
		    		option = 3; //distinguished name
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
		    	replacementDn = argVal;
		    	else if (option == 3)
		    		myDn = argVal;
		    	else if (option == 4) 
		    		hostName = argVal;
		    	else if (option == 5) 
		    		portNum = argVal;
		    	else if (option == 6)
		    		replacementName = argVal;
		    	else if (option == 7) 
		    		fileName = argVal;

		    	option = 0; //reset
		    } //end of option setting
		} //loop through parameters

		if (fileName == null || myDn == null) {
			usage();
	    	return;
		}
		
		CatalogServiceStub stub = getStub(hostName, portNum, myDn);		
		CreateUserRequestDocument doc = CreateUserRequestDocument.Factory.newInstance();
		CreateUserRequestDocument.CreateUserRequest request = doc.addNewCreateUserRequest();
		request.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		
		File dataFile = new File(fileName);
		CatalogUserDocument userDoc = null;
		try {
			userDoc = CatalogUserDocument.Factory.parse(dataFile);
		} catch (Exception e) {
		    System.out.println("An error occurred in parsing the XML containing the user definition: " + e);
		    return;
		}
		if (replacementDn != null)
			userDoc.getCatalogUser().setUserId(replacementDn);
		if (replacementName != null)
			userDoc.getCatalogUser().setUserName(replacementName);
		UserType[] userArray = new UserType[1];
		userArray[0] = userDoc.getCatalogUser();
		request.setCatalogUserArray(userArray);
		
		System.out.println(doc.toString() );
		
		//invoke service
		CreateUserResponseDocument myResponse = stub.createUser(doc);
		System.out.println(myResponse.getCreateUserResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		stub.cleanup();
		return;
	} //end of main
	
	private static void usage() {
    	System.out.println("Usage: AddUser -f <LEAD schema document> -rdn <replacement DN> -rn <optional replacement name> -dn <dn> -ed <error delivery> -h <host> -p <port>\n" + 
 			       "errors = DIRECT\n" +
    			   "host   = localhost\n" + 
    			   "port   = 8080\n" +
    			   "Result and Error Delivery Options: STREAMING, DIRECT");
    	return;
        } //end of usage

}  //end of class AddUser
