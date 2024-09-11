/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/GetAlterationsCustomerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/06/17 18:57:34  aschenk
 *   @scr 5674 - made changes to allow the user to reenter the customer information if esc was hit from the Alteration type options screen.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 21 2002 11:21:22   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks to see if a customer is associated with the transaction
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetAlterationsCustomerSite extends PosSiteActionAdapter
{
    /**
        revision Number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        class name constant
    **/
    public static final String SITENAME = "GetAlterationsCustomerSite";

    //----------------------------------------------------------------------
    /**
     *   Determine if a Customer is associated with the transaction.
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        AlterationsCargo cargo = (AlterationsCargo)bus.getCargo();

        // Local variables
        SaleReturnTransactionIfc transaction;
        CustomerIfc customer;

        // String representing the name of the letter to mail
        // Default the letter to Failure.
        String letterName = CommonLetterIfc.FAILURE;

        if (cargo.getTransaction()!=null) // Does a transaction exist
        {
            // Get the transaction
            transaction = (SaleReturnTransactionIfc) cargo.getTransaction();

            if (transaction.getCustomer()!=null && !bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO)) // Does a customer exist in the transaction
            {
                // Get the customer from the transaction
                customer = transaction.getCustomer();

                // Set the customer in the cargo
                cargo.setCustomer(customer);

                // Set the letter to Continue
                letterName = CommonLetterIfc.CONTINUE;
            }
        }

        // Mail the letter
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}
