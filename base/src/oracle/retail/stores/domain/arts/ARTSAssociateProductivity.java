/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSAssociateProductivity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   11 Apr 2002 18:11:46   jbp
 * Initial revision.
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;
import java.util.ArrayList;

import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields to interact with the
    JdbcSaveAssociateProductivity
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSAssociateProductivity implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -980585580530106768L;

    /**
        The store ID
    **/
    protected String storeID = null;
    protected String workstationID = null;
    protected EYSDate businessDate = null;
    protected String transactionSalesAssociateID = null;
    protected ArrayList lineItems = null;



    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  storeID             The store ID
        @param  reportingPeriods    The list of reporting periods
    **/
    //---------------------------------------------------------------------
    public ARTSAssociateProductivity()
    {
    }

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  storeID             The store ID
        @param  reportingPeriods    The list of reporting periods
    **/
    //---------------------------------------------------------------------
    public ARTSAssociateProductivity(String storeID,
                                     String workstationID,
                                     EYSDate businessDate,
                                     String transactionSalesAssociateID)
    {
        this.storeID = storeID;
        this.workstationID = workstationID;
        this.businessDate = businessDate;
        this.transactionSalesAssociateID = transactionSalesAssociateID;
    }

    //---------------------------------------------------------------------
    /**
        Returns the store id
        <p>
        @return  the store id
    **/
    //---------------------------------------------------------------------
    public String getStoreID()
    {
        return(storeID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the store id
        <p>
        @param  value   The store id
    **/
    //---------------------------------------------------------------------
    public void setStoreID(String value)
    {
        storeID = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves workstation identifier. <P>
        @return workstation identifier
    **/
    //----------------------------------------------------------------------------
    public String getWorkstationID()
    {
        return(workstationID);
    }

    //----------------------------------------------------------------------------
    /**
        Sets workstation identifier. <P>
        @param value  workstation identifier
    **/
    //----------------------------------------------------------------------------
    public void setWorkstationID(String value)
    {
        this.workstationID = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the businessDate
        <p>
        @return  the businessDate
    **/
    //---------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {
        return this.businessDate;
    }

    //---------------------------------------------------------------------
    /**
        Sets the businessDate
        <p>
        @param  value   businessDate
    **/
    //---------------------------------------------------------------------
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }

    //---------------------------------------------------------------------
    /**
        Returns the transactionSalesAssociateID
        <p>
        @return  the transactionSalesAssociateID
    **/
    //---------------------------------------------------------------------
    public String getTransactionSalesAssociateID()
    {
        return this.transactionSalesAssociateID;
    }

    //---------------------------------------------------------------------
    /**
        Sets the transactionSalesAssociateID
        <p>
        @param  value   transactionSalesAssociateID
    **/
    //---------------------------------------------------------------------
    public void setTransactionSalesAssociateID(String transactionSalesAssociateID)
    {
        this.transactionSalesAssociateID = transactionSalesAssociateID;
    }

    //---------------------------------------------------------------------
    /**
        Returns the lineItems
        <p>
        @return  the lineItems
    **/
    //---------------------------------------------------------------------
    public ArrayList getLineItems()
    {
        return this.lineItems;
    }

    //---------------------------------------------------------------------
    /**
        Sets the lineItems
        <p>
        @param  value   lineItems
    **/
    //---------------------------------------------------------------------
    public void setLineItems(ArrayList lineItems)
    {
        this.lineItems = lineItems;
    }
}
