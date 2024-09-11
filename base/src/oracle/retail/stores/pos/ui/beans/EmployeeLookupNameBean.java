/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeLookupNameBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    03/12/09 - Min length of firstName and lastName fields is now 1
 *    sgu       03/11/09 - change names from alphanumerice to text fields
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/24/2007 11:17:16 AM  Ashok.Mondal    CR 4376
 *      :V7.2.2 merge to trunk.
 * 3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse
 *
 *Revision 1.4  2004/07/17 19:21:23  jdeleau
 *@scr 5624 Make sure errors are focused on the beans, if an error is found
 *during validation.
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:52:32   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:17:36   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:51:22   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:26   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:54:22   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 23 2002 15:04:14   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
   Contains the visual presentation for Looking up the Employee by name.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class EmployeeLookupNameBean extends ValidatingBean
{
    /** Revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** First Name field and label ID */
    protected static final int FIRST_NAME = 0;

    /** Lase Name field and label ID */
    protected static final int LAST_NAME = 1;

    /** Label Text array */
    protected static String labelText[] = {"First Name:", "Last Name:"};

    /** Label Text array */
    protected static String labelTags[] = {"FirstNameLabel", "LastNameLabel"};

    /** Label components */
    protected JLabel[] fieldLabels = new JLabel[labelText.length];

    /** Model used to transport data between the UI and Business logic */
    protected EmployeeLookupNameBeanModel beanModel    = new EmployeeLookupNameBeanModel();

    /** First Name component */
    protected ConstrainedTextField firstNameField  = null;

    /** Last Name component */
    protected ConstrainedTextField lastNameField   = null;

    /** Indicates if the bean should be updated */
    protected boolean dirtyModel                   = false;

    //----------------------------------------------------------------------------
    /***
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public EmployeeLookupNameBean()
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
        setName("EmployeeMasterBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        for(int i=0; i<labelText.length; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "20");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");

        JComponent[] fields = {firstNameField, lastNameField};
        UIUtilities.layoutDataPanel(this, fieldLabels, fields);
    }

    //------------------------------------------------------------------------
    /**
     * Overrides the inherited setVisible() to set the focus on the reply area.
       @param value boolean
     */
    //------------------------------------------------------------------------
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value && !errorFound())
        {
            setCurrentFocus(firstNameField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Activates this bean.
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * Deactivates this bean.
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
    }

    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setFirstName(firstNameField.getText());
        beanModel.setLastName(lastNameField.getText());
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
            throw new NullPointerException("Attempt to set EmployeeLookupNameBeanModel" +
                                           " to null");
        }
        Object oldValue = beanModel;
        if (model instanceof EmployeeLookupNameBeanModel)
        {
            beanModel = (EmployeeLookupNameBeanModel)model;
            dirtyModel = true;
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
        if(dirtyModel)
        {
            firstNameField.setText(beanModel.getFirstName());
            lastNameField.setText(beanModel.getLastName());

            dirtyModel = false;
        }
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        for (int i = 0; i < labelTags.length; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                fieldLabels[i]));
        }
        lastNameField.setLabel(fieldLabels[LAST_NAME]);
        firstNameField.setLabel(fieldLabels[FIRST_NAME]);
    }

    //------------------------------------------------------------------------
    /**
     * Gets the model for error screen processing.
     * @return POSBaseBeanModel
    */
    //------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        updateModel();
        return beanModel;
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EmployeeLookupNameBean (Revision " +
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

    //--------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    //--------------------------------------------------------------------------
    public static void main(String[] args)
    {
        javax.swing.JFrame frame = new
          javax.swing.JFrame("EmployeeLookupNameBean");

        EmployeeLookupNameBean bean = new EmployeeLookupNameBean();

        EmployeeLookupNameBeanModel beanModel = new EmployeeLookupNameBeanModel();

        beanModel.setFirstName("Frank");
        beanModel.setLastName("Black");

        bean.setModel(beanModel);
        bean.activate();

        frame.setSize(530, 290);
        frame.getContentPane().add(bean);
        frame.show();
    }
}
