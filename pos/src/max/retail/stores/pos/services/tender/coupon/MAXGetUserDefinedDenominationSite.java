package max.retail.stores.pos.services.tender.coupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationBeanModel;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCountBeanModel;
import max.retail.stores.pos.ui.beans.MAXUserDefinedCouponBeanModel;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

public class MAXGetUserDefinedDenominationSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2003951124271101793L;

	public void arrive(BusIfc bus) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		MAXUserDefinedCouponBeanModel userDefinedModel = new MAXUserDefinedCouponBeanModel();
		userDefinedModel.setAmount(DomainGateway.getBaseCurrencyInstance());
		userDefinedModel.setQuantity(0);
		ui.showScreen(MAXPOSUIManagerIfc.USER_DEFINED_COUPON,userDefinedModel);
	}

	public void depart(BusIfc bus) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		MAXUserDefinedCouponBeanModel model = (MAXUserDefinedCouponBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.USER_DEFINED_COUPON);

		MAXCouponDenominationBeanModel couponDenoBeanModel = (MAXCouponDenominationBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION);
		ArrayList couponDenominationCountBeanModelList = couponDenoBeanModel
				.getCouponDenominationCountBeanModel();

		MAXCouponDenominationCountBeanModel couponDenoModel = null;

		ArrayList couponDenominationCountBeanModel = couponDenoBeanModel
				.getCouponDenominationCountBeanModel();
		Iterator iterator = couponDenominationCountBeanModel.iterator();
		while (iterator.hasNext()) {
			MAXCouponDenominationCountBeanModel couponDeno = (MAXCouponDenominationCountBeanModel) iterator
					.next();
			if (couponDeno.getDescription().equals(
					model.getAmount().getStringValue())) {
				/*
				 * int oldQuantity = couponDeno.getQuantity(); int newQuantity =
				 * oldQuantity+quantity;
				 */
				couponDeno.setQuantity(model.getQuantity());
				couponDeno.setAmount(DomainGateway
						.getBaseCurrencyInstance((model.getAmount()
								.getDecimalValue()).multiply(new BigDecimal(
								model.getQuantity()))));
				couponDenoModel = couponDeno;
				break;
			}
		}
		if (couponDenoModel == null) {
			if (!model.getAmount().getDecimalValue()
					.equals(DomainGateway.getBaseCurrencyInstance().getDecimalValue())) {
				if (model.getQuantity() > 0) {
					couponDenoModel = new MAXCouponDenominationCountBeanModel();

					couponDenoModel.setQuantity(model.getQuantity());
					couponDenoModel.setAmount(model.getAmount().multiply(
							new BigDecimal(model.getQuantity())));
					couponDenoModel.setDescription(model.getAmount() + "");
					couponDenoModel.setFieldDisabled(true);
					couponDenoModel.setFieldHidden(false);
					couponDenoModel.setLabel("UserDefined");
					couponDenoModel.setLabelTag(model.getAmount() + "");

					couponDenoModel.setTotalAmount(couponDenoModel.getAmount());

					couponDenoBeanModel.setTotal(couponDenoBeanModel.getTotal().add(couponDenoModel.getAmount()));
					NavigationButtonBeanModel globalButtonBeanModel = new NavigationButtonBeanModel();
					BigDecimal zeroDecimal = new BigDecimal("0.00");
					if(couponDenoModel.getAmount().getDecimalValue().compareTo(zeroDecimal)==0)
						globalButtonBeanModel.setButtonEnabled("Next",false);
					else
						globalButtonBeanModel.setButtonEnabled("Next",true);
					couponDenoBeanModel.setGlobalButtonBeanModel(globalButtonBeanModel);
					couponDenominationCountBeanModelList.add(
							couponDenominationCountBeanModelList.size(),
							couponDenoModel);
				}
			}
		}
		couponDenoBeanModel
				.setCouponDenominationCountBeanModel(couponDenominationCountBeanModelList);
		ui.setModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION, couponDenoBeanModel);

	}
}
