/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/SummaryReportSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    miparek   02/18/09 - Modified report type to retrieve from user-loacale
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:50:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Aug 14 2002 21:22:02   baa
 * retrieve report types from the site
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 07 2002 19:33:58   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   17 May 2002 15:22:42   baa
 * externalize store label
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   17 May 2002 15:09:26   baa
 * Initial revision.
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   Mar 18 2002 11:36:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:24:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;
// java imports
import java.util.ArrayList;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SummaryReportBeanModel;
//------------------------------------------------------------------------------
/**
   User has selected summary reports

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SummaryReportSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "SummaryReportSite";

    //--------------------------------------------------------------------------
    /**
       Set the report type and put up the Summary Entry Form.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        cargo.setReportType(RegisterReportsCargo.REPORT_SUMMARY);
        SummaryReportBeanModel model = new SummaryReportBeanModel();

        // populate report types model
        ArrayList  typeListModel = new ArrayList();
        typeListModel.add(utility.retrieveText("ReportSpec", BundleConstantsIfc.REPORTS_BUNDLE_NAME,
    			SummaryReportBeanModel.STORE_LABEL,SummaryReportBeanModel.STORE,LocaleConstantsIfc.USER_INTERFACE));

        typeListModel.add(utility.retrieveText("ReportSpec", BundleConstantsIfc.REPORTS_BUNDLE_NAME,
				SummaryReportBeanModel.REGISTER_LABEL,SummaryReportBeanModel.REGISTER,LocaleConstantsIfc.USER_INTERFACE));

        typeListModel.add(utility.retrieveText("ReportSpec", BundleConstantsIfc.REPORTS_BUNDLE_NAME,
				SummaryReportBeanModel.TILL_LABEL,SummaryReportBeanModel.TILL,LocaleConstantsIfc.USER_INTERFACE));

        model.setReportTypesModel(typeListModel);
        model.setSelectedType(utility.retrieveText("ReportSpec", BundleConstantsIfc.REPORTS_BUNDLE_NAME,
        		SummaryReportBeanModel.STORE_LABEL,SummaryReportBeanModel.STORE, LocaleConstantsIfc.USER_INTERFACE));
        model.setBusinessDate(cargo.getRegister().getBusinessDate());
        ui.showScreen(POSUIManagerIfc.SUMMARY_REPORT, model);

    }

}
