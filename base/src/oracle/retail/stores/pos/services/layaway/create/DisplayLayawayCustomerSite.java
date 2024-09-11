/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/create/DisplayLayawayCustomerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.2  2004/02/12 16:50:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.10   May 06 2003 13:41:08   baa
 * updates for business customer
 * Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 * 
 *    Rev 1.9   Apr 02 2003 17:50:44   baa
 * customer and screen changes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.8   Mar 21 2003 10:58:32   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.7   Mar 20 2003 18:18:54   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.6   Feb 21 2003 09:35:32   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.5   Feb 19 2003 13:52:26   crain
 * Replaced abbreviations
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.4   Jan 23 2003 18:25:38   crain
 * Changed the screen name for business customer
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.3   Jan 21 2003 15:40:02   crain
 * Changed to accomodate business customer
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.2   Sep 18 2002 17:15:22   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 29 2002 08:46:26   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:21:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.create; 

// java imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
 
//------------------------------------------------------------------------------ 
/** 
    Displays the layaway customer screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/ 
//------------------------------------------------------------------------------ 
public class DisplayLayawayCustomerSite extends PosSiteActionAdapter 
{ 
    /** 
        class name constant 
    **/ 
    public static final String SITENAME = "DisplayLayawayCustomerSite"; 
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $"; 
    /** 
        phone constant 
        @deprecated as of release 6.0
    **/ 
    public static final int PHONE_TYPE_HOME = 0;
  
    
    //-------------------------------------------------------------------------- 
    /** 
        Displays the layaway customer screen.
        <P> 
        @param bus the bus arriving at this site 
    **/ 
    //-------------------------------------------------------------------------- 
    public void arrive(BusIfc bus) 
    { 
        POSUIManagerIfc ui = 
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE); 
        ParameterManagerIfc pm =   (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();                       
        
        CustomerIfc customer = layawayCargo.getCustomer();  
        MailBankCheckInfoBeanModel model = CustomerUtilities.copyCustomerToModel(customer,utility,pm);
        
 
        
        // tell MailBankCheckBean that this is a Layaway transaction
        model.setLayawayFlag(true);
        
        // set the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();

        String custScreenName = utility.retrieveText("StatusPanelSpec"
                                     ,BundleConstantsIfc.LAYAWAY_BUNDLE_NAME
                                     ,"LayawayCustomerScreenName"
                                     ,"Layaway Customer"
                                     ,LocaleConstantsIfc.USER_INTERFACE); 

        String busCustScreenName = utility.retrieveText("StatusPanelSpec"
                                     ,BundleConstantsIfc.LAYAWAY_BUNDLE_NAME
                                     ,"LayawayBusCustomerScreenName"
                                     ,"Layaway Business"
                                     ,LocaleConstantsIfc.USER_INTERFACE); 

        if (model.isBusinessCustomer())
        {
            statusModel.setScreenName(busCustScreenName);
        }
        else
        {
            statusModel.setScreenName(custScreenName);
        }

        statusModel.setCustomerName(model.getCustomerName());

        model.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.CUSTOMER_LAYAWAY, model);
    }
} 


