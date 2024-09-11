/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/StoreDirectoryDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      5    360Commerce 1.4         11/22/2007 11:09:32 PM Naveen Ganesh   PSI
           Code checkin
      4    360Commerce 1.3         5/10/2006 9:48:24 PM   Brett J. Larsen CR
           17307 - remove inventory
      3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:25:32 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:14:27 PM  Robert Pearse   
     $
     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:49  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:34:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:42:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:32   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 04 2002 15:20:50   cdb
 * Added data operation for retrieving list of all stores from the database. May need to add additional fields at some point if this  transaction ends up used elsewhere.
 * Resolution for Backoffice SCR-446: Update Inventory service based on new requirements.
 * 
 *    Rev 1.0   Sep 20 2001 15:59:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:16   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

//--------------------------------------------------------------------------
/**
    The DataTransaction to perform store directory operations.<P>
        Other operations on financial store data are located in
        StoreDataTransaction.
        @see oracle.retail.stores.domain.arts.StoreDataTransaction
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class StoreDirectoryDataTransaction extends DataTransaction
{                                                                               // begin class StoreDirectoryDataTransaction
    private static final long serialVersionUID = -5316326595348001834L;

    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.StoreDirectoryDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName = "StoreDirectoryDataTransaction";
    /**
       An ARTSStore constructed from a store and business day.
    **/
    protected ARTSStore artsStore = null;

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public StoreDirectoryDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //---------------------------------------------------------------------
    public StoreDirectoryDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Searches a store directory for stores based on city, state, zip. <P>
     * @param store StoreIfc reference
     * @return array of StoreIfc objects
     * @throws DataException
     * DataException when an error occurs
     * @see oracle.retail.stores.domain.arts.JdbcSearchStoreDirectoryTest
     * @see oracle.retail.stores.domain.arts.JdbcSearchStoreDirectory
     * @deprecated As of release 13.1 use  {@link #searchStoreDirectory(StoreIfc, LocaleRequestor)}
     */
    public StoreIfc[] searchStoreDirectory(StoreIfc store) throws DataException
    {
        logger.debug( "StoreDirectoryDataTransaction.searchStoreDirectory");

        // use ARTSStore object to hold key
        artsStore = new ARTSStore(store,
                                  (EYSDate) null);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("SearchStoreDirectory");
        da.setDataObject(artsStore);
        dataActions[0] = da;
        setDataActions(dataActions);
        StoreIfc[] retrievedStores = (StoreIfc[]) getDataManager().execute(this);

        logger.debug( "StoreDirectoryDataTransaction.searchStoreDirectory");

        return(retrievedStores);
    }

    /**
     * Searches a store directory for stores based on city, state, zip. <P>
     * @param store StoreIfc reference
     * @return array of StoreIfc objects
     * @throws DataException
     * DataException when an error occurs
     * @see oracle.retail.stores.domain.arts.JdbcSearchStoreDirectoryTest
     * @see oracle.retail.stores.domain.arts.JdbcSearchStoreDirectory
     */
    public StoreIfc[] searchStoreDirectory(StoreIfc store, LocaleRequestor localeRequestor) throws DataException
    {
        logger.debug( "StoreDirectoryDataTransaction.searchStoreDirectory");

        // use ARTSStore object to hold key
        artsStore = new ARTSStore(store, (EYSDate) null);
        
        //prepare StoreSearchCriteria
        StoreSearchCriteriaIfc criteria = DomainGateway.getFactory().getStoreSearchCriteriaInstance();
        criteria.setLocaleRequestor(localeRequestor);
        criteria.setARTSStore(artsStore);
        
        //prepare dataAction
        DataAction da = new DataAction();
        da.setDataOperationName("SearchStoreDirectory");
        da.setDataObject(criteria);

        //set data action and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;
        setDataActions(dataActions);
        StoreIfc[] retrievedStores = (StoreIfc[]) getDataManager().execute(this);

        logger.debug( "StoreDirectoryDataTransaction.searchStoreDirectory");

        return retrievedStores;
    }
    
    /**
     * Searches for all stores based stored in the database. <P>
     * @param localeRequestor the requested locales
     * @return array of StoreIfc objects
     * @throws DataException when an error occurs
     * @deprecated As of release 13.1 use  {@link #retrieveAllStores(LocaleRequestor)}
     */
    public StoreIfc[] retrieveAllStores()
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "StoreDirectoryDataTransaction.retrieveAllStores");

        // use ARTSStore object to hold key
        //artsStore = new ARTSStore(store,
        //                          (EYSDate) null);


        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("RetrieveAllStores");
        dataActions[0] = da;
        setDataActions(dataActions);
        StoreIfc[] retrievedStores = (StoreIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "StoreDirectoryDataTransaction.retrieveAllStores");

        return(retrievedStores);

    }
    
    /**
     * Searches for all stores based stored in the database. <P>
     * @param localeRequestor the requested locales
     * @return array of StoreIfc objects
     * @throws DataException when an error occurs
     */
    public StoreIfc[] retrieveAllStores(LocaleRequestor localeRequestor)
        throws DataException
    {
        logger.debug( "StoreDirectoryDataTransaction.retrieveAllStores");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("RetrieveAllStores");
        da.setDataObject(localeRequestor);
        dataActions[0] = da;
        setDataActions(dataActions);
        StoreIfc[] retrievedStores = (StoreIfc[]) getDataManager().execute(this);

        logger.debug( "StoreDirectoryDataTransaction.retrieveAllStores");

        return(retrievedStores);

    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuffer strResult =
            new StringBuffer("Class:  StoreDirectoryDataTransaction (Revision ");
        strResult.append(getRevisionNumber()).append(") @")
            .append(hashCode()).append("\n");
        if (artsStore == null)
        {
            strResult.append("artsStore:                          [null]\n");
        }
        else
        {
            strResult.append(artsStore.toString()).append("\n");
        }
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

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
    }                                   // end getRevisionNumber()

}                                                                               // end class StoreDirectoryDataTransaction
