/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/GetReasonCodeSite.java /main/15 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   11/04/08 - I18N - Fixed the way the locale was being retrieved
 *    acadar    10/24/08 - localization of post void reason codes
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode

       $Log:
      4    .v8x      1.2.1.0     3/11/2007 1:53:15 PM   Brett J. Larsen CR 4530
            - default reason code not being displayed (when default isn't the
           1st in the list)

           saving default value to the bean model for bean to later display
      3    360Commerce1.2         3/31/2005 4:28:15 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:50 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:11 PM  Robert Pearse
     $
     Revision 1.5  2004/03/03 23:15:10  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.4  2004/02/18 18:59:04  tfritz
     @scr 3818 - Made code review changes.

     Revision 1.3  2004/02/12 16:51:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Feb 10 2004 14:39:56   Tim Fritz
 * Added the new CaptureReasonCodeForNoSale parameter for the new No Sale requirments.
 *
 *    Rev 1.0   Aug 29 2003 16:03:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 14 2003 14:43:52   HDyer
 * Use ReasonBeanModel instead of deprecated NoSaleReasonBeanModel.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Jan 14 2003 09:05:28   RSachdeva
 * Replaced AbstractFinancialCargo.getCodeListMap()   by UtilityManagerIfc.getCodeListMap()
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.0   Apr 29 2002 15:13:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:10   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.nosale;

import java.util.Locale;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

/**
 * This site displays the SELECT_NO_SALE_REASON_CODE form.
 * 
 * @version $Revision: /main/15 $
 */
public class GetReasonCodeSite extends PosSiteActionAdapter
{

    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -6213972420262161838L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Displays the SELECT_NO_SALE_REASON_CODE form.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        boolean getReasonCode = false;

        try
        {
            getReasonCode = pm.getBooleanValue(ParameterConstantsIfc.TRANSACTION_CaptureReasonCodeForNoSale);
        }
        catch (ParameterException e)
        {
            logger.error(e);
        }

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        if (getReasonCode == true)
        {
            NoSaleCargo noSaleCargo = (NoSaleCargo) bus.getCargo();
            CodeListIfc reasonCodes = utility.getReasonCodes(noSaleCargo.getOperator().getStoreID(),
                    CodeConstantsIfc.CODE_LIST_NO_SALE_REASON_CODES);
            noSaleCargo.setLocalizedReasonCodes(reasonCodes);
            ReasonBeanModel model = new ReasonBeanModel();
            Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

            model.inject(reasonCodes, null, LocaleMap.getBestMatch(lcl));

            POSUIManagerIfc ui;
            ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.SELECT_NO_SALE_REASON_CODE, model);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
