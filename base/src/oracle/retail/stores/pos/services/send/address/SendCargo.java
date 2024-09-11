/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/address/SendCargo.java /main/20 2013/05/02 10:47:36 yiqzhao Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
 *                      the description to retail price
 *                      modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
 *    yiqzha 10/19/12 - Refactor to use DestinationTaxRule station to get line
 *                      item tax from send destination postal code.
 *    yiqzha 04/03/12 - refactor store send for cross channel
 *    cgreen 03/30/12 - get journalmanager from bus
 *    cgreen 01/27/11 - refactor creation of data transactions to use spring
 *                      context
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    nkgaut 04/02/09 - Fix for missing space in EJ for Tax Reason
 *    vcheng 12/17/08 - ej defect fixes
 *    ranojh 11/03/08 - Fixed Shipping Method reason codes and removal of
 *                      database and currency locales from
 *                      application.properties file
 *    akandr 10/30/08 - EJ changes
 *    acadar 10/28/08 - localization for item tax reason codes
 *    acadar 10/27/08 - use localized price override reason codes
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:13 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:09 PM  Robert Pearse
     $
     Revision 1.20  2004/09/01 14:18:56  rsachdeva
     @scr 6791 Transaction Level Send Javadoc

     Revision 1.19  2004/08/27 15:07:43  rsachdeva
     @scr 6791 Deprecate Customer Present

     Revision 1.18  2004/08/09 16:28:04  rsachdeva
     @scr 6719 Send Level In Progress

     Revision 1.17  2004/08/09 16:13:53  rsachdeva
     @scr 6791 Send Level In Progress

     Revision 1.16  2004/06/19 14:06:14  lzhao
     @scr 4670: integrate with capture customer

     Revision 1.15  2004/06/16 21:42:50  lzhao
     @scr 4670: refactoring send package.

     Revision 1.14  2004/06/11 19:10:35  lzhao
     @scr 4670: add customer present feature

     Revision 1.13  2004/06/09 19:45:14  lzhao
     @scr 4670: add customer present dialog and the flow.

     Revision 1.12  2004/06/07 18:28:37  jdeleau
     @scr 2775 Support multiple GeoCodes tax screen

     Revision 1.11  2004/06/04 20:23:44  lzhao
     @scr 4670: add Change send functionality.

     Revision 1.10  2004/06/03 22:49:27  jdeleau
     @scr 2775 For tax on send Item, prepare for the upcoming requirement
     to put a screen up for the user to select GeoCode.

     Revision 1.9  2004/06/03 14:48:15  epd
     @scr 5368 data transaction factory updates

     Revision 1.8  2004/06/02 19:06:51  lzhao
     @scr 4670: add ability to delete send items, modify shipping and display shipping method.

     Revision 1.7  2004/04/20 13:17:05  tmorris
     @scr 4332 -Sorted imports

     Revision 1.6  2004/04/12 18:58:36  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.5  2004/04/09 16:56:02  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.4  2004/03/15 21:43:30  baa
     @scr 0 continue moving out deprecated files

     Revision 1.3  2004/02/12 16:51:55  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:29  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:06:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 19 2003 10:48:46   baa
 * changes to shipping address screen get lost upon return to the screen when data validation failed.
 * Resolution for 3159: Send Transaction- Modifying Customer or Adding New Customer unable to select Canadian Province.
 *
 *    Rev 1.2   Jan 13 2003 14:15:20   RSachdeva
 * Replaced AbstractFinancialCargo.getCodeListMap()   by UtilityManagerIfc.getCodeListMap()
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.1   Dec 03 2002 16:19:56   sfl
 * Added one attribute to indicate the shipping address postal code is valid or not.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:04:06   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:08   msg
 * Initial revision.
 *
 *    Rev 1.3   10 Jan 2002 18:35:38   sfl
 * Fixed the null items problem for retrieved send transaction.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   08 Jan 2002 17:22:50   baa
 * add tax override to flow when items are sent out of state
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.1   13 Dec 2001 18:00:06   baa
 * updates to support offline
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   07 Dec 2001 11:22:14   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.send.address;

import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.destinationtaxrule.DestinationTaxRuleCargo;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * The cargo needed by the POS service.
 * 
 */
public class SendCargo extends SaleCargo implements CodeConstantsIfc
{
    private static final long serialVersionUID = 5022120585162727041L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(SendCargo.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * Partial Shipping charge obtained by adding items price or weight
     */
    protected CurrencyIfc partialShippingCharge;

    /**
     * Parameter used to determine how to calculate shipping charges
     */
    protected String calculationParameter;

    /**
     * vector of items from sale return transaction
     */
    protected Vector<AbstractTransactionLineItemIfc> items;

    /**
     * customer data to be used for shipping
     */
    protected CustomerIfc shipToInfo = null;

    /**
     * shipping method
     */
    protected ShippingMethodIfc shippingMethod = null;

    /**
     * Flag to tell the sevice is loaded from sale delete item or send change
     * item
     */
    protected boolean itemUpdate = false;

    /**
     * Array of GeoCodeVO objects
     */
    protected GeoCodeVO[] geoCodes;

    /**
     * billing customer info
     */
    protected CustomerIfc customer = null;

    /**
     * send level in progress.If false, it implies Item level send is in
     * progress, otherwise transaction level send is in progress.
     */
    protected boolean transactionLevelSendlInProgress = false;
    
    /**
     * destination tax rule is obtained by shipping destination postal code.
     */
    protected TaxRulesVO destinationTaxRule;
    
    protected CodeListIfc shippingChargeReasonCodes = null;
    /**
     * Retrieves shipping methods from flatfile.
     * 
     * @return shipping methods code list
     */
    public CodeListIfc getSendShippingMethods()
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        return (utility.getReasonCodes(storeID, CODE_LIST_SHIPPING_METHOD));
    }

    /**
     * Gets the partial shipping charge
     * 
     * @return the partial shipping charge
     */
    public CurrencyIfc getPartialShippingCharges()
    {
        return partialShippingCharge;
    }

    /**
     * Sets the partial shipping charge
     * 
     * @param value CurrencyIfc the partial shipping charge
     */
    public void setPartialShippingCharges(CurrencyIfc value)
    {
        partialShippingCharge = value;
    }

    /**
     * Gets the shippingCalculation parameter
     * 
     * @return the shippingCalculation parameter
     */
    public String getParameter()
    {
        return calculationParameter;
    }

    /**
     * Sets the shippingCalculation parameter
     * 
     * @param value String the shippingCalculation parameter
     */
    public void setParameter(String value)
    {
        calculationParameter = value;
    }

    /**
     * @deprecated This method is not used.
     * Updates item with tax changes.
     * 
     * @param newTax new item tax settings
     */
    public void updateItemTax(BusIfc bus, ItemTaxIfc newTax)
    {
        // update tax based on tax mode
        //Update tax info for every send item
        items = transaction.getLineItemsVector();
        for (int i = 0; i < items.size(); i++)
        {
            if (((SaleReturnLineItemIfc)items.get(i)).getItemSendFlag() &&
                !((SaleReturnLineItemIfc)items.get(i)).isReturnLineItem())
            {
                if ((((SaleReturnLineItemIfc)items.get(i)).getTaxMode() != TaxIfc.TAX_MODE_EXEMPT) &&
                    (((SaleReturnLineItemIfc)items.get(i)).getTaxMode() != TaxIfc.TAX_MODE_NON_TAXABLE))
                {
                   ItemPriceIfc ip = ((SaleReturnLineItemIfc)items.get(i)).getItemPrice();

                   ip.overrideTaxRate(newTax.getOverrideRate(), newTax.getReason());

                   // make journal entry
                   JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

                   double overrideRate = newTax.getOverrideRate();
                   String reasonString;
                   StringBuilder sb = new StringBuilder();

                   // tailor output to mode
                   reasonString = newTax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));

                   sb.append(Util.EOL);
                   Object dataObject[]={((SaleReturnLineItemIfc)items.get(i)).getItemID()};

                   String itemInfo = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LOWER, dataObject);
                   sb.append(itemInfo).append(Util.EOL);

                   Object taxOverrideDataObject[]={overrideRate * 100};
                   String taxOverridePer = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_OVERRIDE_PERCENTAGE, taxOverrideDataObject);
                   sb.append(taxOverridePer).append(Util.EOL);

                   Object dataArgs[] = new Object[]{reasonString};
                   sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_REASON_CODE, dataArgs));

                   journal.journal(getTransaction().getTransactionID(),
                   getOperator().getLoginID(),
                   sb.toString());
                }
            }
        }

    } // end updateItemTax()

    /**
     * Set the flag to indicate the sevice is loaded from sale delete/send
     * update item
     * 
     * @param value boolean from delete items
     */
    public void setItemUpdate(boolean value)
    {
        itemUpdate = value;
    }

    /**
     * Get the flag to indicate the sevice is loaded from sale delete/send
     * update item
     * 
     * @return boolean from delete items
     */
    public boolean isItemUpdate()
    {
        return itemUpdate;
    }

    /**
     * Get customer information to be use for shipping purposes
     * 
     * @return CustomerIfc the customer shipping information
     */
    public CustomerIfc getShipToInfo()
    {
        return shipToInfo;
    }

    /**
     * Get customer information to be use for shipping purposes
     * 
     * @param customerData the customer object containing the shipping
     *            information
     */
    public void setShipToInfo(CustomerIfc customerData)
    {
        shipToInfo = customerData;
    }

    /**
     * Set the Collection of geoCodes
     * 
     * @param geoCodes
     */
    public void setGeoCodes(GeoCodeVO[] geoCodes)
    {
        this.geoCodes = geoCodes;
    }

    /**
     * Get the collection of GeoCodes
     * 
     * @return
     */
    public GeoCodeVO[] getGeoCodes()
    {
        if (this.geoCodes == null)
        {
            this.geoCodes = new GeoCodeVO[0];
        }
        return this.geoCodes;
    }

    /**
     * Get shipping method to be use for shipping purposes
     * 
     * @return ShipppingMethodIfc the shipping method
     */
    public ShippingMethodIfc getShippingMethod()
    {
        return shippingMethod;
    }

    /**
     * Get customer information to be use for shipping purposes
     * 
     * @param method the shippingMethod object containing the shipping method
     */
    public void setShippingMethod(ShippingMethodIfc method)
    {
        shippingMethod = method;
    }

    /**
     * Get the customer as billing customer
     * 
     * @return CustomerIfc customer
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Set customer as billing customer
     * 
     * @param value CustomerIfc customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Checks if send at transaction level or item level is in progress
     * 
     * @return boolean true if transaction level send is in progress
     */
    public boolean isTransactionLevelSendInProgress()
    {
        return transactionLevelSendlInProgress;
    }

    /**
     * Sets true if transaction level send is in progress. The send at
     * transaction level is not assigned yet - it is in progress.
     * 
     * @param transactionLevelSendInProgress true if transaction level send in progress
     */
    public void setTransactionLevelSendInProgress(boolean transactionLevelSendInProgress)
    {
        this.transactionLevelSendlInProgress = transactionLevelSendInProgress;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
       String strResult = new String("Class:  SendCargo (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());

        strResult += "\ncalculationParameter                  = [" + calculationParameter + "]";

        if (partialShippingCharge == null)
            strResult += "\npartialShippingCharge           = [null]";
        else
            strResult += "\npartialShippingCharge          = [" + partialShippingCharge.toString() + "]";

        return(strResult);
    }

	/**
	 * get shipping destination tax rule value object
	 * @return
	 */
    public TaxRulesVO getDestinationTaxRule() 
    {
		return destinationTaxRule;
	}

    /**
     * set destination tax rule value object
     * @param destinationTaxRule
     */
	public void setDestinationTaxRule(TaxRulesVO destinationTaxRule) 
	{
		this.destinationTaxRule = destinationTaxRule;
	}
	
	   /**
     * Set reason codes for modifying shipping charge
     * @param codeList
     */
    public void setShippingChargeReasonCodes(CodeListIfc codeList)
    {
        this.shippingChargeReasonCodes = codeList;
    }
    
    /**
     * Get reason codes for modifying shipping charge
     * @return
     */
    public CodeListIfc getShippingChargeReasonCodes()
    {
        return this.shippingChargeReasonCodes;
    }
}