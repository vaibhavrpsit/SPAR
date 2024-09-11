/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectGroupBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:49:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:31:46   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:35:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.util.Vector;

//----------------------------------------------------------------------------
/**
**/
//----------------------------------------------------------------------------
public class SelectGroupBeanModel extends POSBaseBeanModel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected Vector groups = new Vector();
    protected String group = "*no group*";
    protected ReasonCodeGroupBeanModel selectedGroup;

    //-------------------------------------------------------------------------
    /**
        Returns the value of the Groups field. <p>
        @return the value of Groups
    **/
    //-------------------------------------------------------------------------
    public Vector getGroups()
    {
        return groups;
    }

    public String getGroup()
    {
        return group;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the selected group. <p>
        @return the selected group
    **/
    //-------------------------------------------------------------------------
    public ReasonCodeGroupBeanModel getSelectedGroup()
    {
        return(selectedGroup);
    }

    //-------------------------------------------------------------------------
    /**
        Sets the Groups field. <p>
        @param groups   the value to be set for Groups
    **/
    //-------------------------------------------------------------------------
    public void setGroups(Vector groups)
    {
        this.groups = groups;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    //-------------------------------------------------------------------------
    /**
        Adds a group. <p>
        @param group    the value to add to the list of Groups
    **/
    //-------------------------------------------------------------------------
    public void addGroup(ReasonCodeGroupBeanModel group)
    {
        groups.addElement(group);
    }

    //-------------------------------------------------------------------------
    /**
        Removes a group. <p>
        @param group    the value to remove from the list of Groups
    **/
    //-------------------------------------------------------------------------
    public void removeGroup(ReasonCodeGroupBeanModel group)
    {
        groups.removeElement(group);
    }

    //-------------------------------------------------------------------------
    /**
        Sets the selected group. <p>
        @param group   the value to be set for group
    **/
    //-------------------------------------------------------------------------
    public void setSelectedGroup(ReasonCodeGroupBeanModel group)
    {
        this.selectedGroup = group;
    }

    //-------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object. <p>
        @returns string representing the data in this Object
    **/
    //-------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: SelectGroupBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Groups [" + groups + "]\n");
        buff.append("SelectedGroup [" + selectedGroup + "]\n");

        return(buff.toString());
    }
}
