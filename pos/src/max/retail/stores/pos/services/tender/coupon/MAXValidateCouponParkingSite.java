package max.retail.stores.pos.services.tender.coupon;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.HashMap;

import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationBeanModel;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCouponADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateCouponParkingSite  extends PosSiteActionAdapter  {
	
	public void arrive(BusIfc bus) {

		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
//		TenderableTransactionIfc transaction = cargo.getTransaction();
//		TenderLineItemIfc[] tenderlineitems = transaction.getTenderLineItems();
//		for(TenderLineItemIfc tenderlineitem:tenderlineitems) {
//			tenderlineitem.
//		}
		TenderCouponADO couponTender = null;
		HashMap tenderAttributes = cargo.getTenderAttributes();
		tenderAttributes
				.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.COUPON);

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		//MAXCouponDenominationBeanModel model = (MAXCouponDenominationBeanModel) ui.getModel();
		DataInputBeanModel model1 =new DataInputBeanModel();
		BigDecimal amount = cargo.getParkingTotal().getDecimalValue();
		int couponType = cargo.getCouponType();
		boolean flag = false;
		
			tenderAttributes.put(TenderConstants.AMOUNT, cargo.getParkingTotal().getStringValue());
			tenderAttributes.put(TenderConstants.COUPON_NUMBER,
					cargo.getCouponName());

			
			BigDecimal totAmount = null;
			
			if (couponType == MAXDisplayCouponDenominationListSite.FOOD_COUPON_TYPE) {
				totAmount = (BigDecimal) tenderAttributes.get("foodTotals");
				if (totAmount.compareTo(amount) == 1
						|| totAmount.compareTo(amount) == 0) {
				
					flag=true;
				} else {
					flag = false;
					displayErrorDialogInvalid(ui, "FoodTotalErrorNotice");
				}
			}
				else
					flag= true;
			 
			
			
			
			if(flag)
			{
				try {
					// create a new coupon tender
					TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex
							.getFactory("factory.tender");
					couponTender = (TenderCouponADO) factory
							.createTender(tenderAttributes);
				} catch (ADOException adoe) {
					adoe.printStackTrace();
				} catch (TenderException e) {
					assert (false) : "This should never happen, because UI enforces proper format";
				}
				cargo.setTenderADO(couponTender);
				try {
					couponTender.calculateCouponAmount();
					if(couponType == MAXDisplayCouponDenominationListSite.FOOD_COUPON_TYPE)
					{	tenderAttributes.put("foodTotals", ((BigDecimal)tenderAttributes.get("foodTotals")).subtract(amount));
						cargo.setTenderAttributes(tenderAttributes);
					
					}
					
					bus.mail(new Letter(CommonLetterIfc.CONTINUE),
							BusIfc.CURRENT);
				} catch (TenderException e) {
					if (e.getErrorCode() == TenderErrorCodeEnum.MANUAL_INPUT) {
						displayErrorDialogInvalid(ui, "CouponDenominationNotProvidedError");
					} else {
						displayErrorDialog(ui, "CouponNumberNotValid");
					}
				}

			}
			
		
	}

	protected void displayErrorDialog(POSUIManagerIfc ui, String name) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setArgs(null);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	
	protected void displayErrorDialogInvalid(POSUIManagerIfc ui, String name) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setArgs(null);
		
		dialogModel.setType(DialogScreensIfc.YES_NO);
		dialogModel.setBannerColor(Color.RED);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Retry");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	public void depart(BusIfc bus) {
		LetterIfc letter = (LetterIfc) bus.getCurrentLetter();

		// If the user entered a coupon amount
		if (letter.getName().equals("Continue")) {
			TenderCargo cargo = (TenderCargo) bus.getCargo();
			// Get the manually entered coupon amount and set it in the coupon
			TenderCouponADO coupon = (TenderCouponADO) cargo.getTenderADO();
			HashMap tenderAttributes = coupon.getTenderAttributes();
			try {
				coupon.setTenderAttributes(tenderAttributes);
			} catch (TenderException e) {
				logger.error("GetCouponAmountSite.depart(): Invalid amount "
						+ Util.throwableToString(e) + "");
			}
		}
	}

}
