/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/OneTimeCustomerReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/14/09 - use localized code for send mail bank
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse
 * $
 * Revision 1.4  2004/09/23 00:07:16  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/06/21 14:22:41  khassen
 * @scr 5684 - Feature enhancements for capture customer use case: customer/capturecustomer accomodation.
 *
 * Revision 1.2  2004/06/18 12:12:26  khassen
 * @scr 5684 - Feature enhancements for capture customer use case.
 *
 * Revision 1.1  2004/04/05 15:44:13  epd
 * @scr 4263 Moved Mail Bank Check to new sub tour
 *
 * Revision 1.9  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.8  2004/03/01 23:09:57  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.7  2004/03/01 19:03:15  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.6  2004/02/27 20:59:05  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.5  2004/02/27 16:39:40  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.4  2004/02/26 23:26:04  bjosserand
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
 * Revision 1.3 2004/02/12 16:48:22 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:22:51 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Feb 06 2004 16:53:56 bjosserand Mail Bank Check.
 *
 * Rev 1.1 Feb 05 2004 14:27:16 bjosserand Mail Bank Check.
 *
 * Rev 1.0 Feb 01 2004 13:44:32 bjosserand Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
//--------------------------------------------------------------------------
/**
 * Returns from CustomerFind to Tender.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @deprecated Since POS 7.0, use tenderADO service
 */
//--------------------------------------------------------------------------
public class OneTimeCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -415808545047159470L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.tender.mailcheck.FindCustomerReturnShuttle.class);

    public static final String SHUTTLENAME = "OneTimeCustomerReturnShuttle";

    // the customer cargo
    protected CaptureCustomerInfoCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
     * Gets a copy of CustomerCargo for use in unload().
     * <p>
     *
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CaptureCustomerInfoCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Links the Customer to the Transaction and copies Customer info.
     * <p>
     *
     * @param bus
     *            the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerCargo.getCustomer();

        TenderCargo tenderCargo = (TenderCargo) bus.getCargo();

        if (customer != null)
        {
            tenderCargo.setCustomer(customer);
            if (customerCargo.getCustomer().getPersonalIDType() != null)
            tenderCargo.setIdType(customerCargo.getCustomer().getPersonalIDType().getCode());
            tenderCargo.setLocalizedPersonalIDCode(customerCargo.getCustomer().getPersonalIDType());
            tenderCargo.setPhoneType(customerCargo.getCustomer().getPhoneType());
        }
    }
}
