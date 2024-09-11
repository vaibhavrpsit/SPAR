/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.

  Rev 1.0     Deepshikha Singh   28/09/2015    Initial Draft:Changes done for loyalty points redeem  
* Rev 1.1	Ashish yadav	12/09/16	Changes done for code merging
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.RegisterMode;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.AuthorizableTenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCheckADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCreditADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupDebitADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupGiftCardADO;
import oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

public abstract class MAXAbstractRetailTransactionADO extends AbstractRetailTransactionADO {
	
// Changes starts for rev 1.1
protected TenderADOIfc[] getAuthPendingTenderLineItems()
    {
        List tenderList = new ArrayList(10);

        // If in training mode, simply return an empty array
        RegisterADO registerADO = getContext().getRegisterADO();
        if (registerADO.isInMode(RegisterMode.TRAINING))
        {
            // convert list to array and return
            TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
            result = (TenderADOIfc[]) tenderList.toArray(result);
            return result;
        }

        // iterate through all groups and add every tender to the result
        Iterator iter = tenderGroupMap.values().iterator();
        // the allowable tenders are orderable
        // giftcard, credit, check, and then debit
        TenderGroupADOIfc debitGroup = null;
        TenderGroupADOIfc checkGroup = null;
       // TenderGroupADOIfc giftCardGroup = null;
        TenderGroupADOIfc creditGroup = null;
      
        
        
        while (iter.hasNext())
        {
            TenderGroupADOIfc group = (TenderGroupADOIfc) iter.next();
            //if(group )
            
            if (group instanceof TenderGroupDebitADO)
            {
                debitGroup = group;
            }
            else if (group instanceof TenderGroupCheckADO)
            {
                checkGroup = group;
            }
            else if (group instanceof TenderGroupCreditADO)
            {
                creditGroup = group;
            }
            //change for remove gift card from authorize group.
//            else if (group instanceof TenderGroupGiftCardADO)
//            {
//            break;
//               // giftCardGroup = group;
//            }
            else if (!(group instanceof TenderGroupGiftCardADO) && group instanceof AuthorizableTenderGroupADOIfc)
            {
                pullAuthPendingTendersFromGroup(tenderList, group);
            }
        }
        // add gift card
       /* if (giftCardGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, giftCardGroup);
        }*/
        // add credit
        if (creditGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, creditGroup);
        }
        // add check
        if(checkGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, checkGroup);
        }
        // now add the debit group so it is LAST!
        if (debitGroup != null)
        {
            pullAuthPendingTendersFromGroup(tenderList, debitGroup);
        }

        // convert list to array and return
        TenderADOIfc[] result = new TenderADOIfc[tenderList.size()];
        result = (TenderADOIfc[]) tenderList.toArray(result);
        return result;
    }
// Changes ends for rev 1.1
	public TenderTypeEnum[] getEnabledTenderOptions()
    {
        // temporary list. initialize to a size that can hold
        // all tender types
        ArrayList tenderList = new ArrayList(14);

        // local String constants
        final String NONE = "None";

        // create utility object
        UtilityIfc util = getUtility();

        ///////
        // Cash
        String[] cashAccepted = util.getParameterValueList("CashAccepted");
        if (cashAccepted == null)
        {
            // initialize to take base currency
            cashAccepted = new String[1];
            cashAccepted[0] = NONE;
        }
        // if our base cash description exists as one of the cash accepted options, add to list
        if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), cashAccepted))
        {
            tenderList.add(TenderTypeEnum.CASH);
        }

        ////////
        // Check
        String[] checksAccepted = util.getParameterValueList("ChecksAccepted");
        if (checksAccepted == null)
        {
            checksAccepted = new String[1];
            checksAccepted[0] = NONE;
        }
        else
        {
            checksAccepted = extractCheckCurrencyPart(checksAccepted);
        }
        // Add Check to the list if the base currency is listed in the check currencies
        if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), checksAccepted))
        {
            tenderList.add(TenderTypeEnum.CHECK);
        }

        /////////
        // Non-Store Coupon
        if (util.getParameterValue("CouponsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.COUPON);
        }

        /////////
        // Credit
        if (util.getParameterValue("CreditCardsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.CREDIT);
        }

        ////////
        // Debit
        if (util.getParameterValue("DebitCardsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.DEBIT);
        }

        ////////////
        // Gift Card
        if (util.getParameterValue("GiftCardsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.GIFT_CARD);
        }

        ////////
        // Gift Cert
        String[] gcAccepted = util.getParameterValueList("GiftCertificatesAccepted");
        if (gcAccepted == null)
        {
            gcAccepted = new String[1];
            gcAccepted[0] = NONE;
        }
        // Add gift certificate to the list if the base currency is listed in the gift certificate currencies
        if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), gcAccepted))
        {
            tenderList.add(TenderTypeEnum.GIFT_CERT);
        }

        /////////////////
        // Purchase Order
        if (util.getParameterValue("PurchaseOrdersAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.PURCHASE_ORDER);
        }

        ///////////////
        // Store Credit
        /** if (util.getParameterValue("StoreCreditsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        } **/

        String[] scAccepted = util.getParameterValueList("StoreCreditsAccepted");
        if (scAccepted == null)
        {
            scAccepted = new String[1];
            scAccepted[0] = NONE;
        }

        // Add store credit to the list if the base currency is listed in the store credit currencies
        if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), scAccepted))
        {
            tenderList.add(TenderTypeEnum.STORE_CREDIT);
        }

        ///////////////
        // Mall Certificate
        if (util.getParameterValue("MallCertificateAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.MALL_CERT);
        }

        ///////////////
        // Money Order
        if (util.getParameterValue("MoneyOrderAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(TenderTypeEnum.MONEY_ORDER);
        }
        //changes start for rev 1.0
        if (util.getParameterValue("LoyaltyPointsAccepted", "Y").equalsIgnoreCase("Y"))
        {
            tenderList.add(MAXTenderTypeEnum.LOYALTY_POINTS);
        }
        tenderList.add(MAXTenderTypeEnum.PAYTM);
        tenderList.add(MAXTenderTypeEnum.MOBIKWIK);
        //changes end for rev 1.0
        /////////////////
        // Traveler Check
        String[] travChecksAccepted = util.getParameterValueList("TravelersChecksAccepted");
        if (travChecksAccepted == null)
        {
            travChecksAccepted = new String[1];
            travChecksAccepted[0] = NONE;
        }
        else
        {
            travChecksAccepted = extractCheckCurrencyPart(travChecksAccepted);
        }
        // Add Traveler Check to the list if the base currency is listed in the check currencies
        if (util.isStringListed(DomainGateway.getBaseCurrencyInstance().getDescription(), travChecksAccepted))
        {
            tenderList.add(TenderTypeEnum.TRAVELERS_CHECK);
        }

        // Enable the button for the "Alternate" currency if Cash or Traveler's checks or Checks
        // are accepted in more than one currency and there are alternate currencies
        // available.
        // Note that "Alternate" is not the label for the button, just the action
        CurrencyTypeIfc[] altCurrencies = DomainGateway.getAlternateCurrencyTypes();
        String baseCurrency = DomainGateway.getBaseCurrencyInstance().getDescription();
        ArrayList parmAltCash = getAltCurrenciesAccepted(baseCurrency, cashAccepted);
        ArrayList parmAltTC = getAltCurrenciesAccepted(baseCurrency, travChecksAccepted);
        ArrayList parmAltCheck = getAltCurrenciesAccepted(baseCurrency, checksAccepted);

        if ((altCurrencies != null) && (altCurrencies.length > 0)) // are there alternate currencies to use
        {
            String firstAltCurr = altCurrencies[0].getCurrencyCode();

            // If the first alternate currency (from domain) appears in one of the
            // accepted tender parameters
            if (util.isStringListed(firstAltCurr, parmAltCash.toArray())
                || util.isStringListed(firstAltCurr, parmAltTC.toArray())
                || util.isStringListed(firstAltCurr, parmAltCheck.toArray()))
            {
                tenderList.add(TenderTypeEnum.ALTERNATE);
            }
        }

        // convert list to array
        TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
        tenderTypeArray = (TenderTypeEnum[]) tenderList.toArray(tenderTypeArray);
        return tenderTypeArray;
    }
}
