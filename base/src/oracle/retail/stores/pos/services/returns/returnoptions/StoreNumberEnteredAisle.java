/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/StoreNumberEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:29 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 16:06:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class StoreNumberEnteredAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4831657275328007672L;


    //--------------------------------------------------------------------------
    /**     Gets the store number from UI
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {


        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();

        // Get store number from UI
        String storeNumber = ui.getInput();

        // Create a new object search criteria and set store number and date range
        SearchCriteriaIfc searchCriteria =
            DomainGateway.getFactory().getSearchCriteriaInstance();
        searchCriteria.setStoreNumber(storeNumber);
        searchCriteria.setDateRange(null);

        // set the search criteria in the cargo
        cargo.setSearchCriteria(searchCriteria);

        Letter letter = new Letter(CommonLetterIfc.LINK);

        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        if (transaction != null)
        {
            CustomerIfc customer = transaction.getCustomer();
            if (customer != null)
            {
                searchCriteria.setCustomer(customer);
                letter = new Letter(CommonLetterIfc.CUSTOMER);
            }
        }

        bus.mail(letter, BusIfc.CURRENT);

    }
}
