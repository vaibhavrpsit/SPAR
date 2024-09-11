/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditRoleBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:10:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:17:30   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:06   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:20   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:04   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 08 2002 09:02:10   mpm
 * Externalized text for role UI screens.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
    The Update Role bean presents the functionality of the Update Role screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class EditRoleBean extends ValidatingBean
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** bean model */
    protected EditRoleBeanModel beanModel = new EditRoleBeanModel();

    /** role name label */
    protected JLabel roleNameLabel = null;

    /** role name field */
    protected ConstrainedTextField roleNameField = null;

    /**
     * role name label
    **/
    protected static String ROLE_NAME_LABEL = "RoleNameLabel";

    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    public EditRoleBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initialize the class.
    **/
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("EditRoleBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        roleNameLabel = uiFactory.createLabel("Role Name:", null, UI_LABEL);

        roleNameField = new ConstrainedTextField();
        roleNameField.setName("roleNameField");
        roleNameField.setColumns(25);
        roleNameField.setMinLength(1);
        roleNameField.setMaxLength(60);


        JLabel[] labels = {roleNameLabel};
        JComponent[] fields = {roleNameField};
        UIUtilities.layoutDataPanel(this, labels, fields, false);
    }

    //---------------------------------------------------------------------
    /**
        Updates the model property value.
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setRoleName(roleNameField.getText());
    }

    //---------------------------------------------------------------------
    /**
        Sets the model property.
        @param model The new value for the property.
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set UpdateRoleBean " +
                "model to null");
        }
        else
        {
            if (model instanceof EditRoleBeanModel)
            {
                beanModel = (EditRoleBeanModel)model;
                roleNameField.setText(beanModel.getRoleName());
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Gets the bean model to set up return from the error screen.
        @return the model
    **/
    //---------------------------------------------------------------------
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
        roleNameLabel.setText(retrieveText(ROLE_NAME_LABEL,roleNameLabel));
        roleNameField.setLabel(roleNameLabel);
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: EditRoleBean (Revision "
            + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        EditRoleBean bean = new EditRoleBean();
        UIUtilities.doBeanTest(bean);
    }
}
