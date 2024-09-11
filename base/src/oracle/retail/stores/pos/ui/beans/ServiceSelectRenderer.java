/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ServiceSelectRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *    acadar    02/09/09 - use default locale for display of date and time
 *    ddbaker   11/20/08 - Updates for clipping problems
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/11/2007 4:25:39 PM   Mathews Kochummen use
 *          locale's date format
 *    3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:13 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:12:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Apr 07 2003 10:45:30   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.2   Sep 24 2002 14:10:20   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:26   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:52:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:53:42   msg
 * Initial revision.
 *
 *    Rev 1.0   16 Feb 2002 17:55:14   baa
 * Initial revision.
 * Resolution for POS SCR-1142: Service Alert screen does not have dark blue border at top.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.text.DateFormat;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
//-------------------------------------------------------------------------
/**
   This is the renderer for the Email list.
  $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class ServiceSelectRenderer extends AbstractListRenderer

{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       The property for the currency format.
    **/
    public static int TYPE       = 0;
    public static int DATE       = 1;
    public static int TIME       = 2;

    public static int BLANK      = 3;
    public static int SUMMARY    = 4;

    public static int MAX_FIELDS = 5;

    /** the default weights that layout the first display line */
    public static int[] DEFAULT_WEIGHTS = {22,39,39}; //{20,20,20,40};
    /** the default weights that layout the second display line */
    public static int[] DEFAULT_WEIGHTS2 = {22,78};

    /** the default widths that layout the first display line */
    public static int[] DEFAULT_WIDTHS = {1,1,1};
    /** the default widths that layout the second display line */
    public static int[] DEFAULT_WIDTHS2 = {1,2};

    public static String SERVICE_SELECT_SPEC = "ServiceAlertButtonSpec";
    public static String SERVICE_SELECT_UI_SPEC = "ServiceSelectSpec";
    public static final String DESCRIPTION_SUMMARY_LABEL = "DescriptionSummaryLabel";


    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public ServiceSelectRenderer()
    {
        super();
        setName("ServiceSelectRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;
        secondLineWeights = DEFAULT_WEIGHTS2;
        firstLineWidths = DEFAULT_WIDTHS;
        secondLineWidths = DEFAULT_WIDTHS2;

        setFirstLineWeights("serviceAlertListRendererWeights");
        setSecondLineWeights("serviceAlertListRendererWeights2");
        setFirstLineWidths("serviceAlertListRendererWidths");
        setSecondLineWidths("serviceAlertListRendererWidths2");

        fieldCount = MAX_FIELDS;
        lineBreak  = TIME;
        secondLineBreak = SUMMARY;

        initialize();
    }

     //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {
        labels[TYPE].setHorizontalAlignment(JLabel.LEFT);
        labels[DATE].setHorizontalAlignment(JLabel.LEFT);
        labels[TIME].setHorizontalAlignment(JLabel.LEFT);
        labels[SUMMARY].setHorizontalAlignment(JLabel.LEFT);

    }
    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        AlertEntryIfc alertEntry = (AlertEntryIfc)value;

        //      get Correct Locale based Tender Type descriptor
        UtilityManagerIfc utility =
                       (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String tempDesc = AlertEntryIfc.ALERT_TYPE_DESCRIPTORS[alertEntry.getAlertType()];

        labels[TYPE].setText(utility.retrieveText(SERVICE_SELECT_SPEC,
                                                  BundleConstantsIfc.SERVICE_ALERT_BUNDLE_NAME,
                                                  tempDesc,
                                                  tempDesc));

        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        String dateString = dateTimeService.formatDate(alertEntry.getTimeIssued().dateValue(),defaultLocale , DateFormat.SHORT);
        String timeString = dateTimeService.formatTime(alertEntry.getTimeIssued().dateValue(), defaultLocale, DateFormat.SHORT);
        labels[DATE].setText(dateString);
        labels[TIME].setText(timeString);

        labels[BLANK].setText("");
        String descriptionLabel = utility.retrieveText(SERVICE_SELECT_UI_SPEC,
                                                       BundleConstantsIfc.SERVICE_ALERT_BUNDLE_NAME,
                                                       DESCRIPTION_SUMMARY_LABEL,
                                                       DESCRIPTION_SUMMARY_LABEL);
        labels[SUMMARY].setText(descriptionLabel + " " + alertEntry.getSummary());
   }

     //---------------------------------------------------------------------
    /**
       Formats an EYSDate value for the JTable display as Time
       @param date an EYSDate value
       @return String to be used by the JTable
     */
    //---------------------------------------------------------------------
    protected  String getTimeString(EYSDate date)
    {
            return date.toFormattedTimeString(DateFormat.SHORT,getDefaultLocale());
     }
   //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void setPropertyFields()  { }
    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        this.props = props;
    }

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        String dummy = "dummy";
        AlertEntryIfc alertEntry = DomainGateway.getFactory().getAlertEntryInstance();
        alertEntry.setAlertType(0);
        alertEntry.setTimeIssued(new EYSDate(2001, 4, 16));
        alertEntry.setSummary("dummy");
        return(alertEntry);
    }


    //---------------------------------------------------------------------
    /**
       main entrypoint - starts the part when it is run as an application
       @param args String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        ServiceSelectRenderer bean = new ServiceSelectRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}
