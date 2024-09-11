/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/PurchaseOrderCustomerLinkedSite.java /main/13 2012/08/07 16:20:10 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  07/17/12 - wptg - merged string for key InvalidType
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abhayg    05/07/10 - Fixed Transaction status issue
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         11/2/2006 10:43:33 AM  Keith L. Lesikar
 *         OracleCustomer parameter update.
 *    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:29 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.1  2004/04/02 22:13:51  epd
 *   @scr 4263 Updates to move Purchase Order tender to its own tour
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Dec 23 2003 17:01:30   crain
 * Fixed a flow defect
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.3   Dec 19 2003 17:13:20   crain
 * Fixed letter
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.2   Dec 17 2003 11:40:56   crain
 * Added 360Customer parameter
 * Resolution for 3421: Tender redesign
 * 
 *    Rev 1.1   Nov 14 2003 15:59:18   bwf
 * Fixed Layaway and special order crash.
 * Resolution for 3472: System crashes when PO is selected to make payment on new layaway
 * Resolution for 3474: System crashes when PO is selected to tender a new special order
 * Resolution for 3478: System crashes when PO is selected to tender an existing Layaway
 * 
 *    Rev 1.0   Nov 04 2003 11:17:50   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 24 2003 14:54:54   bwf
 * Initial revision.
 * Resolution for 3418: Purchase Order Tender Refactor
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

// foundation imports
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Determines if a business customer is linked to the
    transaction and display error message if not.<P>
    $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class PurchaseOrderCustomerLinkedSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/13 $";
        
    /** Resource ID **/
    public static final String resourceID = "InvalidType";
    
    //----------------------------------------------------------------------
    /**
        Determines if a business customer is linked to the
        transaction.<P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letterName = null;

        UtilityIfc utility;
        try
        {
            utility = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        if ("N".equals(utility.getParameterValue("OracleCustomer", "N")))
        {
            letterName = "Agency";
        }
        else
        {
            letterName = CommonLetterIfc.FAILURE;
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            RetailTransactionADOIfc trans = (RetailTransactionADOIfc) cargo.getCurrentTransactionADO();
        
            try
            {
                if(trans.isBusinessCustomerLinked())
                {
                    // make the transation taxable status
                    cargo.getTenderAttributes().put(TenderConstants.TRANSACTION_NON_TAXABLE, new Boolean(!trans.isTaxableTransaction()));  

                    letterName = CommonLetterIfc.CONTINUE;
                }
            }
            catch(TenderException te)
            {
                letterName = null;
                
                DialogBeanModel model = noCustomerLinked(bus);  
            
                // show screen
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);          
            }   
        }
        
        if (letterName != null)
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }
    
    //----------------------------------------------------------------------
    /**
        Creates a Dialog screen for no business customer linked.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public DialogBeanModel noCustomerLinked(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(resourceID);

        model.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.LINK);
        model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.FAILURE);
        model.setType(DialogScreensIfc.CONFIRMATION);

        return model;
    }
}
