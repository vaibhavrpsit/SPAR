/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailDetailBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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


/**
 * This is the bean model used by the EmailDetailBean. <P>
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 * @see oracle.retail.stores.pos.ui.beans.EmailDetailBean
 */
public class EmailDetailBeanModel extends POSBaseBeanModel
{
    private String fieldEmailDetail = new String();
    
    /** 
        Indicates the appropriate state of the reply button, default true.
    **/
    private boolean replyEnabled = true;
    
    /**
     * EmailDetailBeanModel constructor
     */
    public EmailDetailBeanModel() 
    {
        super();
    }
    
    //---------------------------------------------------------------------
    /**
        Retrieves display text. <P>
        @return the string.
    **/
    //---------------------------------------------------------------------
    public String getEmailDetail()
    {                                   // begin getEmailDetail()
        return(fieldEmailDetail);
    }                                   // end getEmailDetail()
    
    //---------------------------------------------------------------------
    /**
        Sets transaction tax property value. <P>
        @param value tax property
    **/
    //---------------------------------------------------------------------
    public void setEmailDetail(String displayText)
    {                                   // begin setEmailDetail()
        fieldEmailDetail = displayText;
    }
    /**
        Sets the reply button to enabled or disabled. <P>
        @param boolean.
    **/
    //--------------------------------------------------------------------- 
    public void setReplyEnabled(boolean value)
    {                                   // begin setReplyEnabled()
        replyEnabled = value;
    }                                  // end setReplyEnabled()

    //---------------------------------------------------------------------
    /**
        Sets the reply status to enabled or disabled. <P>
        @param boolean.
    **/
    //--------------------------------------------------------------------- 
    public boolean getReplyEnabled()
    {                                   // begin getReplyEnabled()
        return(replyEnabled);
    }                                  // end getReplyEnabled()                                  // end getEmailDetail()
}
