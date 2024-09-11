/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/OpenStoreLaunchShuttle.java /main/13 2014/07/23 15:44:29 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/15/14 - Support for cancelling offline store open
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:03  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:29:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:15:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.startofday.StartOfDayCargo;

//--------------------------------------------------------------------------
/**
    This service copies any needed information from the cargo used
    in one service to another service. <P>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class OpenStoreLaunchShuttle extends FinancialCargoShuttle
{

	/** Serial Version ID */
	private static final long serialVersionUID = -4829194141749007627L;

	/** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.register.registeropen.OpenStoreLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
       Copies information to the cargo used in the service. <P>
       
       @since 14.1 Copies the store open mode to the start of day cargo
       to support offline store open operations
       
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // execute FinancialCargoShuttle class unload
        super.unload(bus);

        // get cargo reference and set attributes
        StartOfDayCargo cargo = (StartOfDayCargo)bus.getCargo();
        StoreStatusIfc status = cargo.getStoreStatus();
        
        if (status.isStale())
        {
        	// Sets the StoreOpenCloseTransaction to an offline mode if the store status is 
        	// stale. This will allow handling duplicate open close transactions
        	
            logger.warn("Store status for " + status.getStore().getStoreID() + " is stale for"
                      + " for date " + status.getBusinessDate()+ " setting open mode to offline");
            cargo.setStoreOpenMode(StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_OFFLINE);
        }
        
        cargo.setShowWarning(true);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  OpenStoreLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
