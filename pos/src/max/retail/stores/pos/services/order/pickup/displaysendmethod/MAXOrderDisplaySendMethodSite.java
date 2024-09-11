/******************************************************************************** 
	* Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
	*
	*
	* Rev   1.1     Dec 13, 2016    Ashish Yadav        Changes for Send FES
	* Rev   1.0     Oct 17, 2016    Ashish Yadav    	Code Merging 
	*********************************************************************************/
package max.retail.stores.pos.services.order.pickup.displaysendmethod;

// java imports
import java.util.Vector;

import max.retail.stores.domain.arts.MAXReadShippingMethodTransaction;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.pos.appmanager.send.MAXSendManagerIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXOrderShippingMethodBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadShippingMethodTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
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


public class MAXOrderDisplaySendMethodSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2974778117333027439L;
	/**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 9$";
    /**
        offline shipping method prompt tag
    **/
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT_TAG = "AlternateShippingMethodPromptTag";
    /**
        offline shipping method prompt
    **/
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT =
      "Enter the shipping method and shipping charge and press Done.";

    //--------------------------------------------------------------------------
    /**
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
          ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
          POSUIManagerIfc     ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
          UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

          SendCargo cargo = (SendCargo)bus.getCargo();

          MAXSendManagerIfc sendMgr = null;
          try
          {
              sendMgr = (MAXSendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
          }
          catch (ManagerException e)
          {
              // default to product version
              sendMgr = (MAXSendManagerIfc) new SendManager();
          }

          // Retrieve shipping calculation and shipping charge parameter
          String shippingCalculation = sendMgr.getShippingCalculationType(pm);
          CurrencyIfc shippingCharge = sendMgr.getShippingCharge(shippingCalculation,
                                                                 cargo.getLineItems());
          cargo.setPartialShippingCharges(shippingCharge);
          cargo.setParameter(shippingCalculation);

          CustomerIfc shipToCustomer = cargo.getShipToInfo();

          PromptAndResponseModel prompt =  new PromptAndResponseModel();

          MAXOrderShippingMethodBeanModel model = null;
          if ( ui.getModel() instanceof MAXOrderShippingMethodBeanModel )
          {
              model = (MAXOrderShippingMethodBeanModel) ui.getModel(MAXPOSUIManagerIfc.MAX_ORDER_SHIPPING_METHOD);
          }
          else
          {
              model = new MAXOrderShippingMethodBeanModel();
              model.setFirstName(shipToCustomer.getFirstName());
              model.setLastName(shipToCustomer.getLastName());
              if (!Util.isEmpty(shipToCustomer.getCompanyName())
                  && !shipToCustomer.isBusinessCustomer())
              {
                  model.setOrgName(shipToCustomer.getCompanyName());
              }
          }
          Vector addressVector = shipToCustomer.getAddresses();
          if (!addressVector.isEmpty())
          {
                AddressIfc addr = (AddressIfc) addressVector.elementAt(0);
                Vector lines = addr.getLines();
                if (lines.size() >= 1)
                {
                    model.setAddressLine1((String) lines.elementAt(0));
                }
                if (lines.size() >= 2)
                {
                    model.setAddressLine2((String) lines.elementAt(1));
                }
                if (lines.size() >= 3)
                {
                    model.setAddressLine3((String) lines.elementAt(2));
                }
                model.setCity(addr.getCity());

                int countryIndex = utility.getCountryIndex(addr.getCountry(),pm);
                model.setCountryIndex(countryIndex);
                model.setStateIndex(utility.getStateIndex(countryIndex,addr.getState(), pm));
                model.setCountries(utility.getCountriesAndStates(pm));

                model.setPostalCode(addr.getPostalCode());
                // Changes starts for code merging(commenting below line, not sure whether to use it or not)
               // model.setExtPostalCode(addr.getPostalCodeExtension());
                // Changes ends for code merging

          }
          model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
          model.setCalculationType(cargo.getParameter());
          model.setItemsShippingCharge(cargo.getPartialShippingCharges());
          ShippingMethodIfc methods[] = null;
          try
          {
               ReadShippingMethodTransaction shippingTransaction = null;

               shippingTransaction = (ReadShippingMethodTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_SHIPPING_METHOD_TRANSACTION);
            // Changes start for Rev 1.1 (Ashish : Send)
               LocaleRequestor localeReq = utility.getRequestLocales();
               ShippingMethodSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getShippingMethodSearchCriteria();
               searchCriteria.setLocaleRequestor(localeReq);
            // Changes end for Rev 1.1 (Ashish : Send)

               // get list of items matching search criteria from database
               // Changes start for Rev 1.1 (Ashish : Send)
               methods = (ShippingMethodIfc[]) (((MAXReadShippingMethodTransaction)shippingTransaction).readShippingMethod(searchCriteria));
            // Changes ends for Rev 1.1 (Ashish : Send)
               // If shipcharges were not obtained from database, set appropriate
               // prompt. Also get internationalized text from code list.
               CodeListIfc shippingList = cargo.getSendShippingMethods();
               for (int i=0; i < methods.length; i++)
               {
                  if (methods[i].getBaseShippingCharge() == null)
                  {
                     model.setOffline(true);
                     prompt.setPromptText
                       (utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                             BundleConstantsIfc.SEND_BUNDLE_NAME,
                                             ALTERNATE_SHIPPING_METHOD_PROMPT_TAG,
                                             ALTERNATE_SHIPPING_METHOD_PROMPT));
                     model.setPromptAndResponseModel(prompt);
                  }

                  // Update text for internationalization
                  String code = Integer.toString(methods[i].getShippingMethodID());
                  CodeEntryIfc shippingCodeEntry = shippingList.findListEntryByCode(code);
                  if ((shippingCodeEntry != null) && (shippingCodeEntry.getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)) != null))
                  {
                      methods[i].setShippingType(shippingCodeEntry.getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
                  }
               }
        }
        catch (DataException e)
        {
           // If database errors are found use offline screeen
           model.setOffline(true);
           prompt.setPromptText
               (utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                     BundleConstantsIfc.SEND_BUNDLE_NAME,
                                     ALTERNATE_SHIPPING_METHOD_PROMPT_TAG,
                                     ALTERNATE_SHIPPING_METHOD_PROMPT));
           model.setPromptAndResponseModel(prompt);
           // Retrieve shipping methods from code list flat file.
           // get reason code list from cargo
           CodeListIfc rcl = cargo.getSendShippingMethods();
           methods = setShippingMethods(rcl.getEntries());
        }
        finally
        {
            NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
            globalModel.setButtonEnabled(CommonLetterIfc.NEXT,false);

            model.setShipMethodsList((oracle.retail.stores.domain.shipping.ShippingMethodIfc[]) methods);

            if ( cargo.isItemUpdate() )
            {
                TransactionTotalsIfc totals = cargo.getTransaction().getTransactionTotals();
                ShippingMethodIfc method = (ShippingMethodIfc) ((MAXTransactionTotalsIfc) totals).getSendPackages()[cargo.getSendIndex()-1].getShippingMethod();
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
                
                globalModel.setButtonEnabled(CommonLetterIfc.CANCEL, false);
                globalModel.setButtonEnabled(CommonLetterIfc.UNDO, false);
            }

            model.setGlobalButtonBeanModel(globalModel);

            ui.showScreen(MAXPOSUIManagerIfc.MAX_ORDER_SHIPPING_METHOD, model);
        }
    }

    protected ShippingMethodIfc[] setShippingMethods(CodeEntryIfc[] methodList)
    {
        ShippingMethodIfc list[] = new ShippingMethodIfc[methodList.length];
        for ( int i=0;  i < methodList.length; i++ )
        {
          CodeEntryIfc entry = methodList[i];
          list[i] = (ShippingMethodIfc) DomainGateway.getFactory().getShippingMethodInstance();
          list[i].setShippingType(entry.getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
          list[i].setBaseShippingCharge(DomainGateway.getBaseCurrencyInstance());
        }
        return list;
   }

}
