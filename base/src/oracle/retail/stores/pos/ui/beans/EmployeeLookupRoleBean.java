/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeLookupRoleBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Jan 26 2004 16:03:02   jriggins
 * Initial revision.
 * Resolution for 3597: Employee 7.0 Updates
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
 Contains the visual presentation for Looking up the Employee by name.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//----------------------------------------------------------------------------
public class EmployeeLookupRoleBean extends ValidatingBean
{
    /** Revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** First Name field and label ID */
    protected static final int ROLES = 0;

    /** Label Text array */
    protected static String labelText[] = {"Role:"};

    /** Label Text array */
    protected static String labelTags[] = {"RolesLabel"};

    /** Label components */
    protected JLabel[] fieldLabels = new JLabel[labelText.length];

    /** Model used to transport data between the UI and Business logic */
    protected EmployeeLookupRoleBeanModel beanModel = new EmployeeLookupRoleBeanModel();

    /** Roles component */
    protected ValidatingComboBox rolesField = null;

    /** Indicates if the bean should be updated */
    protected boolean dirtyModel                   = false;

    //----------------------------------------------------------------------------
    /***
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public EmployeeLookupRoleBean()
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
        setName("EmployeeLookupRoleBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        // Set the labels
        for(int i=0; i<labelText.length; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }
        
        // Set the corresponding fields
        rolesField = uiFactory.createValidatingComboBox("rolesField", "false", "20");        
        JComponent[] fields = {rolesField};
        
        // Pass them to the panel
        UIUtilities.layoutDataPanel(this, fieldLabels, fields, false);
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
            setCurrentFocus(rolesField);
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
        rolesField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * Deactivates this bean.
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        rolesField.removeFocusListener(this);
    }

    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setSelectedRoleIndex(rolesField.getSelectedIndex());
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
            throw new NullPointerException("Attempt to set EmployeeLookupRoleBeanModel" +
            " to null");
        }
        Object oldValue = beanModel;
        if (model instanceof EmployeeLookupRoleBeanModel)
        {
            beanModel = (EmployeeLookupRoleBeanModel)model;
            dirtyModel = true;
            updateBean();
        }
    }
    //---------------------------------------------------------------------
    /**
     * Update the bean if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        String[] roleTitles = beanModel.getRoleTitles();
        if (roleTitles != null)
        {    
            rolesField.setModel(new ValidatingComboBoxModel(roleTitles));
            rolesField.setSelectedIndex(0);
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
        String strResult = new String("Class: EmployeeLookupRoleBean (Revision " +
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
        EmployeeLookupRoleBean bean = new EmployeeLookupRoleBean();
        EmployeeLookupRoleBeanModel beanModel = new EmployeeLookupRoleBeanModel();

        // Populate a list of roles
        RoleIfc[] roles = new RoleIfc[10];
        for (int x = 0; x < roles.length; x++)
        {    
            RoleIfc role = new Role();
            role.setTitle("Role " + (x+1));
            roles[x] = role;
        }
        beanModel.setRoles(roles);        
        bean.setModel(beanModel);
        
//        UIUtilities.setUpTest();
//        UIUtilities.doBeanTest(bean);
//        
//        // Initialize the UI components
//        javax.swing.JFrame frame = new
//        javax.swing.JFrame("EmployeeLookupRoleBean");
//
//        EmployeeLookupRoleBean bean = new EmployeeLookupRoleBean();
//        EmployeeLookupRoleBeanModel beanModel = new EmployeeLookupRoleBeanModel();
//
//        // Populate a list of roles
//        RoleIfc[] roles = new RoleIfc[10];
//        for (int x = 0; x < roles.length; x++)
//        {    
//            RoleIfc role = new Role();
//            role.setTitle("Role " + (x+1));
//            roles[x] = role;
//        }
//        beanModel.setRoles(roles);
//                
//        bean.setModel(beanModel);
//        bean.activate();
//
//        frame.setSize(530, 290);
//        frame.getContentPane().add(bean);
//        frame.show();
    }
}
