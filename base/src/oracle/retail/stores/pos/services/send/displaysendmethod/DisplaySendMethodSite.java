/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/DisplaySendMethodSite.java /main/22 2013/05/02 10:47:37 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
 *                         the description to retail price
 *                         modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
 *    yiqzhao   03/13/13 - Add reason code for shipping charge override for
 *                         cross channel and store send.
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - add previous changes for supporting multiple address
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/25/10 - additional fixes for the process order flow
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   02/18/09 - do not set shipping method selected if there are no
 *                         shipping methods
 *
 * ===========================================================================
 * $Log:
 * 9    360Commerce 1.8         5/7/2007 2:21:04 PM    Sandy Gu        enhance
 *      shipping method retrieval and internal tax engine to handle tax rules
 * 8    360Commerce 1.7         5/1/2007 12:15:40 PM   Brett J. Larsen CR 26474
 *       - Tax Engine Enhancements for Shipping Carge Tax (for VAT feature)
 *
 * 7    360Commerce 1.6         4/25/2007 8:51:34 AM   Anda D. Cadar   I18N
 *      merge
 * 6    360Commerce 1.5         3/16/2006 5:59:14 AM   Akhilashwar K. Gupta
 *      CR-3995: Updated "arrive()" method as per QA Code review comment.
 * 5    360Commerce 1.4         3/2/2006 4:31:11 AM    Akhilashwar K. Gupta
 *      CR-3995: Fixed to remove duplicate First Name and Last Name
 * 4    360Commerce 1.3         3/2/2006 4:08:41 AM    Akhilashwar K. Gupta
 *      CR-3995: Updated to remove duplicate setting of Customer Name
 * 3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse
 *
 * Revision 1.11  2004/09/20 17:20:39  rsachdeva
 * @scr 7210 Queued Transaction Send generates QueueException when DB connection is restored
 *
 * Revision 1.10  2004/07/30 20:01:57  jdeleau
 * @scr 6630 When hitting escape on a send item transaction, reset the
 * tax back to its original value.
 *
 * Revision 1.9  2004/06/22 17:28:10  lzhao
 * @scr 4670: code review
 *
 * Revision 1.8  2004/06/14 23:35:26  lzhao
 * @scr 4670: fix shipping charge calculation.
 *
 * Revision 1.7  2004/06/07 18:28:37  jdeleau
 * @scr 2775 Support multiple GeoCodes tax screen
 *
 * Revision 1.6  2004/06/04 20:23:44  lzhao
 * @scr 4670: add Change send functionality.
 *
 * Revision 1.5  2004/06/03 14:47:46  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.4  2004/06/03 13:29:21  lzhao
 * @scr 4670: delete send item.
 *
 * Revision 1.3  2004/06/02 19:06:51  lzhao
 * @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 * Revision 1.2  2004/05/28 16:42:52  rsachdeva
 * @scr 4670 Send: Multiple Sends
 *
 * Revision 1.1  2004/05/26 16:37:47  lzhao
 * @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 * Revision 1.8  2004/05/11 19:06:52  rsachdeva
 * @scr 4670 Send: Multiple Sends
 *
 * Revision 1.7  2004/05/05 13:27:46  rsachdeva
 * @scr 4670 Send: Multiple Sends
 *
 * Revision 1.6  2004/04/20 13:17:05  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.5  2004/04/14 15:17:10  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.4  2004/03/03 23:15:09  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:51:55  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:52:29  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:06:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 08 2003 12:56:04   baa
 * I18n phone types
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Mar 19 2003 12:13:46   HDyer
 * Update shipping method texts for internationalization.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Feb 21 2003 09:35:34   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 19 2002 09:46:58   baa
 * use new method for getting country/state info
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:04:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:06   msg
 * Initial revision.
 *
 *    Rev 1.6   Mar 10 2002 09:37:24   mpm
 * Externalized text.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.5   03 Jan 2002 14:22:34   baa
 * cleanup code
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.4   19 Dec 2001 17:41:54   baa
 * updates for send
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.3   17 Dec 2001 19:13:52   baa
 * updates to ui
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   13 Dec 2001 17:59:50   baa
 * updates to support offline
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   06 Dec 2001 18:48:44   baa
 * additional updates for  send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   04 Dec 2001 17:22:54   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.displaysendmethod;

import java.util.List;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.manager.shipping.ShippingManagerIfc;
import oracle.retail.stores.domain.shipping.ShippingItemIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingRequest;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;

/**
 * Site that displays the methods in which a {@link SendPackageLineItemIfc} can
 * be sent.
 */
public class DisplaySendMethodSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5714079227056501715L;

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * offline shipping method prompt tag
     */
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT_TAG = "AlternateShippingMethodPromptTag";

    /**
     * offline shipping method prompt
     */
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT = "Enter the shipping method and shipping charge and press Done.";
    
    /**
     * Default Shipping Charge Service Item ID
     */
    public static final String DEFAULT_SHIPPING_CHARGE_ITEM_ID = "ShippingChargeItemID";

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
          ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
          POSUIManagerIfc     ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
          UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

          SendCargo cargo = (SendCargo)bus.getCargo();

          SendManagerIfc sendMgr = null;
          try
          {
              sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
          }
          catch (ManagerException e)
          {
              // default to product version
              sendMgr = new SendManager();
          }

          CustomerIfc shipToCustomer = cargo.getShipToInfo();

          PromptAndResponseModel prompt =  new PromptAndResponseModel();

          ShippingMethodBeanModel model = null;
          if ( ui.getModel() instanceof ShippingMethodBeanModel )
          {
              model = (ShippingMethodBeanModel) ui.getModel(POSUIManagerIfc.SHIPPING_ADDRESS);
          }
          else
          {
              model = new ShippingMethodBeanModel();
              model.setFirstName(shipToCustomer.getFirstName());
              model.setLastName(shipToCustomer.getLastName());
              if (!Util.isEmpty(shipToCustomer.getCompanyName()) && !shipToCustomer.isBusinessCustomer())
              {
                  model.setOrgName(shipToCustomer.getCompanyName());
              }
          }
          AddressIfc addr = shipToCustomer.getPrimaryAddress();
          if (addr != null)
          {
              Vector<String> lines = addr.getLines();
              if (lines.size() >= 1)
              {
                  model.setAddressLine1(lines.get(0));
              }
              if (lines.size() >= 2)
              {
                  model.setAddressLine2(lines.get(1));
              }
              if (lines.size() >= 3)
              {
            	  model.setAddressLine3(lines.get(2));
              }
              model.setCity(addr.getCity());
              
              int countryIndex = utility.getCountryIndex(addr.getCountry(),pm);
              model.setCountryIndex(countryIndex);
              model.setStateIndex(utility.getStateIndex(countryIndex,addr.getState(), pm));
              model.setCountries(utility.getCountriesAndStates(pm));
              
              model.setPostalCode(addr.getPostalCode());
          }
          model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
          model.setCalculationType(cargo.getParameter());
          model.setItemsShippingCharge(cargo.getPartialShippingCharges());
          
          CodeListIfc reasonCodeList = getReasonCodes(cargo.getStoreStatus().getStore().getStoreID(), 
                                                      CodeConstantsIfc.CODE_LIST_SHIPPING_PRICE_OVERRIDE_REASON_CODES);
          cargo.setShippingChargeReasonCodes(reasonCodeList);
          
          model.inject(reasonCodeList, "", LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
          
          ShippingMethodIfc[] methods = null;

          try
          {
               // get list of items matching search criteria from database
               LocaleRequestor localeReq = utility.getRequestLocales();
               localeReq.setSortByLocale(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));


               List<ShippingItemIfc> shippingItems = sendMgr.getShippingItems(cargo.getLineItems());
               
               ShippingManagerIfc shippingsManager =
                       (ShippingManagerIfc)Gateway.getDispatcher().getManager(ShippingManagerIfc.TYPE); 
               ShippingRequest request = new ShippingRequest();
               request.setShippingItems(shippingItems);
               request.setShippingLocaleRequetor(localeReq);
               methods = shippingsManager.getStoreSendShippingMethods(request);
               // If shipcharges were not obtained from database, set appropriate prompt.
               for (int i=0; i < methods.length; i++)
               {
                  if (methods[i].getBaseShippingCharge() == null)
                  {
                     model.setOffline(true);
                     prompt.setPromptText(utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC, BundleConstantsIfc.SEND_BUNDLE_NAME, ALTERNATE_SHIPPING_METHOD_PROMPT_TAG,
                             ALTERNATE_SHIPPING_METHOD_PROMPT));
                     model.setPromptAndResponseModel(prompt);
                  }
               }
          } catch (DataException e)
          {
              // If database errors are found use offline screeen
              model.setOffline(true);
              prompt.setPromptText(utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC, BundleConstantsIfc.SEND_BUNDLE_NAME, ALTERNATE_SHIPPING_METHOD_PROMPT_TAG,
                      ALTERNATE_SHIPPING_METHOD_PROMPT));
              model.setPromptAndResponseModel(prompt);
              // Retrieve shipping methods from code list flat file.
              // get reason code list from cargo
              CodeListIfc rcl = cargo.getSendShippingMethods();
              methods = toShippingMethods(rcl.getEntries());        	  
          }
          finally
          {
            NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
            globalModel.setButtonEnabled(CommonLetterIfc.NEXT,false);

            model.setShipMethodsList(methods);

            if ( cargo.isItemUpdate() )
            {
                ShippingMethodIfc method = cargo.getTransaction().getSendPackages()[cargo.getSendIndex()-1].getShippingMethod();
                int selectedIndex = 0;
                for ( int i = 0; i < methods.length; i++ )
                {
                    if ( method.getShippingMethodID() == methods[i].getShippingMethodID() )
                    {
                        selectedIndex = i;
                        break;
                    }
                }
                model.setSelectedShipMethod(selectedIndex);
                if (!Util.isEmpty(method.getShippingInstructions()))
                {
                    model.setInstructions(method.getShippingInstructions());
                }
                globalModel.setButtonEnabled(CommonLetterIfc.CANCEL, false);
                globalModel.setButtonEnabled(CommonLetterIfc.UNDO, false);
            }

            model.setGlobalButtonBeanModel(globalModel);

            ui.showScreen(POSUIManagerIfc.SHIPPING_METHOD, model);
        }
    }

 
    /**
     * Convert the given codes into shipping methods.
     *
     * @param methodList
     * @return
     */
    protected ShippingMethodIfc[] toShippingMethods(CodeEntryIfc[] methodList)
    {
        ShippingMethodIfc list[] = new ShippingMethodIfc[methodList.length];
        for ( int i=0;  i < methodList.length; i++ )
        {
          CodeEntryIfc entry = methodList[i];
          list[i] = DomainGateway.getFactory().getShippingMethodInstance();
          list[i].setLocalizedShippingTypes(entry.getLocalizedText());
          list[i].setBaseShippingCharge(DomainGateway.getBaseCurrencyInstance());
        }
        return list;
   }
      
    /**
     * Convenience method to retrieve reason codes for the specified code list
     * type based upon the store ID of the current store status.
     *
     * @param codeListType <code>String</code> containing the name of the code list.
     * 
     * @return code list unless {@link #getStoreStatus()} is null.
     */
    protected CodeListIfc getReasonCodes(String storeID, String codeListType)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        return utility.getReasonCodes(storeID, codeListType);
    }    

}
