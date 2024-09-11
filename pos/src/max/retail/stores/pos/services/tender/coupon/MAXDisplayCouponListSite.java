
package max.retail.stores.pos.services.tender.coupon;

// java imports
import java.io.Serializable;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCouponBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


public class MAXDisplayCouponListSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 52926323170230306L;
	/**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 9$";
    /**
        offline shipping method prompt tag
    **/
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT_TAG = "AlternateShippingMethodPromptTag";
    /**
        offline shipping method prompt
    **/
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT =
      "Enter the shipping method and shipping charge and press Done.";

    //--------------------------------------------------------------------------
    /**
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
          ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
          POSUIManagerIfc     ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
          try {
			Serializable[] foodCouponTypeList = pm.getParameterValues("FoodCouponTypeList");
			Serializable[] nonFoodCouponTypeList = pm.getParameterValues("NonFoodCouponTypeList");
			String couponList[] = new String[foodCouponTypeList.length + nonFoodCouponTypeList.length];
			int index=0;
			for (int i = 0; i< foodCouponTypeList.length;i++) {
				Serializable param = foodCouponTypeList[i];
			
				if(param instanceof String)
					couponList[index++]=(String)param;
			}
			for (int i = 0; i< nonFoodCouponTypeList.length;i++) {
				Serializable param = nonFoodCouponTypeList[i];
				if(param instanceof String)
					couponList[index++]=(String)param;
			}
			MAXCouponBeanModel model = null;
	          if ( ui.getModel() instanceof MAXCouponBeanModel )
	          {
	              model = (MAXCouponBeanModel) ui.getModel(MAXPOSUIManagerIfc.COUPON_LIST);
	          }
	          else
	          {
	              model = new MAXCouponBeanModel();
	          }
	          model.setCouponList(couponList);
	         
	          ui.setModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION, null);
	          ui.showScreen("COUPON_LIST", model);
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
