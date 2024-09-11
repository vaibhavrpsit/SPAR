/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectBusinessDateBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.2.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:07 PM  Robert Pearse   
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
 *    Rev 1.1   Sep 25 2003 16:12:14   dcobb
 * Migrate to jdk 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:49:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:28   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 08 2002 16:06:10   mpm
 * Externalized text for business date UI screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
    Provides list of business dates from which the user can select to be used
    by transactions preformed on this register. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.pos.ui.beans.SelectBusinessDateBeanModel
**/
//------------------------------------------------------------------------------
public class SelectBusinessDateBean extends BaseBeanAdapter
{                                       // begin class SelectBusinessDateBean
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** list label */
    protected JLabel businessDateLabel = null;

    /** list field */
    protected JList fieldBusinessDateList = null;

    /* scroll pane */
    protected JScrollPane businessDateScrollPane = null;

    /** bean model */
    protected SelectBusinessDateBeanModel beanModel = null;

    // holds list of dates to display
    protected ValidatingComboBox dateList = null;
   

    /** business date field label string */
    protected static final String labelTag = "BusinessDateLabel";

    //---------------------------------------------------------------------
    /**
        Constructs SelectBusinessDateBean object. <P>
    **/
    //---------------------------------------------------------------------
    public SelectBusinessDateBean()
    {                                   // begin SelectBusinessDateBean()
        super();
        initialize();
    }                                  // end SelectBusinessDateBean()

    //---------------------------------------------------------------------
    /**
        Pulls data from bean to update the model. <P>
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {                                   // begin updateModel()
        // use index of selected item to retrieve original date from the model
        int dateIndex = dateList.getSelectedIndex();

        // set the selected date back to the model based upon the selected index
        beanModel.setSelectedDate(beanModel.getBusinessDates()[dateIndex]);
    }                                   // end updateModel()

    //---------------------------------------------------------------------
    /**
        Sets the model data to the bean. <P>
        @param model bean model data
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {                                   // begin setModel()
        // if no model, throw an exception
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SelectBusinessDateBean model to null");
        }
        else if (model instanceof SelectBusinessDateBeanModel)
        {
            beanModel = (SelectBusinessDateBeanModel) model;
            updateBean();
        }
    }                                   // end setModel()
    //------------------------------------------------------------------------------
    /**
     *    Updates the information displayed on the screen with the model's
     *    data.
     */
    //------------------------------------------------------------------------------
    protected void updateBean()
    {                                   // begin updateBean()
        int n = beanModel.getBusinessDates().length;
        String dates[] = new String[n];
        EYSDateField businessDate = new EYSDateField();
        for (int i = 0; i < n; i++)
        {
            businessDate.setDate(beanModel.getBusinessDates()[i]); 
            dates[i] = businessDate.getText();
        }

        dateList.setModel(new ValidatingComboBoxModel(dates));
        dateList.setSelectedIndex(0);
    }

    //---------------------------------------------------------------------
    /**
        Initializes the bean. <P>
    **/
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("SelectBusinessDateBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        // set business date label
        businessDateLabel =
            uiFactory.createLabel(retrieveText(labelTag,
                                               "Business Date:"), null, UI_LABEL);

        dateList = uiFactory.createValidatingComboBox("BusinessDateList", "false", "10");

        JLabel[] labels = {businessDateLabel};
        JComponent[] comps = {dateList};

        UIUtilities.layoutDataPanel(this, labels, comps);
    }                                   // end initialize()

    //---------------------------------------------------------------------
    /**
       Overrides set visible to request focus.
       @param aFlag that indicates if the component should be visible or not.
    */
    //---------------------------------------------------------------------
    public void setVisible( boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            setCurrentFocus(dateList);
        }
    }
    
    //--------------------------------------------------------------------------
    /**
      * Activates this bean.
      */
     public void activate()
     {
         super.activate();
         dateList.addFocusListener(this);
     }

    //--------------------------------------------------------------------------
    /**
     *  Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        dateList.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        if (businessDateLabel != null)
        {
            businessDateLabel.setText(retrieveText(labelTag,
                                                   businessDateLabel));
        }
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
        Retrieves the revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: SelectBusinessDatebean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        SelectBusinessDateBean main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new SelectBusinessDateBean());
    }

}
