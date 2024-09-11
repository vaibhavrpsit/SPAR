/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrdersSummaryReportBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/26/2007 3:21:28 PM   Mathews Kochummen use
 *          locale appropriate date label
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse
 *
 *   Revision 1.5  2004/08/24 19:36:35  jdeleau
 *   @scr 6918 If the dates entered in order summary or summary report screens
 *   are not valid, make sure the invalid entries appear to the user after
 *   the error message has been read.
 *
 *   Revision 1.4  2004/08/24 15:02:17  jdeleau
 *   @scr 6910 Fix the displayable date from MM/dd/yyyy to MM/DD/YYYY
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:52:50   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:56   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:36   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:40   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 07 2002 20:44:40   mpm
 * Externalized text for report UI screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Feb 12 2002 10:46:04   dfh
 * cleanup, make end business date not editable
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
   Contains the visual presentation for Orders Summary Report Information
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class OrdersSummaryReportBean extends ValidatingBean
{
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int BUSN_DATE_START = 0;
    public static final int BUSN_DATE_END   = BUSN_DATE_START + 1;
    public static final int MAX_FIELDS      = BUSN_DATE_END + 1; //add one because of 0 index!

    public static String labelText[] =
    {
        "Starting Business Day ({0}):",
        "Ending Business Day ({0}):"
    };

    // Constant label strings
    protected static String labelTags[] =
    {
        "StartingBusinessDayLabel",
        "EndingBusinessDayLabel"
    };

    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];
    // model
    protected DateRangeReportBeanModel beanModel = new DateRangeReportBeanModel();
    // starting business date field
    protected EYSDateField startBusnDateField = null;
    // end business date field
    protected JLabel endBusnDateField   = null;

    // Don't update bean, error screen was just shown, we
    // want the screen to continue showing what the user entered in error.
    private boolean ignoreUpdate = false;
    //----------------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public OrdersSummaryReportBean()
    {
        super();
    }

    //----------------------------------------------------------------------------
    /**
     * activate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        startBusnDateField.setVisible(true);
        startBusnDateField.addFocusListener(this);
        endBusnDateField.addFocusListener(this);
    }

    //----------------------------------------------------------------------------
    /**
     * Deactivate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();

        startBusnDateField.removeFocusListener(this);
        endBusnDateField.removeFocusListener(this);
    }

    //----------------------------------------------------------------------------
    /**
     *  Configures the bean.
     */
    //----------------------------------------------------------------------------
    public void configure()
    {
        setName("OrdersSummaryReportBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        for(int cnt = 0; cnt < MAX_FIELDS; cnt++)
        {
            fieldLabels[cnt] =  uiFactory.createLabel(labelText[cnt] , null, UI_LABEL);
        }

        startBusnDateField = uiFactory.createEYSDateField("startBusnDateField");
        startBusnDateField.setColumns(10);

        endBusnDateField = uiFactory.createLabel("",null,UI_LABEL);



        UIUtilities.layoutDataPanel(this, fieldLabels,
            new JComponent[] {startBusnDateField, endBusnDateField});
    }

    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //------------------------------------------------------------------------
    /**
     * Gets the model for the current settings of this bean.
     * @return the model for the current values of this bean
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setStartBusinessDate(startBusnDateField.getDate());
    }

    //------------------------------------------------------------------------
    /**
     * Sets the model for the current settings of this bean.
     * @param model the model for the current values of this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set SummaryReportBeanModel" +
                                           " to null");
        }

        if (model instanceof DateRangeReportBeanModel)
        {
            beanModel = (DateRangeReportBeanModel) model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        endBusnDateField.setFocusable(false);

        if(this.ignoreUpdate == false)
        {
            if(beanModel.getStartBusinessDate() == null)
            {
                startBusnDateField.setDate(startBusnDateField.getEYSDate());
            }
            else
            {
                startBusnDateField.setDate(beanModel.getStartBusinessDate());
            }

            // end business date is a JLabel not an EYSDateField therfore use setText
            // method to initialize the field
            String dateFieldFormat =((DateDocument) startBusnDateField.getDocument()).getFormat();
            if(beanModel.getEndBusinessDate() == null)
            {
                 endBusnDateField.setText(startBusnDateField.getEYSDate().toFormattedString(dateFieldFormat,getDefaultLocale()));
            }
            else
            {
                endBusnDateField.setText(beanModel.getEndBusinessDate().toFormattedString(dateFieldFormat,getDefaultLocale()));
            }
        }

        setCurrentFocus(startBusnDateField);
        this.ignoreUpdate = false;
    }


    /**
     * Show the error screen
     *
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#showErrorScreen()
     */
    public void showErrorScreen()
    {
        super.showErrorScreen();
        this.ignoreUpdate = true;
    }
    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                labelText[i]));
        }
        //use locale appropriate date label
        String translatedLabel = getTranslatedDatePattern();
        fieldLabels[BUSN_DATE_START].setText(LocaleUtilities.formatComplexMessage(fieldLabels[BUSN_DATE_START].getText(), translatedLabel));
        fieldLabels[BUSN_DATE_END].setText(LocaleUtilities.formatComplexMessage(fieldLabels[BUSN_DATE_END].getText(), translatedLabel));
        //associate labels with fields
        startBusnDateField.setLabel(fieldLabels[BUSN_DATE_START]);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: OrdersSummaryReportBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //----------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    //----------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();


        DateRangeReportBeanModel
            beanModel = new DateRangeReportBeanModel();
            beanModel.setStartBusinessDate(new EYSDate());
            beanModel.setEndBusinessDate(new EYSDate());

        OrdersSummaryReportBean
            bean = new OrdersSummaryReportBean();
            bean.configure();
            bean.setModel(beanModel);
            bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
