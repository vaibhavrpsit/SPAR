/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/CheckForCustomerRequiredSite.java /main/11 2012/08/27 11:22:59 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - wptg - removed placeholder from key NoLinkedCustomer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:00:32   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:31:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
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

//--------------------------------------------------------------------------
/**
    Based on a parameter, This site determines if tax exempt must have a
    customer linked to the transaction.
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class CheckForCustomerRequiredSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
        Parameter constant
    **/
    public static final String REQUIRES_CUSTOMER = "RequireCustomerLink";

    /**
        Error screen constant
    **/
    public static final String NO_LINKED_CUSTOMER = "NoLinkedCustomer";

    //----------------------------------------------------------------------
    /**
        Determines if the return must have a customer linked to the
        transaction.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();

        if (isCustomerRequired(bus) && !isCustomerAvailable(cargo))
        {
            /*
             * First, we should check to see if the original transaction
             * has a customer.  If it does, we should just assign him
             * to the current transaction.
             */
             RetailTransactionIfc transaction = cargo.getTransaction();
             if (transaction != null && transaction.getCustomer() != null)
             {
                  cargo.setCustomer(transaction.getCustomer());
                  bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
             }
             else
            {

                // build the screen
                DialogBeanModel model = new DialogBeanModel();

                model.setResourceID("NoLinkedCustomer");
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

    //----------------------------------------------------------------------
    /**
        Checks the Customer Required parameter.
        <P>
        @param BusIfc used to access the parameter manager
        @return boolean true if customer required
    **/
    //----------------------------------------------------------------------
        protected boolean isCustomerRequired(BusIfc bus)
        {
            boolean required = false;
            try
            {
               ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
               String value  = pm.getStringValue(REQUIRES_CUSTOMER);
               if (value.equalsIgnoreCase("Y"))
               {
                 required = true;
               }
            }
            catch (ParameterException e)
           {
            // Use default
           }

           return required;
       }

    //----------------------------------------------------------------------
    /**
        Checks if the customer is available.
        <P>
        @param ReturnOptionsCargo contains customer
        @return boolean true if a customer is available
    **/
    //----------------------------------------------------------------------
        protected boolean isCustomerAvailable(ModifyTransactionTaxCargo cargo)
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

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
