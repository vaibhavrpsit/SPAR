/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/AmountResetBalanceActionSite.java /rgbustores_13.4x_generic_branch/1 2011/07/12 15:58:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vikini    04/02/09 - Franked Tender value should be actual tender value
 *                         not PO face value
 *
 * ===========================================================================
 * $Log:
 |    8    360Commerce 1.7         6/12/2008 4:32:39 PM   Charles D. Baker CR
 |         32040 - Updated to avoid clearing tax exempt status unless a) we're
 |          removing a tax exempt tender and b) there are not remaining
 |         tenders that are tax exempt purchase orders. Code review by Jack
 |         Swan.
 |    7    360Commerce 1.6         5/30/2007 9:01:57 AM   Anda D. Cadar   code
 |         cleanup
 |    6    360Commerce 1.5         5/18/2007 9:19:18 AM   Anda D. Cadar
 |         always use decimalValue toString
 |    5    360Commerce 1.4         5/8/2007 11:32:24 AM   Anda D. Cadar
 |         currency changes for I18N
 |    4    360Commerce 1.3         4/2/2006 11:19:23 PM   Dinesh Gautam   CR
 |         8184: Updated arrive(BusIfc bus) method, to call method
 |         resetPOTenderAmount(tenderAttributes) and passed Tender Attributes
 |         as argument.
 |         Added method resetPOTenderAmount(tenderAttributes) to update face
 |         value amount if tender is of type PO, tax exempted and face value
 |         amount  is greater than tender amount. 
 |    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:19:38 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse   
 |   $
 |   Revision 1.1  2004/04/02 22:13:51  epd
 |   @scr 4263 Updates to move Purchase Order tender to its own tour
 |
 |   Revision 1.3  2004/02/12 16:48:22  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:22:51  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 |   updating to pvcs 360store-current
 |
 |    Rev 1.0   Jan 13 2004 17:46:38   cdb
 | Initial revision.
 | Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 | Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 | 
 |    Rev 1.0   Nov 07 2003 16:11:44   bwf
 | Initial revision.
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class AmountResetBalanceActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5026517750002050592L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The arrive method checks the limits.
     * 
     * @param bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.AMOUNT, cargo.getCurrentTransactionADO().getBalanceDue().getDecimalValue().toString());
        
        resetPOTenderAmount(tenderAttributes);
        
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);        
    }

    private void resetPOTenderAmount(HashMap<String,Object> tenderAttributes)
    {
    	TenderTypeEnum tenderType =(TenderTypeEnum) tenderAttributes.get(TenderConstants.TENDER_TYPE);
    	String taxableStatus = (String)tenderAttributes.get(TenderConstants.TAXABLE_STATUS);
    	
    	if(TenderTypeEnum.PURCHASE_ORDER.equals(tenderType) && (TenderConstants.TAX_EXEMPT.equalsIgnoreCase(taxableStatus)) ){
    		double faceAmount = new Double(((String)tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT))).doubleValue();
    		double amount = new Double(((String)tenderAttributes.get(TenderConstants.AMOUNT))).doubleValue();
    		/*if(faceAmount > amount){
    			tenderAttributes.put(TenderConstants.FACE_VALUE_AMOUNT, tenderAttributes.get(TenderConstants.AMOUNT));
    		}*/
    		
    		// Commenting the if block
    		// FaceValueAmount value as this is the value used in franking. 
    		// Amount tender attribute is used in receipt printing
    	}
    }
    
}
