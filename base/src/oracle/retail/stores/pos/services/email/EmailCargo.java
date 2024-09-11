/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/EmailCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:50:10  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:38  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:24   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 09 2002 15:24:16   dfh
 * test
 * Resolution for POS SCR-187: CR/Email, app hangs when unauth user enters Password screen
 * 
 *    Rev 1.1   Jan 09 2002 15:06:38   dfh
 * updates to return access functionID E_MAIL for security
 * access
 * Resolution for POS SCR-187: CR/Email, app hangs when unauth user enters Password screen
 * 
 *    Rev 1.0   Sep 24 2001 11:16:28   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email;

// java imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;

//--------------------------------------------------------------------------
/**
    Cargo that carries the data for the email service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EmailCargo 
    extends AbstractFinancialCargo implements DBErrorCargoIfc
{        
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //------- Constants for defining search type ---------//
    public static final int   SEARCH_BY_ORDER_ID        = 0;
    public static final int   SEARCH_BY_CUSTOMER        = 1;
    public static final int   SEARCH_FOR_NEW_EMAILS     = 3;

    /**
        order ID
    **/
    protected String orderID = "";

    /**
        emessage
    **/
    protected EMessageIfc emessage = null;

    /**
        emessage list
    **/
    protected EMessageIfc[] emessageList = null;

    /**
        message detail
    **/
    protected String messageDetail = "";

    /**
        reply message
    **/
    protected String replyMessage = "";

    /**
        fatal error
    **/
    protected boolean fatalError = false;

    /**
        store ID
    **/
    protected String storeID = "";
    
    /**
        cashier ID
    **/
    protected String cashierID = "";

    /**
        customer
    **/
    protected CustomerIfc customer = null;

    /**
        search Start Date
    **/
    protected EYSDate searchStartDate = null;
    
    /**
        search End Date
    **/
    protected EYSDate searchEndDate   = null;
    
    /**
        use Date Range
    **/
    protected boolean useDateRange    = false;

    /**
        data exception error code
    **/
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
        email search type - customer/orderID
    **/
    protected int searchType = EmailCargo.SEARCH_BY_ORDER_ID;

    /**
        Sets the search method which the cargo will be used for.
        @param name int value of the searchMethod
    **/
    //--------------------------------------------------------------------------
    public void setSearchMethod(int name)
    {
        searchType = name;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the name of a search method which the cargo will be used for.
        @return int representation of the searchMethod
    **/
    //--------------------------------------------------------------------------
    public int getSearchMethod()
    {
        return searchType;
    }

    //--------------------------------------------------------------------------
    /**
        Adds an OrderID to the cargo.
        @param newOrderID the OrderID to add
    **/
    //--------------------------------------------------------------------------    
    public void setOrderID(String value)
    {
        orderID = value;
    }
    //--------------------------------------------------------------------------
    /**
        Returns the OrderID held in cargo.
        @return String order ID
    **/
    //--------------------------------------------------------------------------    
    public String getOrderID()
    {
        return orderID;        
    }

    //--------------------------------------------------------------------------
    /**
        Adds a single EMessageIfc to the cargo.
        @param value the EMessageIfc to add
    **/
    //--------------------------------------------------------------------------
    public void setSelectedMessage(EMessageIfc value)
    {
        emessage = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the EMessage held in cargo.
        @return EMessageIfc
    **/
    //--------------------------------------------------------------------------
    public EMessageIfc getSelectedMessage()
    {
        return emessage;        
    }

    //--------------------------------------------------------------------------
    /**
        Adds a EMessageIfc list to the cargo.
        @param value the EMessageIfc[] to add
    **/
    //--------------------------------------------------------------------------
    public void setEMessageList(EMessageIfc[] value)
    {
        emessageList = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the EMessage list held in cargo.
        @return EMessageIfc[]
    **/
    //--------------------------------------------------------------------------
    public EMessageIfc[] getEMessageList()
    {
        return emessageList;        
    }

    //--------------------------------------------------------------------------
    /**
        Adds the EMessage detail to the cargo.
        @param value the message String to add
    **/
    //--------------------------------------------------------------------------
    public void setMessageDetail(String value)
    {
        messageDetail = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the EMessage detail held in cargo.
        @return String message detail string
    **/
    //--------------------------------------------------------------------------
    public String getMessageDetail()
    {
        return messageDetail;        
    }
     
    //--------------------------------------------------------------------------
    /**
        Adds the reply message to the cargo.
        @param value the reply message String to add
    **/
    //--------------------------------------------------------------------------
    public void setReplyMessage(String value)
    {
        replyMessage = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the reply message held in cargo.
        @return String reply message string
    **/
    //--------------------------------------------------------------------------
    public String getReplyMessage()
    {
        return replyMessage;        
    }
        
    //--------------------------------------------------------------------------
    /**
        Sets the boolean fatal error value in the cargo.
        @param the boolean value
    **/
    //--------------------------------------------------------------------------
    public void setFatalError(boolean value)
    {
        fatalError = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the fatalError value held in cargo.
        @return boolean fatalError
    **/
    //--------------------------------------------------------------------------
    public boolean getFatalError()
    {
        return fatalError;        
    }
    
    
    //--------------------------------------------------------------------------
    /**
        Sets the store ID in the cargo
        @param the storeID string value
    **/
    //--------------------------------------------------------------------------    
    public void setStoreID(String value)
    {
        storeID = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the storeID value held in cargo.
        @return String storeID
    **/
    //--------------------------------------------------------------------------
    public String getStoreID()
    {
        return storeID;        
    }
    
    //--------------------------------------------------------------------------
    /**
        Sets the cashierID in the cargo
        @param the cashierID string value
    **/
    //--------------------------------------------------------------------------
    public void setCashierID(String value)
    {
        cashierID = value;
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the cashierID value held in cargo.
        @return String cashierID
    **/
    //--------------------------------------------------------------------------
    public String getCashierID()
    {
        return cashierID;        
    }
    
    //--------------------------------------------------------------------------
    /**
        Gets the selected customer.  This is the customer whose emessages we
        wish to query.      <p>
        @return Customer
    **/
    //--------------------------------------------------------------------------
    public CustomerIfc getSelectedCustomer()
    {
        return customer;
    }    

    //----------------------------------------------------------------------
    /**
        Sets the customer selected via Customer Find.  
        This is the customer whose emessages we wish to query. <p>
        @param Customer
    **/
    //--------------------------------------------------------------------------
    public void setSelectedCustomer(CustomerIfc value)
    {
        customer = value;
    }    
            
    //----------------------------------------------------------------------
    /**
        Sets the summaryStart search date which the cargo will be used for.
        @param name int value of the summaryStatus
    **/
    //--------------------------------------------------------------------------
    public void setStartDate(EYSDate date)
    {
        searchStartDate = date;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the value of summaryStart, search date method which the cargo will be used for.
        @return int representation of the summaryStatus
    **/
    //--------------------------------------------------------------------------
    public EYSDate getStartDate()
    {
        return searchStartDate;
    }    

    //----------------------------------------------------------------------
    /**
        Sets the summaryEnd search date which the cargo will be used for.
        @param name int value of the summaryStatus
    **/
    //--------------------------------------------------------------------------
    public void setEndDate(EYSDate date)
    {
        searchEndDate = date;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the value of summaryEnd, search date method which the cargo will be used for.
        @return int representation of the summaryStatus
    **/
    //--------------------------------------------------------------------------
    public EYSDate getEndDate()
    {
        return searchEndDate;
    }

    //----------------------------------------------------------------------
    /**
        Sets the useDateRange flag for date range searches.
        @param name boolean flag valud of useDateRange
    **/
    //--------------------------------------------------------------------------
    public void setDateRange(boolean flag)
    {
        useDateRange = flag;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the value of useDateRange, whether to use date range in searches.
        @return boolean representation of the useDateRange
    **/
    //--------------------------------------------------------------------------
    public boolean getDateRange()
    {
        return useDateRange;
    }
 
    //----------------------------------------------------------------------
    /**
        Returns the error code returned with a DataException.
        <P>
        @return the integer value
    **/
    //----------------------------------------------------------------------
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    //----------------------------------------------------------------------
    /**
        Sets the error code returned with a DataException.
        <P>
        @param  the integer value
    **/
    //----------------------------------------------------------------------
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }
    
    //---------------------------------------------------------------------
    /**
        Take a snapshot of the current state of the cargo. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Cargo state is placed on the stack.
        </UL>
        @param none
        @return SnapshotIfc  Snapshot object containing relevent data from the cargo
        @exception none
    **/
    //---------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {                                   // Begin makeSnapshot()

        return new TourCamSnapshot(this);

    }                                   // End makeSnapshot()

    //---------------------------------------------------------------------
    /**
        Restore cargo to original state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Original cargo values are restored
        </UL>
        @param snapshot  object that contains cargo state information
        @return void
        @exception ObjectRestoreException   Bedrock is unable to restore cargo to original state
    **/
    //---------------------------------------------------------------------
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {                                   // Begin restoreSnapshot()
    }                                   // End restoreSnapshot()
   
    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.E_MAIL;
    }
   
    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
