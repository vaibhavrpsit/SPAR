/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek			Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.customer;

import oracle.retail.stores.domain.customer.CustomerConstantsIfc;

public interface MAXCustomerConstantsIfc extends CustomerConstantsIfc {

	/**
	 * revision number
	 */
	public static String revisionNumber = "$Revision: 1.3 $";

	/**
	 * Customer type - LOCAL
	 */
	public static final String LOCAL = "L";

	/**
	 * Customer type - CRM
	 */
	public static final String CRM = "T";

	/**
	 * Customer type - CORPORATE
	 */
	public static final String CORPORATE = "C";
	/**
	 * Customer type - EMPLOYEE
	 */
	public static final String EMPLOYEE = "E";
	/**
	 * Customer type - OTHER
	 */
	public static final String OTHER = "O";
}
