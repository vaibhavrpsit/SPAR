/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ado.tender;

/**
 * This class defines the constants to be used as keys
 * in the hash used as an argument to addTender().
 */
public interface MAXTenderConstantsIfc
{

	
    public static final String STORE_CREDIT_VALIDATED = "STORE_CREDIT_VALIDATED";
    public static final String STORE_CREDIT_EXPIRED = "STORE_CREDIT_EXPIRED";
    
    public static final String STORE_CREDIT_GRACE_PERIOD = "StoreCreditGraceDaysToExpiration";
    
}
