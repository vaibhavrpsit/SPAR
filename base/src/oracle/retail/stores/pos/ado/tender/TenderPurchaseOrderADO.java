/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderPurchaseOrderADO.java /main/14 2013/09/05 10:36:15 abondala Exp $
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
 *    6    360Commerce 1.5         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    5    360Commerce 1.4         4/25/2007 8:52:53 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/05/26 23:09:03  crain
 *   @scr 5062 Purchase Order- Taxable status missing from journal when agency is other/business
 *
 *   Revision 1.4  2004/05/17 19:30:57  crain
 *   @scr 4198 Receipt prints incorrect PO Tender amount
 *
 *   Revision 1.3  2004/04/22 21:03:53  epd
 *   @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Feb 05 2004 13:46:42   rhafernik
 * log4j changes
 * 
 *    Rev 1.3   Jan 06 2004 11:23:08   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 * 
 *    Rev 1.2   Dec 19 2003 16:04:36   crain
 * Changed amount to face value amount
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.1   Dec 17 2003 11:34:12   crain
 * Added setFaceValueAmount
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.0   Nov 04 2003 11:13:18   epd
 * Initial revision.
 * 
 *    Rev 1.2   Oct 24 2003 14:49:56   bwf
 * Put in PO functionality.
 * Resolution for 3418: Purchase Order Tender Refactor
 * 
 *    Rev 1.1   Oct 21 2003 10:01:02   epd
 * Refactoring.  Moved RDO tender to abstract class
 * 
 *    Rev 1.0   Oct 17 2003 12:33:50   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
//--------------------------------------------------------------------------
/**
 This class is the purchase order ado that access the rdo.
 $Revision: /main/14 $
 **/
//--------------------------------------------------------------------------
public class TenderPurchaseOrderADO extends AbstractTenderADO
{
	/**
	 revision number
	 **/
	public static final String revisionNumber = "$Revision: /main/14 $";
	
	//------------------------------------------------------------------------
	/**
	 No arg constructor.  Meant to use tender factory to instantiate   
	 **/    
	//------------------------------------------------------------------------
	protected TenderPurchaseOrderADO() {}
	
	//------------------------------------------------------------------------
	/**
	 Initialize the RDO
	 @see oracle.retail.stores.pos.ado.tender.AbstractTenderADO#initializeTenderRDO()  
	 **/    
	//----------------------------------------------------------------------
	protected void initializeTenderRDO()
	{
		tenderRDO = DomainGateway.getFactory().getTenderPurchaseOrderInstance();
	}
	
	//------------------------------------------------------------------------
	/**
	 Return the tender type enumeration.
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
	 @return TenderTypeEnum 
	 **/    
	//----------------------------------------------------------------------
	public TenderTypeEnum getTenderType()
	{
		return TenderTypeEnum.PURCHASE_ORDER;
	}
	
	//------------------------------------------------------------------------
	/**
	 Validate.
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
	 **/    
	//----------------------------------------------------------------------
	public void validate() throws TenderException
	{
		// nothing needs to be validated here
	}
	
	//------------------------------------------------------------------------
	/**
	 Get the tender attributes.
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderAttributes()
	 @return map HashMap with attributes in it
	 **/    
	//----------------------------------------------------------------------
	public HashMap getTenderAttributes()
	{
		HashMap map = new HashMap(6);
		map.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.PURCHASE_ORDER);
		map.put(TenderConstants.AMOUNT, 
				getAmount().getStringValue());
		map.put(TenderConstants.NUMBER, 
				new String (((TenderPurchaseOrderIfc)tenderRDO).getPurchaseOrderNumber()));
		if (((TenderPurchaseOrderIfc)tenderRDO).getFaceValueAmount() != null)
		{  
			map.put(TenderConstants.FACE_VALUE_AMOUNT, 
					((TenderPurchaseOrderIfc)tenderRDO).getFaceValueAmount().getStringValue());
		}
		map.put(TenderConstants.AGENCY_NAME, 
				((TenderPurchaseOrderIfc)tenderRDO).getAgencyName());
		map.put(TenderConstants.TAXABLE_STATUS, 
				((TenderPurchaseOrderIfc)tenderRDO).getTaxableStatus());
		
		return map;
	}
	
	//------------------------------------------------------------------------
	/**
	 Get the tender attributes.
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
	 @param tenderAttributes HashMap with attributes in it
	 **/    
	//----------------------------------------------------------------------
	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
	{
		// get the amount
		CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
		((TenderPurchaseOrderIfc)tenderRDO).setAmountTender(amount);
		((TenderPurchaseOrderIfc)tenderRDO).
		setPurchaseOrderNumber((String)tenderAttributes.get(TenderConstants.NUMBER));
		if (tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT) != null)
		{  
			((TenderPurchaseOrderIfc)tenderRDO).setFaceValueAmount(parseAmount((String)tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT)));
		}
		if (tenderAttributes.get(TenderConstants.AGENCY_NAME) != null)
		{
			((TenderPurchaseOrderIfc)tenderRDO).setAgencyName((String)tenderAttributes.get(TenderConstants.AGENCY_NAME));        
		}
		if (tenderAttributes.get(TenderConstants.TAXABLE_STATUS) != null)
		{
			((TenderPurchaseOrderIfc)tenderRDO).setTaxableStatus((String)tenderAttributes.get(TenderConstants.TAXABLE_STATUS));        
		}
	}
	
	/**
	 * Indicates Purchase Order is a NOT type of PAT Cash
	 * @return false
	 */
	public boolean isPATCash()
	{
		return false;
	}
	
	//------------------------------------------------------------------------
	/**
	 Get the tender attributes.
	 @see oracle.retail.stores.pos.ado.journal.JournalableADOIfc#getJournalMemento()
	 @return memento Map of tender attributes
	 **/    
	//----------------------------------------------------------------------
	public Map getJournalMemento()
	{
		Map memento = getTenderAttributes();
		// add tender descriptor
		memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
		return memento;
	}
	
	//------------------------------------------------------------------------
	/**
	 Change from legacy system to new system.
	 @param rdo EYSDomainIfc
	 **/    
	//----------------------------------------------------------------------
	public void fromLegacy(EYSDomainIfc rdo)
	{
		tenderRDO = (TenderPurchaseOrderIfc)rdo;
	}
	
	//------------------------------------------------------------------------
	/**
	 Change to legacy system from new system.
	 @return EYSDomainIfc
	 **/    
	//----------------------------------------------------------------------
	public EYSDomainIfc toLegacy()
	{
		return tenderRDO;
	}
	
	//------------------------------------------------------------------------
	/**
	 Change to legacy system from new system.
	 @param type Class
	 @return EYSDomainIfc
	 **/    
	//----------------------------------------------------------------------
	public EYSDomainIfc toLegacy(Class type)
	{
		return toLegacy();
	}
}
