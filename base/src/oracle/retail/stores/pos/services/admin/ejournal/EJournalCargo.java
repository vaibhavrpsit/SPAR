/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ejournal/EJournalCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:27:54 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:46 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:48:48  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:36:07  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 24 2003 06:32:08   rrn
 * Added businessDate.
 * Resolution for 3646: EJournal - default search date should be business date not system date
 * 
 *    Rev 1.1   Dec 17 2003 09:19:32   rrn
 * Added registerID.
 * Resolution for 3611: EJournal to database
 * 
 *    Rev 1.0   Aug 29 2003 15:52:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 18:07:16   pdd
 * Added Security Override.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.ejournal;

// Foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.journal.JournalSearchData;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.ui.beans.TransactionLookupBeanModel;

//------------------------------------------------------------------------------
/**
    Carries the data for the ejournal service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EJournalCargo extends UserAccessCargo
{
    protected TransactionLookupBeanModel model = null;
    protected String errorMsg = new String();
    protected boolean newSearch = false;
    // The current journalled transaction
    protected int transactionIndex = -1;
    // The number of journalled transactions
    protected int transactionCount = 0;
    // The data search object
    protected JournalSearchData data = null;
    // The text for each transaction
    protected String transactionStrings[] = new String[1];
    
    /** 
        Register id
    */
    protected String registerID = new String();
    
    /**
        Business date
    */
    protected EYSDate businessDate = DomainGateway.getFactory().getEYSDateInstance();

    
    public void setRegisterID(String regID)
    {
        registerID = regID;
    }
    
    public String getRegisterID()
    {
        return registerID;
    }
    
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }
    
    public EYSDate getBusinessDate()
    {
        return businessDate;
    }

    public void setBeanModel(TransactionLookupBeanModel m)
    {
        model = m;
    }

    public TransactionLookupBeanModel getBeanModel()
    {
        return(model);
    }

    public void setErrorMsg(String value)
    {
        errorMsg = value;
    }

    public String getErrorMsg()
    {
        return(errorMsg);
    }

    public void setNewSearch(boolean value)
    {
        newSearch = value;
    }

    public boolean getNewSearch()
    {
        return(newSearch);
    }

    //--------------------------------------------------------------------------
    /**
       Set the current transaction index.

       @param i int
    */
    //--------------------------------------------------------------------------
    public void setTransactionIndex(int i)
    {
        transactionIndex = i;
    }

    //--------------------------------------------------------------------------
    /**
       Get the current transaction index.

       @return int
    */
    //--------------------------------------------------------------------------
    public int getTransactionIndex()
    {
        return transactionIndex;
    }

    //--------------------------------------------------------------------------
    /**
       Set the current number of transactions.

       @param i int
    */
    //--------------------------------------------------------------------------
    public void setTransactionCount(int i)
    {
        transactionCount = i;
    }

    //--------------------------------------------------------------------------
    /**
       Get the current number of transactions.

       @return int
    */
    //--------------------------------------------------------------------------
    public int getTransactionCount()
    {
        return transactionCount;
    }

    //--------------------------------------------------------------------------
    /**
       Set the search data.

       @param jsd JournalSearchData
    */
    //--------------------------------------------------------------------------
    public void setData(JournalSearchData jsd)
    {
        data = jsd;
    }

    //--------------------------------------------------------------------------
    /**
       Get the search data.

       @return JournalSearchData
    */
    //--------------------------------------------------------------------------
    public JournalSearchData getData()
    {
        return data;
    }

    //--------------------------------------------------------------------------
    /**
       Set the transaction strings.

       @param strings String[]
    */
    //--------------------------------------------------------------------------
    public void setTransactionStrings(String[] strings)
    {
        transactionStrings = strings;
    }

    //--------------------------------------------------------------------------
    /**
       Get the transaction strings.

       @return String[] transaction strings
    */
    //--------------------------------------------------------------------------
    public String[] getTransactionStrings()
    {
        return transactionStrings;
    }

    //--------------------------------------------------------------------------
    /**
       Returns the E-Journal access function ID.

       @return int RoleFunctionIfc.ELECTRONIC_JOURNAL
    */
    //--------------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.ELECTRONIC_JOURNAL;
    }

    //--------------------------------------------------------------------------
    /**
        Create a SnapshotIfc which can subsequently be used to restore
            the cargo to its current state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The cargo is able to make a snapshot.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>A snapshot is returned which contains enough data to restore the
            cargo to its current state.
        </UL>
        @return an object which stores the current state of the cargo.
        @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
    */
    //--------------------------------------------------------------------------

    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }


    //--------------------------------------------------------------------------
    /**
        Reset the cargo data using the snapshot passed in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The snapshot represents the state of the cargo, possibly relative
        to the existing state of the cargo.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>The cargo state has been restored with the contents of the snapshot.
        </UL>
        @param snapshot is the SnapshotIfc which contains the desired state
            of the cargo.
        @exception ObjectRestoreException is thrown when the cargo cannot
            be restored with this snapshot
    */
    //--------------------------------------------------------------------------

    public void restoreSnapshot(SnapshotIfc snapshot)
        throws ObjectRestoreException
    {
    }

}
