/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/AbstractStatusEntity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 10 2003 11:50:36   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.1   May 20 2003 07:22:14   jgs
 * Deprecated getHardtotals() and setHardtotals() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.0   Jun 03 2002 16:51:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:20:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// java imports
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------ 
/**
    This abstract class represents a store financial entity.  Each of these
    entities identifies a sign-on and sign-off operator, a business day, an open
    and close timestamp, accountability flag, a count object and a status. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------ 
public abstract class AbstractStatusEntity implements AbstractStatusEntityIfc
{                                       // begin class AbstractStatusEntity
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1164798435800634341L;


    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        employee who signs on for open
    **/
    protected EmployeeIfc signOnOperator = null;
    /**
        employee who signs on for close
    **/
    protected EmployeeIfc signOffOperator = null;
    /**
        status code @see AbstractStatusEntityIfc
    **/
    protected int status = AbstractStatusEntityIfc.STATUS_CLOSED;
    /**
        status code @see #AbstractStatusEntityIfc
    **/
    protected int previousStatus = AbstractStatusEntityIfc.STATUS_CLOSED;
    /**
        time of last status change
    **/
    protected EYSDate lastStatusChangeTime = null;
    /**
        register-open timestamp
    **/
    protected EYSDate openTime = null;
    /**
        register-close timestamp
    **/
    protected EYSDate closeTime = null;
    /**
        business day (may not be same day as open time)
    **/
    protected EYSDate businessDate = null;

        //---------------------------------------------------------------------
        /**
                Constructs AbstractStatusEntity object. <P>
                <B>Pre-Condition(s)</B>
                <UL>
                <LI>none
                </UL>
                <B>Post-Condition(s)</B>
                <UL>
                <LI>none
                </UL>
        **/
        //---------------------------------------------------------------------
        public AbstractStatusEntity()
        {                                   // begin AbstractStatusEntity()
        setLastStatusChangeTime();
        }                                  // end AbstractStatusEntity()

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public abstract Object clone();

    //---------------------------------------------------------------------
    /**
        Sets attributes in clone of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param newEntity new AbstractStatusEntity object
    **/
    //--------------------------------------------------------------------- 
    protected void setCloneAttributes(AbstractStatusEntity newEntity)
    {                                   // begin setCloneAttributes()
        // set values
        newEntity.setStatus(status);
        newEntity.setPreviousStatus(previousStatus);
        newEntity.setLastStatusChangeTime((EYSDate) lastStatusChangeTime.clone());
        if (signOnOperator != null)
        {
            newEntity.setSignOnOperator((EmployeeIfc) signOnOperator.clone());
        }
        if (signOffOperator != null)
        {
            newEntity.setSignOffOperator((EmployeeIfc) signOffOperator.clone());
        }
        if (openTime != null)
        {
            newEntity.setOpenTime((EYSDate) openTime.clone());
        }
        if (closeTime != null)
        {
            newEntity.setCloseTime((EYSDate) closeTime.clone());
        }
        if (businessDate != null)
        {
            newEntity.setBusinessDate((EYSDate) businessDate.clone());
        }
    }                                   // end setCloneAttributes()
                                        
    //----------------------------------------------------------------------------
    /**
        Retrieves operator who signs on for this entity. <P>
        @return operator who signs on for this entity
    **/
    //----------------------------------------------------------------------------
    public EmployeeIfc getSignOnOperator()
    {                                   // begin getSignOnOperator()
        return(signOnOperator);
    }                                   // end getSignOnOperator()

    //----------------------------------------------------------------------------
    /**
        Sets operator who signs on for this entity. <P>
        @param value  operator who signs on for this entity
    **/
    //----------------------------------------------------------------------------
    public void setSignOnOperator(EmployeeIfc value) 
    {                                   // begin setSignOnOperator()
        signOnOperator = value;
    }                                   // end setSignOnOperator()

    //----------------------------------------------------------------------------
    /**
        Retrieves operator who signs off for this entity. <P>
        @return operator who signs off for this entity
    **/
    //----------------------------------------------------------------------------
    public EmployeeIfc getSignOffOperator()
    {                                   // begin getSignOffOperator()
        return(signOffOperator);
    }                                   // end getSignOffOperator()

    //----------------------------------------------------------------------------
    /**
        Sets operator who signs off for this entity. <P>
        @param value  operator who signs off for this entity
    **/
    //----------------------------------------------------------------------------
    public void setSignOffOperator(EmployeeIfc value) 
    {                                   // begin setSignOffOperator()
        signOffOperator = value;
    }                                   // end setSignOffOperator()

    //----------------------------------------------------------------------------
    /**
        Retrieves status. <P>
        @return status
        @see AbstractStatusEntityIfc
    **/
    //----------------------------------------------------------------------------
    public int getStatus()
    {                                   // begin getStatus()
        return(status);
    }                                   // end getStatus()

    //----------------------------------------------------------------------------
    /**
        Sets status, previous status, last-status-change time. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  status
        @see AbstractStatusEntityIfc 
    **/
    //----------------------------------------------------------------------------
    public void setStatus(int value)
    {                                   // begin setStatus()
        // set previous status
        previousStatus = status;
        // set status
        status = value;
        // set status change time to current date
        lastStatusChangeTime = new EYSDate();
    }                                   // end setStatus()

    //----------------------------------------------------------------------------
    /**
       Indicates if status is open. <P>
           @return true if status is open, false otherwise
       @see AbstractStatusEntityIfc 
    **/
    //----------------------------------------------------------------------------
    public boolean isOpen()
        {
            boolean b = false;
            if (status == AbstractStatusEntityIfc.STATUS_OPEN)
            {
                    b = true;
            }

        return (b);
        }

    //----------------------------------------------------------------------------
    /**
       Indicates if status is closed. <P>
           @return true if status is closed, false otherwise
       @see AbstractStatusEntityIfc 
    **/
    //----------------------------------------------------------------------------
    public boolean isClosed()
        {
            boolean b = false;
            if (status == AbstractStatusEntityIfc.STATUS_CLOSED)
            {
                    b = true;
            }

        return (b);
        }

    //----------------------------------------------------------------------------
    /**
       Indicates if status is suspended. <P>
           @return true if status is suspended, false otherwise
       @see AbstractStatusEntityIfc 
    **/
    //----------------------------------------------------------------------------
    public boolean isSuspended()
        {
            boolean b = false;
            if (status == AbstractStatusEntityIfc.STATUS_SUSPENDED)
            {
            b = true;
            }

        return (b);
        }

    //----------------------------------------------------------------------------
    /**
       Indicates if status is reconciled. <P>
           @return true if status is reconciled, false otherwise
       @see AbstractStatusEntityIfc 
    **/
    //----------------------------------------------------------------------------
    public boolean isReconciled()
        {
            boolean b = false;
            if (status == AbstractStatusEntityIfc.STATUS_RECONCILED)
            {
                    b = true;
            }

        return (b);
        }



    //----------------------------------------------------------------------------
    /**
        Retrieves previous status of the entity. <P>
        @return previous status of the entity
        @see AbstractStatusEntityIfc
    **/
    //----------------------------------------------------------------------------
    public int getPreviousStatus()
    {                                   // begin getPreviousStatus()
        return(previousStatus);
    }                                   // end getPreviousStatus()

    //----------------------------------------------------------------------------
    /**
        Sets previous status of the entity.  This should not be accessed
        from outside this class. <P>
        @param value  previous status of the entity
        @see AbstractStatusEntityIfc 
    **/
    //----------------------------------------------------------------------------
    protected void setPreviousStatus(int value) 
    {                                   // begin setPreviousStatus()
        previousStatus = value;
    }                                   // end setPreviousStatus()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp of last status change. <P>
        @return timestamp of last status change
    **/
    //----------------------------------------------------------------------------
    public EYSDate getLastStatusChangeTime()
    {                                   // begin getLastStatusChangeTime()
        return(lastStatusChangeTime);
    }                                   // end getLastStatusChangeTime()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of last status change. <P>
        @param value  timestamp of last status change
    **/
    //----------------------------------------------------------------------------
    public void setLastStatusChangeTime(EYSDate value)
    {                                   // begin setLastStatusChangeTime()
        lastStatusChangeTime = value;
    }                                   // end setLastStatusChangeTime()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of last status change to current time. <P>
    **/
    //----------------------------------------------------------------------------
    protected void setLastStatusChangeTime()
    {                                   // begin setLastStatusChangeTime()
        lastStatusChangeTime = new EYSDate();
    }                                   // end setLastStatusChangeTime()

    //----------------------------------------------------------------------------
    /**
        Retrieves entity-open timestamp. <P>
        @return entity-open timestamp
    **/
    //----------------------------------------------------------------------------
    public EYSDate getOpenTime()
    {                                   // begin getOpenTime()
        return(openTime);
    }                                   // end getOpenTime()

    //----------------------------------------------------------------------------
    /**
        Sets entity-open timestamp. Set milliseconds to zero to avoid
        problems with differences in time stamps. <P>
        @param value  entity-open timestamp
    **/
    //----------------------------------------------------------------------------
    public void setOpenTime(EYSDate value)
    {                                   // begin setOpenTime()
        openTime = value;
        if (openTime != null)
        {
            openTime.setMillisecond(0);
        }
    }                                   // end setOpenTime()

    //----------------------------------------------------------------------------
    /**
        Sets entity-open timestamp to current time. Set milliseconds to zero to avoid
        problems with differences in time stamps.<P>
    **/
    //----------------------------------------------------------------------------
    public void setOpenTime()
    {                                   // begin setOpenTime()
        openTime = new EYSDate();
        if (openTime != null)
        {
            openTime.setMillisecond(0);
        }
    }                                   // end setOpenTime()

    //----------------------------------------------------------------------------
    /**
        Retrieves entity-close timestamp. <P>
        @return entity-close timestamp
    **/
    //----------------------------------------------------------------------------
    public EYSDate getCloseTime()
    {                                   // begin getCloseTime()
        return(closeTime);
    }                                   // end getCloseTime()

    //----------------------------------------------------------------------------
    /**
        Sets entity-close timestamp.  Set milliseconds to zero to avoid
        problems with differences in time stamps.<P>
        @param value  entity-close timestamp
    **/
    //----------------------------------------------------------------------------
    public void setCloseTime(EYSDate value)
    {                                   // begin setCloseTime()
        closeTime = value;
        if (closeTime != null)
        {
            closeTime.setMillisecond(0);
        }
    }                                   // end setCloseTime()

    //----------------------------------------------------------------------------
    /**
        Sets entity-close timestamp to current time.  Set milliseconds to zero to avoid
        problems with differences in time stamps.<P>
    **/
    //----------------------------------------------------------------------------
    public void setCloseTime()
    {                                   // begin setCloseTime()
        closeTime = new EYSDate();
        closeTime.setMillisecond(0);
    }                                   // end setCloseTime()

    //----------------------------------------------------------------------------
    /**
        Retrieves business date (may not be same day as open time). <P>
        @return business date (may not be same day as open time)
    **/
    //----------------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {                                   // begin getBusinessDate()
        return(businessDate);
    }                                   // end getBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets business date (may not be same day as open time). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  business date (may not be same day as open time)
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDate(EYSDate value)
    {                                   // begin setBusinessDate()
        businessDate = value;
    }                                   // end setBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets business date to current date. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDate()
    {                                   // begin setBusinessDate()
        businessDate = new EYSDate(EYSDate.TYPE_DATE_ONLY);
    }                                   // end setBusinessDate()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = false;

        if (obj instanceof AbstractStatusEntity)
        {
            AbstractStatusEntity c = (AbstractStatusEntity) obj;          // downcast the input object
        
            // compare all the attributes of ReconcilableCount
            if (getStatus()         == c.getStatus() &&
                getPreviousStatus() == c.getPreviousStatus() &&
                Util.isObjectEqual(getSignOnOperator(), c.getSignOnOperator()) &&
                Util.isObjectEqual(getSignOffOperator(), c.getSignOffOperator()) &&
                Util.isObjectEqual(getLastStatusChangeTime(), c.getLastStatusChangeTime()) &&
                Util.isObjectEqual(getOpenTime(), c.getOpenTime()) &&
                Util.isObjectEqual(getCloseTime(), c.getCloseTime()) &&
                Util.isObjectEqual(getBusinessDate(), c.getBusinessDate()))
            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
        }
        return(isEqual);
    }                                   // end equals()

    //---------------------------------------------------------------------
    /**
        Returns status descriptor string, checking for invalid status value. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value status code
        @return String status descriptor
        @see AbstractStatusEntityIfc#STATUS_DESCRIPTORS
    **/
    //--------------------------------------------------------------------- 
    public String statusToString(int value)
    {                                   // begin statusToString()
        String strResult;
        try
        {
            strResult = AbstractStatusEntityIfc.STATUS_DESCRIPTORS[value];
        }
        // if not valid value, say unknown
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult = "Invalid value [" + value + "]";
        }
        return(strResult);
    }                                   // end statusToString()

    //---------------------------------------------------------------------
    /**
        Returns status descriptor string, checking for invalid status value. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return String status descriptor
        @see AbstractStatusEntityIfc#STATUS_DESCRIPTORS
    **/
    //--------------------------------------------------------------------- 
    public String statusToString()
    {                                   // begin statusToString()
        return(statusToString(status));
    }                                   // end statusToString()

    //---------------------------------------------------------------------
    /**
        Converts attributes to string value.<P>
        @return string representation of attributes
    **/
    //--------------------------------------------------------------------- 
    public String attributesToString()
    {                                   // begin attributesToString()
        String strResult = "\nSTATUS ATTRIBUTES:\n";
        // add attributes to string
        if (signOnOperator == null)
        {
            strResult += "signOnOperator:                         [null]\n";
        } 
        else
        {
            strResult += "SignOnOperator:\nSub" + signOnOperator.toString() + "\n";
        }
        if (signOffOperator == null)
        {
            strResult += "signOffOperator:                        [null]\n";
        } 
        else
        {
            strResult += "SignOffOperator:\nSub" + signOffOperator.toString() + "\n";
        }
        strResult += "status:                                 [" + statusToString() + "]\n"
                   + "previous status:                        [" + statusToString(previousStatus) + "]\n";
        if (lastStatusChangeTime == null)
        {
            strResult += "lastStatusChangeTime:                   [null]\n";
        } 
        else
        {
            strResult += "lastStatusChangeTime:                   [" + lastStatusChangeTime.toString() + "]\n";
        }
        if (openTime == null)
        {
            strResult += "openTime:                               [null]\n";
        } 
        else
        {
            strResult += "openTime:                               [" + openTime.toString() + "]\n";
        }
        if (closeTime == null)
        {
            strResult += "closeTime:                              [null]\n";
        } 
        else
        {
            strResult += "closeTime:                              [" + closeTime.toString() + "]\n";
        }
        if (businessDate == null)
        {
            strResult += "businessDate:                           [null]\n";
        } 
        else
        {
            strResult += "businessDate:                           [" + businessDate.toString() + "]\n";
        }
        
        return(strResult);
        
    }                                   // end attributesToString()

    //---------------------------------------------------------------------
    /**
        This method converts hard totals information to a comma delimited
        String. <P>
        @return String
    **/
    //--------------------------------------------------------------------- 
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendInt(status);
        builder.appendInt(previousStatus);

        if (lastStatusChangeTime == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            lastStatusChangeTime.getHardTotalsData(builder);
        }

        if (openTime == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            openTime.getHardTotalsData(builder);
        }

        if (closeTime == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            closeTime.getHardTotalsData(builder);
        }

        if (businessDate == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            businessDate.getHardTotalsData(builder);
        }
        
        if (signOnOperator == null)
            {
            builder.appendStringObject("null");
        }
        else
        {
            signOnOperator.getHardTotalsData(builder);
        }

        if (signOffOperator == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            signOffOperator.getHardTotalsData(builder);
        }
    }

    //---------------------------------------------------------------------
    /**
        This method populates this object from a comma delimited string.
        <P>
        @param String   String containing hard totals data.
    **/
    //--------------------------------------------------------------------- 
        public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        status               = builder.getIntField();
        previousStatus       = builder.getIntField();

        lastStatusChangeTime  = (EYSDate)builder.getFieldAsClass();
        if (lastStatusChangeTime != null)
        {
            lastStatusChangeTime.setHardTotalsData(builder);
        }

        openTime  = (EYSDate)builder.getFieldAsClass();
        if (openTime != null)
        {
            openTime.setHardTotalsData(builder);
        }

        closeTime  = (EYSDate)builder.getFieldAsClass();
        if (closeTime != null)
        {
            closeTime.setHardTotalsData(builder);
        }

        businessDate  = (EYSDate)builder.getFieldAsClass();
        if (businessDate != null)
        {
            businessDate.setHardTotalsData(builder);
        }

        // Get the Signon Operator
        signOnOperator       = (EmployeeIfc)builder.getFieldAsClass();
        if (signOnOperator != null)
        {
            signOnOperator.setHardTotalsData(builder);
        }
            
        // Get the Signoff Operator
        signOffOperator      = (EmployeeIfc)builder.getFieldAsClass();
        if (signOffOperator != null)
        {
            signOffOperator.setHardTotalsData(builder);
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

}                                      // end class AbstractStatusEntity


