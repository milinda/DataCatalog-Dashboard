package edu.indiana.d2i.datacatalog.dashboard.xmccat.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.indiana.dde.catalog.catalogclient.CatalogServiceStub;
import edu.indiana.dde.metadata.catalog.types.*;
import edu.indiana.dde.catalog.catalogclient.*;


public class ContextQuery extends TesterBase {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int option = 0;
		String hostName = "localhost";
		String portNum = "8080";
		String dn = null;
		String hFilter = "TARGET";
		String cFilter = "ID_ONLY";
		String conceptFilterFile = null;
		String elementFilterFile = null;
		String resultDelivery = "STREAMING";
		String errorDelivery = "DIRECT";
		String targetFileName = null;  //file containing the queryTarget element
		String contextFileName = null;  //file containing the queryContext element (optional)
		int count = 0;
		int offset = 0;
		int responseType = 0; // 1=ID only, 2=full schema, 3 = properties, 4 = elements

		for (int j = 0; j < args.length; j++) {
			String argVal = args[j];
			if ( argVal.compareToIgnoreCase("--help") == 0 || argVal.compareToIgnoreCase("-help") == 0) {
				usage();
				return;
			} else if (argVal.startsWith("-")) {
				if (argVal.compareToIgnoreCase("-target") == 0)
					option = 1; //target file
				else if (argVal.compareToIgnoreCase("-context") == 0)
					option = 2; //context file
				else if (argVal.compareToIgnoreCase("-hf") == 0)
					option = 6; //Hierarchy Filter
				else if (argVal.compareToIgnoreCase("-cf") == 0)
					option = 7; //Content Filter
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
				else if (argVal.compareToIgnoreCase("-off") == 0)
					option = 10;
				else if (argVal.compareToIgnoreCase("-cnt") == 0)
					option = 11;
				else if (argVal.compareToIgnoreCase("-concept") == 0)
					option = 12;  // path and name of the concept filter file - comma delimited
				else if (argVal.compareToIgnoreCase("-element") == 0)
					option = 13;  // path and name of the element filter file - comma delimited
				else //invalid
					option = 0;
			} else if (option > 0) {
				if (option == 1)
					targetFileName = argVal;
				else if (option == 2)
					contextFileName = argVal;
				else if (option == 3)
					dn = argVal;
				else if (option == 4) 
					hostName = argVal;
				else if (option == 5) 
					portNum = argVal;
				else if (option == 6)
					hFilter = argVal;
				else if (option == 7)
					cFilter = argVal;
				else if (option == 8)
					resultDelivery = argVal;
				else if (option == 9)
					errorDelivery = argVal;
				else if (option == 10)
					offset = Integer.valueOf(argVal);
				else if (option == 11)
					count = Integer.valueOf(argVal);
				else if (option == 12)
					conceptFilterFile = argVal;
				else if (option == 13)
					elementFilterFile = argVal;
				option = 0; //reset
			} //end of option setting
		} //loop through parameters

		if (dn == null || targetFileName == null) {
			usage();
			return;
		}
		
		// Create the service stub and query request
		CatalogServiceStub stub = getStub(hostName, portNum, dn);
		ContextQueryRequestDocument doc = ContextQueryRequestDocument.Factory.newInstance(); 
		ContextQueryRequestDocument.ContextQueryRequest request = doc.addNewContextQueryRequest();	

		//The context query consists of:
		// 1) a user ID
		// 2) queryTarget
		// 3) contextQuery (optional)
		// 4) queryResultFormat
		// build the target
		File targetFile = new File(targetFileName);
		QueryTargetDocument targetDoc = QueryTargetDocument.Factory.parse(targetFile);
		request.setQueryTarget(targetDoc.getQueryTarget() );
		
		if (contextFileName != null) {
			File context = new File(contextFileName);
			ContextQueryDocument contextDoc = ContextQueryDocument.Factory.parse(context);
			request.setContextQuery(contextDoc.getContextQuery() );
		}

		// build the result format
		QueryResultFormatType resultFormat = QueryResultFormatType.Factory.newInstance();
		resultFormat.setOffset(offset);
		resultFormat.setCount(count);
		
//		resultFormat.setShowCreateDate(false);
//		resultFormat.setShowModifyDate(true);
		resultFormat.setHierarchyFilter(HierarchyFilterType.Enum.forString(hFilter) );
		// If both a concept filter and an element filter are specified, the
		// concept filter will take precedence and the element filter will be ignored.
		// A query result can optionally have one or the other, but not both.
		if (conceptFilterFile != null) {
			loadConceptFilter(resultFormat, conceptFilterFile);
		} else if (elementFilterFile != null) {
			loadElementFilter(resultFormat, elementFilterFile);
		} else {
			// If a content filter is specified, it will only be applied if there is not 
			// a concept or element filter specified.
			System.out.println("Applying content filter: " + cFilter);
			resultFormat.setContentFilter(ContentFilterType.Enum.forString(cFilter) );
			if (cFilter.compareToIgnoreCase("ID_ONLY") == 0)
				responseType = 1;
			else 
				responseType = 2;
		}
		resultFormat.setResultDeliveryMethod(CatalogDeliveryType.Enum.forString(resultDelivery) );
		resultFormat.setErrorDeliveryMethod(CatalogDeliveryType.Enum.forString(errorDelivery) );
		
		request.setQueryResultFormat(resultFormat);
		System.out.println(doc.toString() );

		//invoke service
		long start = System.currentTimeMillis();
		QueryResponseDocument myResponse = stub.query(doc);
		long end = System.currentTimeMillis();
		System.out.println("Query execution round trip (ms): " + (end-start) );
		System.out.println(myResponse.getQueryResponse().getCatalogOperationStatus().getStatus().toString() );
		System.out.println("\nResponse Document:\n******************\n" + myResponse.toString() );
		stub.cleanup();
		return;
	} //end of main
	
	private static void loadConceptFilter(QueryResultFormatType resultFormat, String filterFileName) throws Exception {
		File filterFile = new File(filterFileName);
		if (!filterFile.canRead() ) {
			throw new Exception("The file named " + filterFileName + 
					            " does not exist or cannot be read, so the concept filter could not be applied.");
		}
		BufferedReader br = new BufferedReader(new FileReader(filterFile));
		// expects file with comma separated values: concept_Name, concept_source
		// there should be one line for each concept filter to be included
		String filterLine = br.readLine();
		while (filterLine != null) {
			String[] filterParameters = filterLine.split(",");
			// There should be a name and source - any extraneous stuff is ignored
			if (filterParameters.length > 1) {
				String name = filterParameters[0].trim();
				String source = filterParameters[1].trim();
				if (name.length() > 0 && source.length() > 0) {
					PropertyFilterType filter = resultFormat.addNewPropertyFilter();
					filter.setPropertyName(name);
					filter.setPropertySource(source);
				} else {
					System.err.println("The name and/or source in the following concept filter is blank: " + 
							filterLine + " so it was not included in the result filter.");
				}
			} else {
				System.err.println("The following concept filter did not include a name and source: " + 
						filterLine + " so it was not included in the result filter.");
			}
			
			filterLine = br.readLine();
		}
		return;
	} //end of loadConceptFilter

	private static void loadElementFilter(QueryResultFormatType resultFormat, String filterFileName) throws Exception {
		File filterFile = new File(filterFileName);
		if (!filterFile.canRead() ) {
			throw new Exception("The file named " + filterFileName + 
					            " does not exist or cannot be read, so the element filter could not be applied.");
		}
		BufferedReader br = new BufferedReader(new FileReader(filterFile));
		// expects file with comma separated values: concept_Name, concept_source, element_name, element_source, optional filter_name
		// there should be one line for each element filter to be included
		String filterLine = br.readLine();
		while (filterLine != null) {
			String[] filterParameters = filterLine.split(",");
			// There should be concept names and sources (and possibly a filter name) - any extraneous stuff is ignored
			if (filterParameters.length > 3) {
				String conceptName = filterParameters[0].trim();
				String conceptSource = filterParameters[1].trim();
				String elementName = filterParameters[2].trim();
				String elementSource = filterParameters[3].trim();
				String filterName = elementName; // default
				if (filterParameters.length > 4) {
					filterName = filterParameters[4].trim();
					if (filterName.length() == 0)
						filterName = elementName;
				}
				if (conceptName.length() > 0 && conceptSource.length() > 0 && 
						elementName.length() > 0 && elementSource.length() > 0) {
					ElementFilterType filter = resultFormat.addNewElementFilter();
					filter.setPropertyName(conceptName);
					filter.setPropertySource(conceptSource);
					filter.setElementName(elementName);
					filter.setElementSource(elementSource);
					filter.setElementFilter(filterName);
				} else {
					System.err.println("The concept or element name and/or source in the following element filter is blank: " + 
							filterLine + " so it was not included in the result filter.");
				}
			} else {
				System.err.println("The following element filter did not include the 4 required fields: " + 
						filterLine + " so it was not included in the result filter.");
			}
			
			filterLine = br.readLine();
		}
		return;
	} //end of loadElementFilter
	
	
	private static void usage() {
		System.out.println("Usage: ContextQuery -dn <dn> -hf <optional hierarchy filter> -cf <optional content filter> -concept <filter file> -element <filter file> -target <name of file containing target> -context <name of optional file containing context> -cnt <optional count> -off <optional offset> -rd <optional result delivery> -ed <optional error delivery> -h <host> -p <port>\n" + 
				   "Defaults:\n" + 
				   "hf   = TARGET\n" + 
				   "cf   = ID_ONLY\n" +
				   "rd   = STREAMING\n" + 
				   "ed   = DIRECT\n" + 
				   "host = localhost\n" + 
				   "port = 8080\n" + 
				   "Hierarchy Filter Options: TARGET, SUBTREE\n" +
				   "Content Filter Options: ID_ONLY, FULL_SCHEMA\n" + 
				   " concept and element filters expect the path and name for a file containing one csv filter per line:\n" + 
				   " -concept file: concept_name, concept_source\n" + 
				   " -element file: concept_name, concept_source, element_name, element_source, optional filter_name\n" +
				   "Result and Error Delivery Options: STREAMING, DIRECT\n" + 
				   "The target file is required, but the context file is optional");
		return;
	    } //end of usage
	
	
}// end of class ContextQuery
