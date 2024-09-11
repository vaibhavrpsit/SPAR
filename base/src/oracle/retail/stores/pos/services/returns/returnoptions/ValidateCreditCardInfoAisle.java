/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ValidateCreditCardInfoAisle.java /rgbustores_13.4x_generic_branch/3 2011/08/11 19:22:51 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    08/04/11 - use of ReturnByCreditBeanModel
 *    jswan     07/15/11 - Modified to support changes to lookup transaction by
 *                         account number including account number token.
 *    asinton   12/20/10 - XbranchMerge asinton_bug-10407292 from
 *                         rgbustores_13.3x_generic_branch
 *    asinton   12/17/10 - deprecated hashed account ID.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    7    360Commerce 1.6         12/27/2007 10:39:29 AM Alan N. Sinton  CR
 *         29677: Check in changes per code review.  Reviews are Michael
 *         Barnett and Tony Zgarba.
 *    6    360Commerce 1.5         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    5    360Commerce 1.4         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *         29677: Protect user entry fields of PAN data.
 *    4    360Commerce 1.3         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *         29598: changes for credit/debit PAPB
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.10  2004/07/29 00:28:23  blj
 *   @scr 5828 - added ability to get training mode status from register if transaction is null.
 *
 *   Revision 1.9  2004/07/12 20:13:55  mweis
 *   @scr 6158 "Gift Card ID:" label not appearing correctly
 *
 *   Revision 1.8  2004/05/21 20:56:27  mweis
 *   @scr 4902 Returns' INVALID_CARD_NUMBER message and key prompt incorrect
 *
 *   Revision 1.7  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.6  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.5  2004/03/09 17:23:47  baa
 *   @scr 3561 Add bin range, check digit and bad swipe dialogs
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 23 2004 16:10:16   baa
 * continue returns developement
 * 
 *    Rev 1.1   Dec 29 2003 15:36:26   baa
 * return enhancements
 * 
 *    Rev 1.0   Dec 17 2003 11:37:18   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReturnByCreditBeanModel;


//------------------------------------------------------------------------------
/**
    Validates Credit Card info to be used on search by tender type    
    @version $Revision: /rgbustores_13.4x_generic_branch/3 $
**/
//------------------------------------------------------------------------------

@SuppressWarnings("serial")
public class ValidateCreditCardInfoAisle extends PosLaneActionAdapter
{
    /**
     * field name of that contains the account information
     */
    public static final String FIRST_DIGITS     = "firstCardDigitsField";
 
    /**
     * field name of that contains the account information
     */
    public static final String LAST_DIGITS     = "lastCardDigitsField";
 
    /**
     * credit card text
     */
    public static final String CREDIT_CARD       = "CreditCardText";
    
    /**
     * invalid number dialog key
     */
    public static final String INVALID_CARD_NUMBER  = "InvalidReturnsNumberError";
    
    /**
     * "Common" message key to text to use when the account information is invalid
     */
    public static final String CARD_NUMBER_TEXT = "CreditDebitCardNumber";

    //--------------------------------------------------------------------------
    /**
         Validates Credit Info
         @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnByCreditBeanModel model = (ReturnByCreditBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_CREDIT);

        // Retrieve data from model and cargo
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        String first = model.getFirstCardDigits();
        String last = model.getLastCardDigits();
        
        String maskedAccountNumber = first + DomainUtil.getMaskChar() + last;

        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
        if (searchCriteria == null)
        {
           searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();   
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        
        int selection = model.getDateRangeIndex();
        searchCriteria.setDateRange(ReturnUtilities.calculateDateRange(selection, pm));
        
        searchCriteria.setMaskedAccountNumber(maskedAccountNumber);
        searchCriteria.setItemNumber(model.getItemNumber());
        cargo.setSearchCriteria(searchCriteria);
        cargo.setSearchByTender(true);
        cargo.setHaveReceipt(false);                
        bus.mail(new Letter(CommonLetterIfc.VALIDATE), BusIfc.CURRENT);
    }

}
