/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/FinancialCargoShuttle.java /main/14 2013/01/14 16:35:05 tzgarba Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tzgarba   01/14/13 - Updated to use security context ID instead of
 *                         register ID
 *    cgreene   08/29/11 - set function id in shuttle, not its own site
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:03 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/09 16:55:58  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/04/01 16:04:10  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   08 Nov 2003 01:00:20   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.1   Nov 05 2003 23:38:38   cdb
 * Modified to use AbstractFinancialCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:00:08   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;

import org.apache.log4j.Logger;

/**
 * This shuttle copies the contents of one abstract financial cargo to another.
 * 
 * @version $Revision: /main/14 $
 */
public class FinancialCargoShuttle implements ShuttleIfc
{
    static final long serialVersionUID = 9220770768073159182L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(FinancialCargoShuttle.class);

    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /** Service name of order tour that requires function point access. */
    public static final String ORDER_OPTIONS = "OrderOptions";

    protected AbstractFinancialCargoIfc callingCargo;

    /**
     * Copies information from the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    public void load(BusIfc bus)
    {
        // get cargo reference and extract attributes
        callingCargo = (AbstractFinancialCargoIfc) bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    public void unload(BusIfc bus)
    {
        // get cargo reference and set attributes
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc) bus.getCargo();
        cargo.setAppID(callingCargo.getAppID());
        cargo.setStoreStatus(callingCargo.getStoreStatus());
        cargo.setRegister(callingCargo.getRegister());
        cargo.setTenderLimits(callingCargo.getTenderLimits());
        cargo.setOperator(callingCargo.getOperator());
        cargo.setLastReprintableTransactionID(callingCargo.getLastReprintableTransactionID());
        cargo.setCustomerInfo(callingCargo.getCustomerInfo());
        if (getOrderOptionsTourName().equals(bus.getServiceName()))
        {
            ((UserAccessCargoIfc)cargo).setAccessFunctionID(RoleFunctionIfc.ORDERS);
        }
    }

    /**
     * Return the name of the tour that is traveled for canceling an order.
     * 
     * @return
     */
    protected String getOrderOptionsTourName()
    {
        return ORDER_OPTIONS;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class:  FinancialCargoShuttle (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
