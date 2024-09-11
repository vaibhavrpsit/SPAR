/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/TaxExemptErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/02 22:13:51  epd
 *   @scr 4263 Updates to move Purchase Order tender to its own tour
 *
 *   Revision 1.1  2004/03/02 19:47:48  crain
 *   *** empty log message ***
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.SaleReturnTransactionADO;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
 *  This aisle displays the tax exempt dialog screen
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxExemptErrorAisle extends PosLaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
     *  @param  bus Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = (DataInputBeanModel)ui.getModel();

        String agencyName = (String)model.getValueAsString("businessNameField");
        cargo.getTenderAttributes().put(TenderConstants.AGENCY_NAME, agencyName);  
        
        int taxable = model.getSelectionIndex("transactionTaxableField");
        cargo.getTenderAttributes().put(TenderConstants.TRANSACTION_NON_TAXABLE, new Boolean((taxable == 1) || (taxable == -1)));  

        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        String letterName = CommonLetterIfc.CONTINUE;
        
        if (!((Boolean)cargo.getTenderAttributes().get(TenderConstants.TRANSACTION_NON_TAXABLE)).booleanValue())
        {
            if (txnADO instanceof SaleReturnTransactionADO)
            {
                if (((SaleReturnTransactionADO)txnADO).getTransactionTaxStatus() == TaxIfc.TAX_MODE_EXEMPT)
                {
                    letterName = null;
                    DialogBeanModel dialogModel = new DialogBeanModel();
                    dialogModel.setResourceID("TaxExemptError");
                    dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                    dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }
            }
        }
        
        if (letterName != null)
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
     }
}
