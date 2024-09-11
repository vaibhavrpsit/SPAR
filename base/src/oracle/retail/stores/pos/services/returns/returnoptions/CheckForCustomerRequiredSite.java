/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/CheckForCustomerRequiredSite.java /main/14 2012/08/27 11:23:00 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - wptg - removed place holder from key
 *                         NoLinkedCustomer
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   12/16/08 - Rename parameters FormOfIDRequiredForRetrievedReturn
 *                         to FormOfIDForRetrievedReturn and
 *                         FormOfIDRequiredForNonRetrievedReturn to
 *                         FormOfIDForNonretrievedReturn
 *    mdecama   12/03/08 - Using new Parameters -
 *                         FormOfIdRequiredForRetrievedReturns and
 *                         FormOfIdRequiredForNonRetrievedReturns
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse
 *
 *   Revision 1.7  2004/07/27 19:45:40  jdeleau
 *   @scr 6305 Flow corrections on return without a receipt for linked customer.
 *
 *   Revision 1.6  2004/06/25 15:32:16  cdb
 *   @scr 4286 Updated flow of Returns with customer required when offline.
 *
 *   Revision 1.5  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Apr 09 2003 12:48:28   HDyer
 * Changes from code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.1   Dec 13 2002 15:57:18   HDyer
 * Modified the parameter checked to determine if customer is required for the return.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.0   Apr 29 2002 15:04:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:10   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 10 2002 18:01:16   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Jan 30 2002 20:50:30   dfh
 * use nolinkedcustomer instead of customermustlink -
 * issue 539
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   03 Dec 2001 18:36:46   jbp
 * changed customer not linked dialog screen,
 * removed RerurnNoLinkedCustomer dialog screen
 * Resolution for POS SCR-335: Layaway Updates
 *
 *    Rev 1.0   Sep 21 2001 11:25:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Based on a parameter, This site determines if the return must have a customer
 * linked to the transaction.
 * 
 * @version $Revision: /main/14 $
 */
public class CheckForCustomerRequiredSite extends PosSiteActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 5774960505796542439L;

    /**
     * revision number
     **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Customer letter constant
     **/
    public static final String CUSTOMER = "Customer";

    /**
     * Error screen constant
     */
    public static final String RETURN_NO_LINKED_CUSTOMER = "NoLinkedCustomer";

    /**
     * Determines if the return must have a customer linked to the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        cargo.setCustomerMustLink(isCustomerRequired(pm, cargo));
        if (cargo.isCustomerMustLink() && !isCustomerAvailable(cargo))
        {
            /*
             * First, we should check to see if the original transaction
             * has a customer.  If it does, we should just assign him
             * to the current transaction.
             */
            RetailTransactionIfc transaction = cargo.getOriginalTransaction();
            if (transaction != null && transaction.getCustomer() != null)
            {
                cargo.setCustomer(transaction.getCustomer());
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
            else if(cargo.getTriedLinkingCustomer() == false) // Go directly to the customer page
            {
                cargo.setTriedLinkingCustomer(true);
                bus.mail(new Letter(CommonLetterIfc.YES), BusIfc.CURRENT);
            }
            else
            {

                // build the screen
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(RETURN_NO_LINKED_CUSTOMER);
                model.setType(DialogScreensIfc.CONFIRMATION);

                // display the screen
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * Checks the ID Required parameter.
     * 
     * @param pm used to access the parameter manager
     * @return boolean true if customer required
     */
    protected boolean isCustomerRequired(ParameterManagerIfc pm, ReturnOptionsCargo cargo)
    {
        boolean required = false;
        String value = ParameterConstantsIfc.RETURN_FormOfID_NONE;
        try
        {
        	if (cargo.areAllItemsFromTransaction())
        	{
        		value  = pm.getStringValue(ParameterConstantsIfc.RETURN_FormOfIDForRetrievedReturn);
        	}
        	else
        	{
        		value  = pm.getStringValue(ParameterConstantsIfc.RETURN_FormOfIDForNonretrievedReturn);
        	}
        }
        catch (ParameterException e)
        {
            // Use default
        }

        // Parameter may have more than one value containing 'CustomerID', so
        // look for that string to be contained rather than testing for equal
        int index = value.indexOf(ParameterConstantsIfc.RETURN_FormOfID_CUSTOMER_ID);
        if (index != -1)
        {
            required = true;
        }
        return required;
    }

    /**
     * Checks if the customer is available.
     * 
     * @param cargo contains ReturnsOptionsCargo
     * @return boolean true if a customer is available
     */
    protected boolean isCustomerAvailable(ReturnOptionsCargo cargo)
    {
        boolean available = false;

        // The customer might be in the current transaction or it
        // might be in the cargo, if no transaction has been created yet.
        if (cargo.getTransaction() != null &&
            cargo.getTransaction().getCustomer() != null)
        {
            available = true;
        }
        else if (cargo.getCustomer() != null)
        {
            available = true;
        }

        return available;
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
