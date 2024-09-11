/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonCode.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.utility.Util;


//----------------------------------------------------------------------------
/**
**/
//----------------------------------------------------------------------------
public class ReasonCode extends POSBaseBeanModel
{
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    protected String reasonCodeLevel = ReasonCodesCommon.STORE;
    protected String reasonCodeGroup = "";

    /** The displayable reason code  **/
    protected String reasonCodeName = "";
    /** The user-proposed displayable reason code  **/
    protected String newReasonCodeName = "";

    /** The database ID.  **/
    protected String databaseId = "";
    /** The user-proposed database ID.  **/
    protected String newDatabaseId = "";
    /**
        the reference key (not visible to the user)
    **/
    protected String referenceKey = "";

    /** This service handles both reason codes, which reside in the DB
        and lists of text, which are parameters.  **/
    protected boolean modifyingParameter = false;

    /** When this data member has been set to false, it means this code
        has been deleted and will not be displayed in the list.  **/
    protected boolean enabled = true;

    /** This model and its bean are used to edit and add data; this indicator
        allows the bean to distinguish between them.  **/
    protected boolean editBean = true;

    /** Logical name of the code **/
    protected String codeName = "";

    //-------------------------------------------------------------------------
    /**
        Returns the value of the ReasonCodeName field. <p>
        @return the value of ReasonCodeName
    **/
    //-------------------------------------------------------------------------
    public String getReasonCodeName()
    {
        return reasonCodeName;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the value of the ReasonCodeName field. <p>
        @return the value of ReasonCodeName
    **/
    //-------------------------------------------------------------------------
    public String getNewReasonCodeName()
    {
        return newReasonCodeName;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the value of the ReasonCodeGroup field. <p>
        @return the value of ReasonCodeGroup
    **/
    //-------------------------------------------------------------------------
    public String getReasonCodeGroup()
    {
        return reasonCodeGroup;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the value of the ReasonCodeLevel field. <p>
        @return the value of ReasonCodeLevel
    **/
    //-------------------------------------------------------------------------
    public String getReasonCodeLevel()
    {
        return reasonCodeLevel;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the value of the DatabaseId field. <p>
        @return the value of DatabaseId
    **/
    //-------------------------------------------------------------------------
    public String getDatabaseId()
    {
        return databaseId;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the value of the DatabaseId field. <p>
        @return the value of DatabaseId
    **/
    //-------------------------------------------------------------------------
    public String getNewDatabaseId()
    {
        return newDatabaseId;
    }

    //--------------------------------------------------------------------------
    /**
        Returns indicator for modifying Parameter or Reason Code. <P>
        @return true if modifying a parameter, false otherwise.
    **/
    //--------------------------------------------------------------------------
    public boolean getModifyingParameter()
    {
        return modifyingParameter;
    }

    //---------------------------------------------------------------------
    /**
        Returns enabled flag. <P>
        @return enabled flag
    **/
    //---------------------------------------------------------------------
    public boolean getEnabled()
    {
        return(enabled);
    }

    //---------------------------------------------------------------------
    /**
        Sets enabled flag. <P>
        @param value enabled flag
    **/
    //---------------------------------------------------------------------
    public void setEnabled(boolean value)
    {
        enabled = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns edit/add flag. <P>
        @return edit/add flag
    **/
    //---------------------------------------------------------------------
    public boolean getEditBean()
    {
        return editBean;
    }

    //---------------------------------------------------------------------
    /**
        Sets edit/add flag. <P>
        @param value edit/add flag
    **/
    //---------------------------------------------------------------------
    public void setEditBean(boolean value)
    {
        editBean = value;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the ReasonCodeName field. <p>
        @param reasonCodeName   the value to be set for ReasonCodeName
    **/
    //-------------------------------------------------------------------------
    public void setReasonCodeName(String reasonCodeName)
    {
        this.reasonCodeName = reasonCodeName;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the ReasonCodeName field. <p>
        @param reasonCodeName   the value to be set for ReasonCodeName
    **/
    //-------------------------------------------------------------------------
    public void setNewReasonCodeName(String reasonCodeName)
    {
        this.newReasonCodeName = reasonCodeName;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the ReasonCodeGroup field. <p>
        @param reasonCodeGroup  the value to be set for ReasonCodeGroup
    **/
    //-------------------------------------------------------------------------
    public void setReasonCodeGroup(String reasonCodeGroup)
    {
        this.reasonCodeGroup = reasonCodeGroup;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the ReasonCodeLevel field. <p>
        @param reasonCodeLevel  the value to be set for ReasonCodeLevel
    **/
    //-------------------------------------------------------------------------
    public void setReasonCodeLevel(String reasonCodeLevel)
    {
        this.reasonCodeLevel = reasonCodeLevel;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the DatabaseId field
        @param databaseId   the value to be set for DatabaseId
    **/
    //-------------------------------------------------------------------------
    public void setDatabaseId(String databaseId)
    {
        this.databaseId = databaseId;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the DatabaseId field
        @param databaseId   the value to be set for DatabaseId
    **/
    //-------------------------------------------------------------------------
    public void setNewDatabaseId(String databaseId)
    {
        this.newDatabaseId = databaseId;
    }

    //--------------------------------------------------------------------------
    /**
        Sets indicator for modifying Parameter or Reason Code. <P>
        @param value    true if modifying a parameter, false otherwise
    **/
    //--------------------------------------------------------------------------
    public void setModifyingParameter(boolean value)
    {
        modifyingParameter = value;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the reference key field. <p>
        @param value    the value to be set for reference key
    **/
    //-------------------------------------------------------------------------
    public void setReferenceKey(String value)
    {
        referenceKey = value;
    }

    //-------------------------------------------------------------------------
    /**
        Returns the reference key field. <p>
        @return the reference key
    **/
    //-------------------------------------------------------------------------
    public String getReferenceKey()
    {
        return(referenceKey);
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

        buff.append("Class: " + getClass().getName() + " Revision: " +
                    revisionNumber + "\n");
        buff.append("Name [" + reasonCodeName + "]\n");
        buff.append("Group [" + reasonCodeGroup + "]\n");
        buff.append("Level [" + reasonCodeLevel + "]\n");
        buff.append("DatabaseId [" + databaseId + "]\n");
        buff.append("Reference key [" + referenceKey + "]" + Util.EOL);

        return(buff.toString());
    }

    /**
     * @return the codeName
     */
    public String getCodeName()
    {
        return codeName;
    }

    /**
     * @param codeName the codeName to set
     */
    public void setCodeName(String codeName)
    {
        this.codeName = codeName;
    }


}
