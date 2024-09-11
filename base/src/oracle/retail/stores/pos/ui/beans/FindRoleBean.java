/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FindRoleBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:10:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 28 2003 15:17:18   baa
 * fix focus problem
 * Resolution for 2113: System crash when F1, F1 then Enter selected in Find Role
 * 
 *    Rev 1.3   26 Jul 2003 21:10:20   baa
 * fix lost of focus when exiting help
 * 
 *    Rev 1.2   Aug 14 2002 18:17:44   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   27 Jun 2002 10:57:06   jbp
 * removed dirty model
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 14:48:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:12   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 08 2002 09:02:12   mpm
 * Externalized text for role UI screens.
 *
 *    Rev 1.2   Mar 04 2002 14:15:38   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    The Find Role bean displays a list of roles.
 *
 *    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class FindRoleBean extends SelectionListBean
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** text for the role label */
    public static String ROLE_LABEL = "Role Name:";

    /**
     *  role label tag
     */
    public static String ROLE_LABEL_TAG = "RoleNameLabel";

    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    public FindRoleBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the bean.
     */
    public void configure()
    {
        super.configure();

        setName("FindRoleBean");
        setLabelText(ROLE_LABEL);
        setLabelTags(ROLE_LABEL_TAG);
        beanModel = new FindRoleBeanModel();
    }

    //--------------------------------------------------------------------------
    /**
     *    Updates the model property value. Called to prepare the model for
     *    return to the business logic.
     */
    public void updateModel()
    {
        if(beanModel instanceof FindRoleBeanModel)
        {
            FindRoleBeanModel myModel = (FindRoleBeanModel)beanModel;

            // we only want to retrieve the selected role title index,
            // and not the entire list of role titles
            if (choiceList.getSelectedIndex() > 0)
            {
                // save the selected role title as part of the bean Model
                myModel.setRoleSelectedIndex(choiceList.getSelectedIndex());
            }
            else
            {
                // save the selected role title as part of the bean Model
                myModel.setRoleSelectedIndex(0);
            }            
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Update the bean if it's been changed
     */
    protected void updateBean()
    {
        if (beanModel instanceof FindRoleBeanModel)
        {
            FindRoleBeanModel myModel = (FindRoleBeanModel)beanModel;

            choiceList.setModel(new POSListModel(myModel.getRoleTitles()));
            //set the selected index to the first one
            choiceList.setSelectedIndex(0);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Returns a string representation of this object.
     *  @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());

    }

    //--------------------------------------------------------------------------
    /**
     *  Returns the revision number of the class.
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
     *  Main entry point for testing.
     *  @param args command line parameters
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        FindRoleBean bean = new FindRoleBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
