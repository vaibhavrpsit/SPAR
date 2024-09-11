/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/DisplaySpecialOrderCustomerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:01  mcs
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
 *    Rev 1.1   Sep 12 2003 15:29:02   rsachdeva
 * Customer Name
 * Resolution for POS SCR-2481: At Sp. ord. Item screen, linked customer name not shows on status region
 * 
 *    Rev 1.0   Aug 29 2003 16:07:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   May 06 2003 13:41:10   baa
 * updates for business customer
 * Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 * 
 *    Rev 1.3   May 02 2003 14:26:56   baa
 * add business customer hooks to special order
 * Resolution for POS SCR-2263:  Sp. Order, Link business customer, POS is crashed at Customer Options screen
 * 
 *    Rev 1.2   Feb 21 2003 09:35:36   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Sep 18 2002 17:15:24   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:02:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   26 Oct 2001 12:39:26   jbp
 * added email address to special order customer screen
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 14:55:10   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add;

// java imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
//------------------------------------------------------------------------------
/**
    Displays the special order customer screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplaySpecialOrderCustomerSite extends PosSiteActionAdapter

{

    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Displays the special order customer screen using customer data. If
        the customer does not have an address, then use the state and country
        data obtained from parameter values.
        <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =   (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();

        CustomerIfc customer = specialOrderCargo.getCustomer();
        MailBankCheckInfoBeanModel model = CustomerUtilities.copyCustomerToModel(customer,utility,pm);
           
        //model.setContactInfoOnly(true);
  
        // tell MailBankCheckBean that this is a special order or layaway transaction
        model.setLayawayFlag(true);

        // set the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        
        statusModel.setCustomerName(customer.getFirstLastName());
               
      
        model.setStatusBeanModel(statusModel);

        // display the special order customer screen
        ui.showScreen(POSUIManagerIfc.CUSTOMER_SPECIAL_ORDER, model);
    }
    

    
}
