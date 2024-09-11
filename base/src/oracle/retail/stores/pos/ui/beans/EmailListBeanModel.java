/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailListBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:48 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:55 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:15 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
 * This is the bean model that is used by the EmailListBean. <P>
 * This bean model is used to access arrays of data pertaining to
 * EMessages.
 * @see oracle.retail.stores.pos.ui.beans.EmailListBean
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 * @deprecated as of release 5.0.0
 */
//--------------------------------------------------------------------------
public class EmailListBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "EmailListBeanModel";

    /**
        emessage entry
    **/
    private EMessageIfc[] eMessages = null;

    /**
        selected entry
    **/
    private EMessageIfc selectedMessage = null;

    //--------------------------------------------------------------------------
    /**
        EmailListBeanModel constructor comment.
    **/
    //--------------------------------------------------------------------------
    public EmailListBeanModel()
    {
    }

    //--------------------------------------------------------------------------
    /**
        Gets the eMessage property value.
        @return EMessageIfc[] the eMessage property value array.
        @see #setEMessages
    **/
    //--------------------------------------------------------------------------
    public EMessageIfc[] getEMessages()
    {
        return eMessages;
    }

    //--------------------------------------------------------------------------
    /**
    * Sets the EMessage property value.
    * @param eMessages the new value for the property.
    * @see #getEMessages
    */
    //--------------------------------------------------------------------------
    public void setEMessages(EMessageIfc[] eMessages)
    {
        this.eMessages = eMessages;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selectedMessage property value.
        @return EMessageIfc the eMessage property value.
        @see #setSelectedMessage
    **/
    //--------------------------------------------------------------------------
    public EMessageIfc getSelectedMessage()
    {
        return selectedMessage;
    }

    //--------------------------------------------------------------------------
    /**
    * Sets the selectedMessage property value.
    * @param selectedMessage the new value for the property.
    * @see #getSelectedMessage
    */
    //--------------------------------------------------------------------------
    public void setSelectedMessage(EMessageIfc eMessage)
    {
        // copy the selected eMessage entry obtained from the
        // site into this bean model's selected message value
        selectedMessage = eMessage;
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + CLASSNAME + " (Revision " +
                                      getRevisionNumber() + ")" + hashCode());
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
}
