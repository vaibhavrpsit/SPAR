/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailReplyBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:49 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
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
 * This is the bean model used by the EmailReplyBean. <P>
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 * @see oracle.retail.stores.pos.ui.beans.EmailReplyBean
 */
public class EmailReplyBeanModel extends POSBaseBeanModel
{
    private String fieldEmailDetail = new String();
    private String fieldEmailReply = new String();
    private String fieldEmailReplyHeader = new String();
    
    /**
     * EmailReplyBeanModel constructor comment.
     */
    public EmailReplyBeanModel() 
    {
        super();
    }
    
    //---------------------------------------------------------------------
    /**
        Retrieves email detail text. <P>
        @return the string.
    **/
    //---------------------------------------------------------------------
    public String getEmailDetail()
    {                                   // begin getEmailDetail()
        return(fieldEmailDetail);
    }                                   // end getEmailDetail()
    
    //---------------------------------------------------------------------
    /**
        Retrieves email reply text. <P>
        @return the string.
    **/
    //---------------------------------------------------------------------
    public String getEmailReply()
    {                                   // begin getEmailReply()
        return(fieldEmailReply);
    }                                   // end getEmailReply()
    
    //---------------------------------------------------------------------
    /**
        Retrieves email reply header text. <P>
        @return the string.
    **/
    //---------------------------------------------------------------------
    public String getEmailReplyHeader()
    {                                   // begin getEmailReplyHeader()
        return(fieldEmailReplyHeader);
    }                                   // end getEmailReplyHeader()
    
    //---------------------------------------------------------------------
    /**
        Sets email detail. <P>
        @param value email detail
    **/
    //---------------------------------------------------------------------
    public void setEmailDetail(String value)
    {                                   // begin setEmailDetail()
        fieldEmailDetail = value;
    }                                  // end getEmailDetail()
    
    //---------------------------------------------------------------------
    /**
        Sets email reply. <P>
        @param value email reply
    **/
    //---------------------------------------------------------------------
    public void setEmailReply(String value)
    {                                   // begin setEmailReply()
        fieldEmailReply = value;
    }                                  // end getEmailReply()
    
    //---------------------------------------------------------------------
    /**
        Sets email reply header text. <P>
        @param value email reply header text
    **/
    //---------------------------------------------------------------------
    public void setEmailReplyHeader(String value)
    {                                   // begin setEmailReplyHeader()
        fieldEmailReplyHeader = value;
    }                                  // end getEmailReplyHeader()
}
