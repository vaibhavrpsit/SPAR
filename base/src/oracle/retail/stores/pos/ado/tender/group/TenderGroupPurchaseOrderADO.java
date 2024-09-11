/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupPurchaseOrderADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:43 mszekely Exp $
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
 *    5    360Commerce 1.4         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    4    360Commerce 1.3         4/25/2007 8:52:49 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.6   Feb 05 2004 13:21:04   rhafernik
 * log4j conversion
 * 
 *    Rev 1.5   Jan 26 2004 18:18:26   cdb
 * Altered to have more consistent exception when expected tender amount isn't valid.
 * Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 * Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 * 
 *    Rev 1.4   Jan 14 2004 08:58:56   cdb
 * Updated to allow invalid amount exception.
 * Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 * Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 * 
 *    Rev 1.3   Jan 13 2004 17:35:30   cdb
 * Corrected flow and updated behavior to match current requirements.
 * Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 * Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 * 
 *    Rev 1.2   Jan 06 2004 13:12:08   epd
 * refactored away references to TenderHelper and DomainGateway
 * 
 *    Rev 1.1   Nov 14 2003 11:09:58   epd
 * refactored some void functionality to be more general.
 * 
 *    Rev 1.0   Nov 04 2003 11:13:58   epd
 * Initial revision.
 * 
 *    Rev 1.3   Oct 27 2003 13:39:58   bwf
 * Remove unused import.
 * Resolution for 3418: Purchase Order Tender Refactor
 * 
 *    Rev 1.2   Oct 24 2003 16:40:04   bwf
 * Remove processVoid.
 * Resolution for 3418: Purchase Order Tender Refactor
 * 
 *    Rev 1.1   Oct 24 2003 14:51:38   bwf
 * Add PO functionality.
 * Resolution for 3418: Purchase Order Tender Refactor
 * 
 *    Rev 1.0   Oct 17 2003 12:34:34   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

//--------------------------------------------------------------------------
/**
    This is the group ado for purchase order.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderGroupPurchaseOrderADO extends AbstractTenderGroupADO
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //  --------------------------------------------------------------------------
    /**
        This method retrieves the tender type enumeration.
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
        @return TenderTypeEnum PURCHASE_ORDER
    **/
    //  --------------------------------------------------------------------------
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.PURCHASE_ORDER;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
     */
    public TenderTypeEnum getVoidType()
    {
        return getGroupType();
    }
    
    //  --------------------------------------------------------------------------
    /**
        This method validates the limits of the purchase order.
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
        @param tenderAttributes HashMap
        @param balanceDue CurrencyIfc
    **/
    //  --------------------------------------------------------------------------
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {        
        // Compare tender amount total of this group to limit value
        CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        
        if(tenderAmount.compareTo(balanceDue) > CurrencyIfc.EQUALS)
        {
            TenderException childException = new TenderException("Purchase Order amount is greater than Balance Due",
                                      TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED);
            throw new TenderException("Purchase Order amount does not equal Balance Due",
                                      TenderErrorCodeEnum.INVALID_AMOUNT,
                                      childException);
        }
        else if(tenderAmount.compareTo(balanceDue) != CurrencyIfc.EQUALS)
        {
            throw new TenderException("Purchase Order amount does not equal Balance Due",
                                      TenderErrorCodeEnum.INVALID_AMOUNT);
        }
    }
    
    //  --------------------------------------------------------------------------
    /**
        This method changes from new definition to old.
        @see oracle.retail.stores.ado.ADOIfc#toLegacy()
        @return EYSDomainIfc
    **/
    //  --------------------------------------------------------------------------
    public EYSDomainIfc toLegacy()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    //  --------------------------------------------------------------------------
    /**
        This method changes from new definition to old.
        @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.class)
        @param type Class
        @return EYSDomainIfc
    **/
    //  --------------------------------------------------------------------------
    public EYSDomainIfc toLegacy(Class type)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    //  --------------------------------------------------------------------------
    /**
        This method changes from old definition to new.
        @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
        @param EYSDomainIfc
    **/
    //  --------------------------------------------------------------------------
    public void fromLegacy(EYSDomainIfc rdo)
    {
        // TODO Auto-generated method stub
    }
}
