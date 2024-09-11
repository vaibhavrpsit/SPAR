/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/IsNotCustomerInfoSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/05/10 - remove deprecated log amanger and technician
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/08/09 14:20:27  kll
 *   @scr 6796: logging clean-up
 *
 *   Revision 1.5  2004/04/12 22:32:54  tfritz
 *   @scr 3884 - Found that the sale cargo customer info could be null when you enter POS in training mode and then escape right away
 *
 *   Revision 1.4  2004/03/16 18:30:42  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.3  2004/02/23 15:03:23  blj
 *   @scr 0 - no comment
 *
 *   Revision 1.2  2004/02/20 23:10:33  bjosserand
 *   @scr fix unused imports
 *
 *   Revision 1.1  2004/02/20 21:13:18  aschenk
 *   @scr 2845: Added traffic signal so user is not taken out of POS when they have entered information (zip, phone number) and slected esc.
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:37:08   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:08   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:10:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:33:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

import oracle.retail.stores.domain.customer.CustomerInfoIfc;

/**
 * This signal checks to see if the till is suspended.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class IsNotCustomerInfoSignal implements TrafficLightIfc
{
    static final long serialVersionUID = 4528371495989956748L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(IsNotCustomerInfoSignal.class);

    /**
     * Checks to see if the till is suspended.
     * 
     * @return true if the till is suspended, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        boolean result = true;
        logger.debug("IsNotCustomerInfoSignal.roadClear() - entry");

        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        // get customer info from cargo
        CustomerInfoIfc customerInfo = cargo.getCustomerInfo();

        if (customerInfo != null)
        {
            // determine type of customer info
            int customerInfoType = customerInfo.getCustomerInfoType();

            if (customerInfoType != CustomerInfoIfc.CUSTOMER_INFO_TYPE_NONE)
            {
                result = false;
            }
        }

        logger.debug("IsNotCustomerInfoSignal.roadClear() - exit");
        return (result);
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class:  IsTillSuspendedSignal (Revision " + getRevisionNumber() + ")"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
