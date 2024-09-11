/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderMoneyOrderADO.java /main/14 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         4/25/2007 8:52:54 AM   Anda D. Cadar   I18N
 *        merge
 *        
 *   4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *        Base-lining of 7.1_LA
 *   3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:02 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
 *  $
 *  Revision 1.3  2004/04/22 21:03:53  epd
 *  @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *  Revision 1.2  2004/02/12 16:47:55  mcs
 *  Forcing head revision
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Feb 05 2004 13:46:40   rhafernik
 * log4j changes
 * 
 *    Rev 1.3   Jan 06 2004 11:23:06   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 * 
 *    Rev 1.2   Dec 08 2003 09:16:46   blj
 * code review findings.
 * 
 *    Rev 1.1   Nov 07 2003 14:42:44   blj
 * cleaned up code and added javadoc.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 *
 * TenderMoneyOrderADO contains the business logic for the money order 
 * tender.  
 */
public class TenderMoneyOrderADO extends AbstractTenderADO
{
	/**
	 * no-arg constructor 
	 * it is intended that the tender factory instantiate this
	 */
	protected TenderMoneyOrderADO() {}
	
	/* Initialize the RDO money order object
	 * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
	 */
	protected void initializeTenderRDO()
	{
		tenderRDO = DomainGateway.getFactory().getTenderMoneyOrderInstance();
	}
	/**
	 * @return TenderTypeEnum for MONEY_ORDER tender type
	 * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
	 */
	public TenderTypeEnum getTenderType()
	{
		return TenderTypeEnum.MONEY_ORDER;
	}
	
	/**
	 * There are no special validations to perform on Money Order.
	 * Limits are checked the Money Order tender group.
	 * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
	 * @throws TenderException
	 */
	public void validate() throws TenderException
	{ /* 
	* nothing to do for money order. 
	**/
	}
	
	/**
	 * set the RDO for money order
	 * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
	 */
	public void fromLegacy(EYSDomainIfc rdo)
	{
		//assert(rdo instanceof TenderCashIfc);
		
		tenderRDO = (TenderMoneyOrderIfc)rdo;
	}
	
	/**
	 * @return tenderRDO
	 * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
	 */
	public EYSDomainIfc toLegacy()
	{
		// update with current amount
		return tenderRDO;
	}
	
	/* Put tender attributes in the HashMap.  
	 * @return map
	 * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
	 */
	public HashMap getTenderAttributes()
	{
		HashMap map = new HashMap(2);
		map.put(TenderConstants.TENDER_TYPE, getTenderType());
		map.put(TenderConstants.AMOUNT, 
				getAmount().getStringValue());
		return map;
	}
	
	/* Set the amount in the RDO money order
	 * @see oracle.retail.stores.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
	 */
	public void setTenderAttributes(HashMap tenderAttributes)
	throws TenderException
	{
		// get the amount
		CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
		tenderRDO.setAmountTender(amount);
	}
	
	/**
	 * Indicates Money Order is a type of PAT Cash
	 * @return true for money order is less than or equal to $10k
	 */
	public boolean isPATCash()
	{
		boolean isPATCash = false;
		if (getAmount().signum() == CurrencyIfc.POSITIVE)
		{
			int result = DomainGateway.getBaseCurrencyInstance(TenderADOIfc.PAT_CASH_THRESHOLD)
			.compareTo(getAmount());
			isPATCash = (result == CurrencyIfc.GREATER_THAN || result == CurrencyIfc.EQUALS);
		}
		return isPATCash;
	}
	
	/* Journal tender attributes and descriptor
	 * @return memento
	 * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
	 */
	public Map getJournalMemento()
	{
		// We can reuse the tender attributes for journalling purposes
		Map memento = getTenderAttributes();
		// add tender descriptor
		memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
		
		return memento;
	}
	
	/**
	 * This is a no-op class for money order.  
	 * @return toLegacy();
	 * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
	 */
	public EYSDomainIfc toLegacy(Class type)
	{
		return toLegacy();
	}
}
