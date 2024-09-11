/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/ShowAccountInquiryInfoSite.java /main/11 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/01/13 - Fixed failure to cancel House Account Transaction
 *                         when cancel button pressed or timout occurs.
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *      29661: Changes per code review.
 * 5    360Commerce 1.4         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *      29661: Encrypting, masking and hashing account numbers for House
 *      Account.
 * 4    360Commerce 1.3         1/25/2006 4:11:47 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:45     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:29:58     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:17     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:13     Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:40  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 04 2003 17:33:58   nrao
 * Code Review Changes.
 *
 *    Rev 1.0   Dec 03 2003 17:39:56   nrao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.InstantCreditInquiryBeanModel;

//--------------------------------------------------------------------------
/**
     This site shows the account information on the screen
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ShowAccountInquiryInfoSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        InstantCreditIfc card = cargo.getInstantCredit();

        InstantCreditInquiryBeanModel model = new InstantCreditInquiryBeanModel();
        // populate bean model
        model.setFirstName(card.getCustomer().getFirstName());
        model.setLastName(card.getCustomer().getLastName());
        model.setAccountNumber(card.getEncipheredCardData().getTruncatedAcctNumber());
        model.setCurrentBalance(card.getCurrentBalance());
        model.setCreditLimit(card.getCreditLimit());
        model.setCreditAvailable(card.getCreditLimit().subtract(card.getCurrentBalance()));

        // show screen
        ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_INQUIRY_INFO, model);
    }
}
