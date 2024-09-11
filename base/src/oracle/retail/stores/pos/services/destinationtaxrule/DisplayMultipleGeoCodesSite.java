/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/destinationtaxrule/DisplayMultipleGeoCodesSite.java /main/1 2012/10/22 15:36:15 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/19/12 - Display multiple Geo code which retrieved from
 *                         shipping/send destination postal code.
 *    acadar    06/01/10 - meerged to the tip
 *    acadar    05/28/10 - merged with tip
 *    acadar    05/26/10 - refactor shipping code
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.destinationtaxrule;



import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;


/**
 *
 * If multiple geo codes are found, allow the use to select one
 */
public class DisplayMultipleGeoCodesSite extends PosSiteActionAdapter
{


    /**
     *  Serial Version UID
     */
    private static final long serialVersionUID = 4365804740752397994L;

    /**
     * Arrive at the site, and display all the geoCodes, requesting that the user select one.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        DestinationTaxRuleCargo cargo = (DestinationTaxRuleCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ListBeanModel beanModel = new ListBeanModel();
        beanModel.setListModel(cargo.getGeoCodes());
        ui.setModel(POSUIManagerIfc.MULTIPLE_GEO_CODES, beanModel);
        ui.showScreen(POSUIManagerIfc.MULTIPLE_GEO_CODES, beanModel);
    }

    /**
     * Leaving the site.  Someone did something on the MultipleGeoCode UI that
     * is causing us to leave.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
    	DestinationTaxRuleCargo cargo = (DestinationTaxRuleCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        if(!bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO))
        {
            // Set the selected GeoCode for the GetTaxRulesSite to use to perform its calculations.
            ListBeanModel beanModel = (ListBeanModel)ui.getModel();
            GeoCodeVO selectedGeoCode = (GeoCodeVO) beanModel.getSelectedValue();

            cargo.setGeoCodes(new GeoCodeVO[] {selectedGeoCode});
        }
    }
}
