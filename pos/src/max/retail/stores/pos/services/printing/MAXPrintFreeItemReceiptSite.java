/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *  Rev 1.0     Mar 22, 2017	       Hitesh Dua		Initial revision for Print Free Item
 *
 ********************************************************************************/
package max.retail.stores.pos.services.printing;

import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

public class MAXPrintFreeItemReceiptSite extends PosSiteActionAdapter{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public static final String LANENAME = "MAXPrintFreeItemReceiptSite";

    /**
     * Print the receipt and send a letter
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        String letter = CommonLetterIfc.CONTINUE;

        //check for print free item receipt only when transaction have free item
        if((trans instanceof MAXSaleReturnTransaction && ((MAXSaleReturnTransaction)trans).getPrintFreeItem()!=null) 
        		|| trans instanceof MAXLayawayTransaction && ((MAXLayawayTransaction)trans).getPrintFreeItem()!=null){
        	
        	try
            {
             boolean isDuplicateReceipt = cargo.isDuplicateReceipt(); 
             ReceiptParameterBeanIfc receipt = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
             receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
             receipt.setTransaction(trans);
             receipt.setDocumentType(MAXReceiptTypeConstantsIfc.FREE_ITEM);
             receipt.setDuplicateReceipt(isDuplicateReceipt);

             PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
             pdm.printReceipt((SessionBusIfc)bus, receipt);  
             cargo.setReceiptPrinted(true);               
         }
            catch (PrintableDocumentException e)
            {
                logger.error("Unable to print free item receipt : "+MAXReceiptTypeConstantsIfc.FREE_ITEM, e);
                cargo.setPrinterError(e);
                letter = CommonLetterIfc.ERROR;
            }
        }
        else if(trans instanceof TenderGiftCertificateIfc){
        	
        }
         
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }


  }
