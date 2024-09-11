/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/CaptureCustomerReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  03/06/09 - fixed bug for application terminates when returning
 *                         item with receipt
 *    mahising  02/28/09 - Fixed capture customer issue for returns in no
 *                         receipt flow
 *    arathore  02/14/09 - Updated to pass Personal Id information to printing
 *                         tour.
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $
     Revision 1.6  2004/09/23 00:07:12  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.5  2004/06/23 00:46:11  blj
     @scr 5113 - added capture customer capability for redeem store credit.

     Revision 1.4  2004/06/21 18:23:38  khassen
     @scr 5684 - Feature enhancements for capture customer use case. Code review/updates.

     Revision 1.3  2004/06/21 14:22:41  khassen
     @scr 5684 - Feature enhancements for capture customer use case: customer/capturecustomer accomodation.

     Revision 1.2  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.1  2004/02/27 01:11:43  nrao
     Added Return Shuttle from Tender to Customer Capture.


 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.tender;

// foundation imports
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
 * Returns from CaptureCustomer to Tender.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
// --------------------------------------------------------------------------
public class CaptureCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -321751272411831045L;

    public static final String SHUTTLENAME = "CaptureCustomerReturnShuttle";

    // the customer cargo
    protected CaptureCustomerInfoCargo customerCargo = null;

    // ----------------------------------------------------------------------
    /**
     * Gets a copy of CustomerCargo
     * 
     * @param bus the bus being loaded
     */
    // ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CaptureCustomerInfoCargo)bus.getCargo();
    }

    // ----------------------------------------------------------------------
    /**
     * Make a TenderCargo and populate it.
     * 
     * @param bus the bus being unloaded
     */
    // ----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerCargo.getCustomer();
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        String currentLetter = bus.getCurrentLetter().getName();
        if (currentLetter != null && !(currentLetter.equals(CommonLetterIfc.CANCEL) || 
                currentLetter.equals(CommonLetterIfc.UNDO) || 
                currentLetter.equals(CommonLetterIfc.SKIP)))
        {
            if (customer != null)
            {
                tenderCargo.setCustomer(customer);
                if (customerCargo.getCustomer().getPersonalIDType() != null)
                {
                    String code = customerCargo.getCustomer().getPersonalIDType().getCode();
                    CodeEntryIfc codeEntry = customerCargo.getPersonalIDTypes().findListEntryByCode(code);
                    if (codeEntry != null)
                    {
                        LocalizedTextIfc localizedPersonalIDText = codeEntry.getLocalizedText();

                        LocalizedCodeIfc localizedPersonalIDCode = DomainGateway.getFactory().getLocalizedCode();
                        localizedPersonalIDCode.setCode(code);
                        localizedPersonalIDCode.setText(localizedPersonalIDText);
                        localizedPersonalIDCode.setCodeName(codeEntry.getCodeName());
                        tenderCargo.setLocalizedPersonalIDCode(localizedPersonalIDCode);
                        tenderCargo.setIdType(code);
                    }
                }
            }
        }
    }
}
