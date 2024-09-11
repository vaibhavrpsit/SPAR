/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/CheckItemTypeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 12 2003 10:17:10   DCobb
 * Alterations code review cleanup.
 * Resolution for POS SCR-2007: Add Alterations to Item Inquiry service flow.
 * 
 *    Rev 1.1   Oct 14 2002 16:10:06   DCobb
 * Added alterations service to item inquiry service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * 
 *    Rev 1.0   Apr 29 2002 15:22:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Feb 2002 16:36:52   cir
 * Added check for open amount gift card
 * Resolution for POS SCR-1203: Gift Card sale - add open amount gift card to transaction from item inquiry, skips steps to enter amount
 * 
 *    Rev 1.0   Sep 21 2001 11:29:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
//--------------------------------------------------------------------------
/**
    This site adds an item to the transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckItemTypeSite extends PosSiteActionAdapter implements ProductGroupConstantsIfc
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        alterations letter name constant
    **/
    public static final String ALTERATIONS = "Alterations";

    //----------------------------------------------------------------------
    /**
        Adds the item to the transaction.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //Get the product group from item cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        String productGroup = (String) ((PLUItemIfc) cargo.getPLUItem()).getProductGroupID();
        boolean isPriceEntryRequired = ((PLUItemIfc) cargo.getPLUItem()).getItemClassification().isPriceEntryRequired();

        String  letter = null;
        if (productGroup != null && productGroup.equals(PRODUCT_GROUP_GIFT_CARD)
            && !isPriceEntryRequired)
        {
            letter = CommonLetterIfc.GIFTCARD;
        }
        else if (productGroup != null && productGroup.equals(PRODUCT_GROUP_ALTERATION))
        {
            letter = ALTERATIONS;
        }
        else
        {
            letter = CommonLetterIfc.ADD;
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
