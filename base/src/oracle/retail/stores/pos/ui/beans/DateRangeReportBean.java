/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DateRangeReportBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/26/2007 3:25:10 PM   Mathews Kochummen use
 *          locale appropriate date label
 *    3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse
 *
 *   Revision 1.7  2004/08/24 20:46:40  jdeleau
 *   @scr 6928 Make Starting and Ending Business Dates on Date Range Reports required.
 *
 *   Revision 1.6  2004/08/24 15:02:17  jdeleau
 *   @scr 6910 Fix the displayable date from MM/dd/yyyy to MM/DD/YYYY
 *
 *   Revision 1.5  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 10 2003 15:35:36   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   26 Jul 2003 06:03:08   baa
 * fix date save to model
 *
 *    Rev 1.2   25 Jul 2003 23:19:10   baa
 * default to current date if non selected.
 *
 *    Rev 1.1   Aug 07 2002 19:34:16   baa
 *  * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:30   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:48   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:06   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 07 2002 20:44:40   mpm
 * Externalized text for report UI screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



// swing imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
   Contains the visual presentation for Date Range Report Information
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class DateRangeReportBean extends ValidatingBean
{
    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // Indices for field locations
    protected static final int BUSN_DATE_START = 0;
    protected static final int BUSN_DATE_END   = BUSN_DATE_START + 1;
    protected static final int MAX_FIELDS      = BUSN_DATE_END + 1; //add one because of 0 index!

    // Constant label strings
    protected static String labelText[] =
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
    // Array of field labels
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];

    // Start Business Date
    protected EYSDateField startBusnDateField = null;

    // End Business Date
    protected EYSDateField endBusnDateField   = null;

    // Bean Model
    protected DateRangeReportBeanModel beanModel = null;

    // Dirty flag model
    // @deprecated as of release 5.5 no longer used.
    protected boolean dirtyModel = true;

    // Ignore updates to bean immediately after an error occurs
    private boolean ignoreUpdate;
    //----------------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public DateRangeReportBean()
    {
        super();
        initialize();
    }

    //----------------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //----------------------------------------------------------------------------
    protected void initialize()
    {
        setName("DateRangeReportBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();

    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the components.
     */
    protected void initComponents()
    {

        // create the labels
        for(int cnt = 0; cnt < MAX_FIELDS; cnt++)
        {
            fieldLabels[cnt] =
                uiFactory.createLabel(labelText[cnt], null, UI_LABEL);

        }
        startBusnDateField = new EYSDateField();
        startBusnDateField.setName("startBusnDateField");
        startBusnDateField.setColumns(10);

        endBusnDateField = new EYSDateField();
        endBusnDateField.setName("endBusnDateField");
        endBusnDateField.setColumns(10);
    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JComponent[] components = {startBusnDateField, endBusnDateField};

        UIUtilities.layoutDataPanel(this, fieldLabels, components);
    }

    //--------------------------------------------------------------------------
    /**
     *    Called when this bean is shown or hidden.
     *    @param aValue true if visible, false if not
     */
    public void setVisible(boolean aValue)
    {
        super.setVisible(aValue);

        if(aValue && !errorFound())
        {
            setCurrentFocus(startBusnDateField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Activates this bean.
     */
    public void activate()
    {
        super.activate();
        startBusnDateField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *    Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        startBusnDateField.removeFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *    Updates the model to reflect what is on the screen.
     */
    public void updateModel()
    {
        if (startBusnDateField.getDate() == null)
         {
             beanModel.setStartBusinessDate(startBusnDateField.getEYSDate());
         }
         else
         {
             beanModel.setStartBusinessDate(startBusnDateField.getDate());
         }

         if(endBusnDateField.getDate()== null)
         {
             beanModel.setEndBusinessDate(endBusnDateField.getEYSDate());
         }
         else
         {
             beanModel.setEndBusinessDate(endBusnDateField.getDate());
         }
    }

    //--------------------------------------------------------------------------
    /**
     *    Sets the model for the current settings of this bean.
     *    @param model the model for the current values of this bean
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SummaryReportBeanModel to null");
        }
        else
        {
            if (model instanceof DateRangeReportBeanModel)
            {
                beanModel = (DateRangeReportBeanModel) model;
                updateBean();
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Update the bean if the model has changed
     */
    protected void updateBean()
    {
        if(ignoreUpdate == false)
        {
            if (beanModel.getStartBusinessDate() == null)
            {
                startBusnDateField.setDate(startBusnDateField.getEYSDate());
            }
            else
            {
                startBusnDateField.setDate(beanModel.getStartBusinessDate());
            }

            if(beanModel.getEndBusinessDate() == null)
            {
                endBusnDateField.setDate(endBusnDateField.getEYSDate());
            }
            else
            {
                endBusnDateField.setDate(beanModel.getEndBusinessDate());
            }
        }
        ignoreUpdate = false;
    }

    /**
     * Show the error screen, that appears on validation error.
     *
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#showErrorScreen()
     */
    public void showErrorScreen()
    {
        super.showErrorScreen();
        ignoreUpdate = true;
    }

    //--------------------------------------------------------------------------
    /**
     *    Return the POSBaseBeanModel.
     *    @return posBaseBeanModel as POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()

        String dateLabel = "";
        String translatedLabel = getTranslatedDatePattern();
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            dateLabel = retrieveText(labelTags[i], labelText[i]);

            fieldLabels[i].setText(LocaleUtilities.formatComplexMessage(dateLabel, translatedLabel));
        }

        //associate labels with fields
        startBusnDateField.setLabel(fieldLabels[BUSN_DATE_START]);
        endBusnDateField.setLabel(fieldLabels[BUSN_DATE_END]);
    }                                   // end updatePropertyFields()

    //--------------------------------------------------------------------------
    /**
     *    Returns default display string.
     *    @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());
    }

    //--------------------------------------------------------------------------
    /**
     *    Retrieves the Team Connection revision number.
     *    @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
     *    Main entry point for testing.
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        DateRangeReportBean bean = new DateRangeReportBean();

        DateRangeReportBeanModel beanModel = new DateRangeReportBeanModel();

        beanModel.setStartBusinessDate
          (DomainGateway.getFactory().getEYSDateInstance());
        beanModel.setEndBusinessDate
          (DomainGateway.getFactory().getEYSDateInstance());

        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
