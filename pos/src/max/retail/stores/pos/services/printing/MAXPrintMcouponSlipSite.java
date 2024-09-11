package max.retail.stores.pos.services.printing;

import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

public class MAXPrintMcouponSlipSite  extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 **/
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = 1L;

	// --------------------------------------------------------------------------

	public static final String SITENAME = "MAXPrintMcouponSlipSite";

	/**
	 * Print Mcoupon if configured.
	 * <p>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		boolean sendMail = true;

		
		boolean isDuplicateReceipt = cargo.isDuplicateReceipt(); 
		
		if(cargo.getTransaction()!=null && cargo.getTransaction() instanceof MAXSaleReturnTransaction){
			MAXSaleReturnTransaction trans=(MAXSaleReturnTransaction)cargo.getTransaction();
			if(trans.getMcouponList()!=null && trans.getMcouponList().size()>0){
				
				
				for(MAXMcouponIfc mcoupon:trans.getMcouponList()){
					 try {
			            	PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc) bus
			        				.getManager(PrintableDocumentManagerIfc.TYPE);
			        		MAXReceiptParameterBeanIfc receipt = (MAXReceiptParameterBeanIfc) pdm.getReceiptParameterBeanInstance((SessionBusIfc) bus, trans);
			        		

			                receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
			                receipt.setTransaction(trans);
			                receipt.setMcoupon(mcoupon);
			                receipt.setDocumentType(MAXReceiptTypeConstantsIfc.MCOUPON_RECEIPT);
			                receipt.setDuplicateReceipt(isDuplicateReceipt);
			               
			                pdm.printReceipt((SessionBusIfc)bus, receipt);
			               
								
							}
			                catch (ParameterException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}catch (PrintableDocumentException e) {
								e.printStackTrace();
							}  
			               
			                cargo.setReceiptPrinted(true); 
				}
			}
			
			
		}
		
		 
		if (sendMail) {
			bus.mail(new Letter("PrintBakery"), BusIfc.CURRENT);
		}
	}
}
