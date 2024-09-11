/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DateSearchBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    mahising  02/25/09 - Fixed date issue for customer search
 *    mahising  02/22/09 - Fixed issue for narrow search screen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:10:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Apr 18 2003 15:05:16   RSachdeva
 * Default Dates should be set
 * Resolution for POS SCR-2133: Purchase Date screen, default "Purchase Date To" is not current business date
 * Resolution for POS SCR-2144: At Purchase Date screen, default Purchase Date From need be none
 *
 *    Rev 1.1   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:34   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:48   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:08   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 06 2002 19:30:06   mpm
 * Added text externalization for returns screens.
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

//------------------------------------------------------------------------------
/**
 *    This class is used to display and gather date range
 *    information for inquiries.
 *    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class DateSearchBean extends ValidatingBean
{

    /** revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // Define constants for grid bag layout field positioning on the screen.
    // Max fields is a count of the total number of rows.
    protected static final int START_DATE_ROW = 0;
    protected static final int END_DATE_ROW   = START_DATE_ROW + 1;
    protected static final int MAX_FIELDS     = END_DATE_ROW + 1; //add one because of 0 index!

    protected static String labelText[] =
    {
        "Start Date ({0}):",
        "End Date ({0}):",
    };

    protected static String labelTags[] =
    {
        "StartDateLabel", "EndDateLabel"
    };

    protected JLabel[] fieldLabels = null;

    protected DateSearchBeanModel beanModel = null;

    protected EYSDateField startDateField = null;
    protected EYSDateField endDateField   = null;

    // @deprecated as of release 5.5 obsolete
    protected boolean dirtyModel = true;

    //--------------------------------------------------------------------------
    /**
     *    Default Constructor.
     */
    public DateSearchBean()
    {
        super();
        initialize();
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

    //--------------------------------------------------------------------------
    /**
     *    Initialize the class.
     */
    protected void initialize()
    {
        setName("DateSearchBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     * Initializes the components.
     */
    protected void initComponents()
    {
        fieldLabels = new JLabel[MAX_FIELDS];

        // create the labels
        for(int cnt = 0; cnt < MAX_FIELDS; cnt++)
        {
            fieldLabels[cnt] =
                uiFactory.createLabel(labelText[cnt] , null, UI_LABEL);

        }
        startDateField = new EYSDateField();
        startDateField.setName("startDateField");
        startDateField.setColumns(10);

        endDateField = new EYSDateField();
        endDateField.setName("endDateField");
        endDateField.setColumns(10);
    }

    //--------------------------------------------------------------------------
    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JComponent[] components = {startDateField, endDateField};

        UIUtilities.layoutDataPanel(this, fieldLabels, components);
    }

    //--------------------------------------------------------------------------
    /**
     *    Updates the model for the current settings of this bean.
     */
    public void updateModel()
    {
        beanModel.setStartDate(startDateField.getEYSDate());
        beanModel.setEndDate(endDateField.getEYSDate());
    }
    //------------------------------------------------------------------------
    /**
     * Sets the model to be used with the DateSearchBean.
     * @param model the model for this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set DateSearchBeanModel" +
                                           " to null");
        }
        else
        {
            if (model instanceof DateSearchBeanModel)
            {
                beanModel = (DateSearchBeanModel) model;
                updateBean();
            }
        }
    }
    //---------------------------------------------------------------------
    /**
     * Update the bean from the model
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(beanModel.getStartDate() == null && beanModel.getclearUIFields())
        {
            startDateField.setDate(startDateField.getEYSDate());
        }
        else
        {
            if (beanModel.getclearUIFields())
            {
                startDateField.setText("");
                startDateField.setDate(startDateField.getEYSDate());

            }
            else
            {
                startDateField.setDate(beanModel.getStartDate());
            }
        }

        if(beanModel.getEndDate() == null && beanModel.getclearUIFields())
        {
            endDateField.setDate(endDateField.getEYSDate());
        }
        else
        {
            if (beanModel.getclearUIFields())
            {
                endDateField.setText("");
                endDateField.setDate(endDateField.getEYSDate());

            }
            else
            {
                endDateField.setDate(beanModel.getEndDate());
            }
        }

        beanModel.setclearUIFields(false); // reset model to leave dates
    }     // end method updateModel

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        String translatedLabel = getTranslatedDatePattern();
        String dateLabel = "";
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            dateLabel = retrieveText(labelTags[i], labelText[i]);
            fieldLabels[i].setText(LocaleUtilities.formatComplexMessage(dateLabel, translatedLabel));
        }

        startDateField.setLabel(fieldLabels[START_DATE_ROW]);
        endDateField.setLabel(fieldLabels[END_DATE_ROW]);
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

        DateSearchBean bean = new DateSearchBean();

        DateSearchBeanModel beanModel = new DateSearchBeanModel();

        beanModel.setStartDate
          (DomainGateway.getFactory().getEYSDateInstance());
        beanModel.setEndDate
          (DomainGateway.getFactory().getEYSDateInstance());

        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}   // end class DateSearchBean
