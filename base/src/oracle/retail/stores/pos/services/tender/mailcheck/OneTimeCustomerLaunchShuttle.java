/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/OneTimeCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  04/02/09 - Fixed business customer issue if tender done by mail
 *                         bank check for refund option
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 * $
 * Revision 1.7  2004/09/23 00:07:16  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.6  2004/08/23 16:15:59  cdb
 * @scr 4204 Removed tab characters
 *
 * Revision 1.5  2004/07/06 20:15:05  crain
 * @scr 6004 System crashes when redeeming a gift certificate for Mail Bank Check
 *
 * Revision 1.4  2004/07/01 14:00:57  jeffp
 * @scr 5898 Added check to see what cargo was being passed in.
 *
 * Revision 1.3  2004/06/19 17:08:33  khassen
 * @scr 5684 - Feature enhancements for capture customer use case.  Added setTransaction() call.
 *
 * Revision 1.2  2004/06/19 16:07:25  khassen
 * @scr 5684 - Feature enhancements for capture customer use case.  Added setTransaction() call.
 *
 * Revision 1.1  2004/04/05 15:44:13  epd
 * @scr 4263 Moved Mail Bank Check to new sub tour
 *
 * Revision 1.10  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.9  2004/03/01 23:09:57  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.8  2004/03/01 19:03:15  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.7  2004/02/27 23:17:44  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.6  2004/02/27 20:59:05  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.5  2004/02/27 01:13:01  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.4  2004/02/26 21:08:40  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.3  2004/02/26 19:47:04  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.2  2004/02/26 17:13:59  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.1  2004/02/25 20:26:44  bjosserand
 * @scr 0 Mail Bank Check
 * Revision 1.4 2004/02/17 19:26:17 epd @scr 0 Code cleanup. Returned unused
 * local variables.
 * 
 * Revision 1.3 2004/02/12 16:48:22 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:22:51 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.2 Feb 06 2004 16:53:36 bjosserand Mail Bank Check.
 * 
 * Rev 1.1 Feb 05 2004 14:27:00 bjosserand Mail Bank Check.
 * 
 * Rev 1.0 Nov 04 2003 11:17:42 epd Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
 * This shuttle is used to go to the customer service. $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class OneTimeCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4737809262396226172L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "OneTimeCustomerLaunchShuttle";
    /**
     * tender cargo reference
     */
    protected TenderCargo tenderCargo;

    //----------------------------------------------------------------------
    /**
     * Load a copy of TenderCargo into the shuttle for use in unload().
     * <p>
     * 
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        tenderCargo = (TenderCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Make a CustomerFindCargo and populate it.
     * <p>
     * 
     * @param bus
     *            the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
        //TenderableTransactionIfc trans =
        //    (TenderableTransactionIfc) ((ADO) tenderCargo.getCurrentTransactionADO()).toLegacy();
        CustomerIfc customer = tenderCargo.getCurrentTransactionADO().getCustomer();
        HashMap tenderAttributes = tenderCargo.getTenderAttributes();

        cargo.setCustomer(customer);
        cargo.setTenderType(TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK);
        cargo.setBalanceDue(tenderCargo.parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT)));
        cargo.setTransaction((TransactionIfc)(tenderCargo.getCurrentTransactionADO()).toLegacy());
    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class:  OneTimeCustomerLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
