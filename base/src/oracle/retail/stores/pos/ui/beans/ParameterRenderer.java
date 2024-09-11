/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ParameterRenderer.java /main/16 2011/12/05 12:16:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale when displaying currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         7/11/2007 11:07:31 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    7    360Commerce 1.6         5/30/2007 9:01:58 AM   Anda D. Cadar   code
 *         cleanup
 *    6    360Commerce 1.5         5/22/2007 4:50:03 PM   Mathews Kochummen
 *         i18n change
 *    5    360Commerce 1.4         5/21/2007 9:16:22 AM   Anda D. Cadar   EJ
 *         changes
 *    4    360Commerce 1.3         5/3/2007 3:56:45 PM    Mathews Kochummen
 *         display dates in parameters in locale format
 *    3    360Commerce 1.2         3/31/2005 4:29:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:59 PM  Robert Pearse
 *
 *   Revision 1.5  2004/03/24 19:50:07  mweis
 *   @scr 0 JavaDoc cleanup
 *
 *   Revision 1.4  2004/03/19 21:02:56  mweis
 *   @scr 4113 Enable ISO_DATE datetype
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 01 2003 14:46:04   lzhao
 * use name content to replace name itself.
 *
 * Resolution for 3403: parameter list display ReasonCodeGroup parameters name rather than parameter name value
 *
 *    Rev 1.0   Aug 29 2003 16:11:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 23 2003 11:26:20   bwf
 * Made sure to not check bundle for certain instanceof.
 * Resolution for 2201: Parameters - List box displays <> around some choices.
 *
 *    Rev 1.4   Apr 10 2003 13:00:20   baa
 * update bundles
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.3   Sep 03 2002 16:07:52   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:18:20   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jun 21 2002 18:26:44   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:55:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:46   msg
 * Initial revision.
 *
 *    Rev 1.5   11 Feb 2002 17:00:32   baa
 * fix  value field box
 * Resolution for POS SCR-1219: LayawayLegalStmt parameter value field box too small to see data
 *
 *    Rev 1.4   30 Jan 2002 10:28:04   KAC
 * Now considers "list from list"
 * Resolution for POS SCR-672: Create List Parameter Editor
 *
 *    Rev 1.3   23 Jan 2002 11:27:34   KAC
 * Re-removed modifiability for new look and feel UI.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
//-------------------------------------------------------------------------
/**
    Contains the visual rendering for the Prescription Selection list
**/
//-------------------------------------------------------------------------

public class ParameterRenderer extends AbstractListRenderer
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    /** Index of the NAME JLabel. */
    public static int NAME       = 0;
    /** Index of the VALUE JLabel. */
    public static int VALUE      = 1;
    /** Maximum number of fields.  Currently set to 2. */
    public static int MAX_FIELDS = 2;

    /** first line label weights (set to defaults) */
    public static int[] PARAM_WEIGHTS = {50,50};

    protected JLabel parameterNameField = null;
    protected JLabel valueField = null;

    /**
     * Currency Service
     *
     */
    protected static CurrencyServiceIfc currencyService = null;


    /**
     * Gets the CurrencyService
     *
     */
    protected static CurrencyServiceIfc getCurrencyService()
    {
        if (currencyService == null)
        {
            currencyService = CurrencyServiceLocator.getCurrencyService();
        }
        return currencyService;
    }
    //---------------------------------------------------------------------
    /**
        Default Constructor
    **/
    //---------------------------------------------------------------------
    public ParameterRenderer()
    {
        super();
        setName("ParameterRenderer");

        // set default in case lookup fails
        firstLineWeights = PARAM_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("parameterRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = VALUE;


        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        labels[NAME].setHorizontalAlignment(JLabel.LEFT);
        labels[VALUE].setHorizontalAlignment(JLabel.LEFT);
    }


    //--------------------------------------------------------------------------
    /**
     *  Applies data to the visual components in the renderer.
     *  @param value The data object to render
     */
    public void setData(Object value)
    {
        if (value instanceof ListFromListParameterBeanModel)
        {
            String parameter = ((RetailParameter)value).getParameterName();
            labels[NAME].setText(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,parameter,parameter));
            labels[VALUE].setText(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                                  "ListOfValues","List of values"));
        }
        else if (value instanceof RetailParameter)
        {
            setRetailParameterFields((RetailParameter) value);
        }
        else if (value instanceof ReasonCodeGroupBeanModel)
        {
            setReasonCodeFields((ReasonCodeGroupBeanModel) value);
        }
    }

    //---------------------------------------------------------------------
    /**
        Sets the fields of this Renderer. <p>
        @param parameter    the RetailParameter data that is to be rendered
    **/
    //---------------------------------------------------------------------
    public void setRetailParameterFields(RetailParameter parameter)
    {
        String parameterName = parameter.getParameterName();
        String parameterValue = parameter.getValue();

        labels[NAME].setText(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                                                      parameterName, parameterName));

        // display using currency format
        if (parameter instanceof CurrencyParameterBeanModel)
        {
           parameterValue = getCurrencyService().formatCurrency(parameterValue, getDefaultLocale());

        }
        else if (parameter instanceof ISODateParameterBeanModel)
        {
        	DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        	parameterValue = dateTimeService.formatDate(((ISODateParameterBeanModel)parameter).getOldValue().dateValue(),getDefaultLocale(), DateFormat.SHORT);
        }
        else if(parameter instanceof WholeParameterBeanModel ||
                parameter instanceof StringParameterBeanModel ||
                parameter instanceof DecimalParameterBeanModel ||
                parameter instanceof MultilineStringParameterBeanModel)
        {
            // do nothing
            ;
        }
        else
        {
           parameterValue = UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                                                      parameterValue, parameterValue);
        }
        labels[VALUE].setText(parameterValue);
    }

    //---------------------------------------------------------------------
    /**
        Sets the fields of this Renderer. <p>
        @param group    the data that is to be rendered
    **/
    //---------------------------------------------------------------------
    public void setReasonCodeFields(ReasonCodeGroupBeanModel group)
    {
        String groupName = group.getGroupName();

        labels[NAME].setText(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                                                        groupName, groupName));
        labels[VALUE].setText(UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,
                                           "ListOfValues","List of Values"));
    }

    //--------------------------------------------------------------------------
    /**
     *  Creates a prototype data object used to size the renderer.
     *  @return a populated data object
     */
    public Object createPrototype()
    {
        return new Object();
    }

    //--------------------------------------------------------------------------
    /**
     *  Update the fields based on the properties.
     */
    protected void setPropertyFields()
    {

    }
}
