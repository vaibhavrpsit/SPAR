/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/startofday/BusinessDateLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:42 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:29:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:15:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:28:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.startofday;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.businessdate.BusinessDateCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the Main service to the BusinessDate service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class BusinessDateLaunchShuttle implements ShuttleIfc
{                                       // begin class BusinessDateLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2965841858149252284L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.startofday.BusinessDateLaunchShuttle.class);

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "BusinessDateLaunchShuttle";
    /**
       business day choices
    **/
    protected EYSDate[] businessDateList;

    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the Main service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // get store status from startofday cargo
        StartOfDayCargo cargo = (StartOfDayCargo) bus.getCargo();
        StoreStatusIfc storeStatus = cargo.getStoreStatus();

        // put store status business date in list
        businessDateList = new EYSDate[1];
        businessDateList[0] = storeStatus.getBusinessDate();

    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the BusinessDate service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // set store status list in cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();
        cargo.setBusinessDateList(businessDateList);
        cargo.setAdvanceDateFlag(true);

    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                       // end class BusinessDateLaunchShuttle
