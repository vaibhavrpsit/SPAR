/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/BadParamValueSite.java /main/13 2012/08/07 16:20:03 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  07/06/12 - removed unused keys for InvalidNumericParam
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/11/2007 11:07:30 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    4    360Commerce 1.3         1/22/2006 11:45:05 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:33 PM  Robert Pearse
 *
 *   Revision 1.4  2004/05/21 13:34:57  dfierling
 *   @scr 4170 - fixed dialog text spacing issues
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:33  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:52:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 02 2002 09:35:52   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:38:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:04:00   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:19:02   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 10 2002 18:00:04   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   Feb 05 2002 16:42:12   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:12:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

// java imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.DecimalParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.RetailParameter;
import oracle.retail.stores.pos.ui.beans.WholeParameterBeanModel;
import java.math.BigDecimal;
import java.util.Locale;

//------------------------------------------------------------------------------
/**
    This site informs the user that the new value he attempted
    to give to the parameter is illegitimate.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class BadParamValueSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -6203564309378201293L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/13 $";
  

    //--------------------------------------------------------------------------
    /**
        Informs the user that the new value he attempted
        to give to the parameter is illegitimate. <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        RetailParameter param = cargo.getParameter();
        CurrencyServiceIfc currencyService = CurrencyServiceLocator.getCurrencyService();

        String minStr ="";
        String maxStr = "";
        
        Locale locale =  LocaleMap.getLocale(LocaleMap.DEFAULT);

        if (param instanceof CurrencyParameterBeanModel)
        {
            BigDecimal d=((CurrencyParameterBeanModel)param).getMinValue( ).setScale(2, BigDecimal.ROUND_HALF_UP);
            minStr=currencyService.formatCurrency(d, locale);
            d=((CurrencyParameterBeanModel)param).getMaxValue( ).setScale(2, BigDecimal.ROUND_HALF_UP);
            maxStr=currencyService.formatCurrency(d, locale);
        }
        else if (param instanceof DecimalParameterBeanModel)
        {
            minStr = "" + ((DecimalParameterBeanModel)param).getMinValue();
            maxStr = "" + ((DecimalParameterBeanModel)param).getMaxValue();
        }
        else if (param instanceof WholeParameterBeanModel)
        {
            minStr = "" + ((WholeParameterBeanModel)param).getMinValue();
            maxStr = "" + ((WholeParameterBeanModel)param).getMaxValue();
        }

        String[] args = { param.getParameterNameContent() + " ",
                          minStr,
                          maxStr};

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidNumericParam");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
