/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/FinancialTotalsDataTransaction.java /main/21 2013/10/17 15:50:17 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 10/17/13 - associate productivity should data even if employee is
 *                      not active.
 *    abonda 09/04/13 - initialize collections
 *    jswan  01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *    npoola 06/02/10 - removed the training mode increment id dependency
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    ohorne 03/13/09 - added support for localized department names
 *    mahisi 11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    ddbake 10/24/08 - Updates due to merge
 *    ohorne 10/23/08 - removed deprecated (as of 5.1.0) method
 *                      readEmployeeTotals()
 *
 * ===========================================================================

     $Log:
      8    360Commerce 1.7         3/25/2008 3:46:51 AM   Vikram Gopinath CR
           #29644 ported code from v12x. Updated the catch block of
           readAssociateProductivity() method. No need to set null when
           exception is encountered, the flow should just proceed to retrieve
           the next associate details. Modified method
           readAssociateProductivity() by adding a try/catch block to set the
           associate to null.
      7    360Commerce 1.6         3/4/2008 5:21:27 AM    Manas Sahu
           Changed the readAssociateProductivity function to handle sale
           association id that are not valid.
      6    360Commerce 1.5         2/6/2007 11:05:54 AM   Anil Bondalapati
           Merge from FinancialTotalsDataTransaction.java, Revision 1.3.1.0 
      5    360Commerce 1.4         12/8/2006 5:01:14 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      4    360Commerce 1.3         7/21/2006 2:27:37 PM   Brendan W. Farrell
           Merge fixes from v7.x.  These changes let services extend tax.
      3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:21:41 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:11:05 PM  Robert Pearse   
     $
     Revision 1.9  2004/06/03 14:47:36  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.8  2004/04/20 13:02:36  tmorris
     @scr 4332 -Sorted imports

     Revision 1.7  2004/04/13 01:26:43  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:39  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:50  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:30:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   May 15 2003 16:42:48   cdb
 * Added filtering of workstation classification when requested.
 * Resolution for 1930: RE-FACTORING AND FEATURE ENHANCEMENTS TO PARAMETER SUBSYSTEM
 * 
 *    Rev 1.3   May 05 2003 14:34:40   RSachdeva
 * Till Id Comparison Check
 * Resolution for POS SCR-2271: POS is crashed at till reconcile in below case.
 * 
 *    Rev 1.2   Mar 10 2003 17:11:20   DCobb
 * Renamed and moved methods from TillUtility to FinancialTotalsDataTransaction.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Dec 20 2002 11:15:26   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Jun 03 2002 16:34:50   msg
 * Initial revision.
 *
 *    Rev 1.2   09 Apr 2002 17:01:18   jbp
 * modified design for associate productivity report
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 *    Rev 1.1   Mar 18 2002 22:45:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:02   msg
 * Initial revision.
 *
 *    Rev 1.13   28 Feb 2002 14:40:14   mia
 * added functionality for new implementation of Backoffice Register Open and Close using RegisterStatusBeans instead of proper Register objects.
 * Resolution for Backoffice SCR-658: Refactor DailyOps Open/Close Register
 *
 *    Rev 1.12   28 Feb 2002 14:09:50   adc
 * Changed the signature for readDrawerStatus method
 * Resolution for Backoffice SCR-670: Integration test-not able to Open till from Back Office/n-tier
 *
 *    Rev 1.11   Feb 22 2002 12:15:56   dfh
 * updates for assoc prod report when return against special order complete
 * Resolution for POS SCR-1281: Special Order complete does not update sales tax count/amount on Summary report
 *
 *    Rev 1.10   Feb 16 2002 16:46:02   dfh
 * updates to include layaway completes in assoc prod report
 * Resolution for POS SCR-1299: Completed Layaway Pickup does not update Net Item Sales count/amount on Summary Report
 *
 *    Rev 1.9   Feb 11 2002 22:44:40   dfh
 * cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.8   Feb 10 2002 21:53:38   dfh
 * updates to try to include orders in summary reports
 * Resolution for POS SCR-1225: Summary Report requirements for Special Order has changed - vers 9
 *
 *    Rev 1.7   30 Jan 2002 17:14:40   mpb
 * Added method to read workstation IDs by store number.
 * Resolution for Backoffice SCR-150: ParameterManagement
 *
 *    Rev 1.6   29 Jan 2002 09:51:06   epd
 * Deprecated all methods using accumulate and added new methods without accum parameter (default behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.5   04 Jan 2002 15:58:28   epd
 * added method that added a few other methods as data actions to consolidate operations
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.4   13 Dec 2001 16:57:28   mia
 * Added readAllStoreRegisters
 * Resolution for Backoffice SCR-115: DailyOps- Register
 *
 *    Rev 1.3   12 Dec 2001 16:37:06   epd
 * removed automatic update of drawer status when updating register
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   05 Dec 2001 10:07:44   epd
 * update register now saves drawer status
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   05 Dec 2001 08:45:48   epd
 * Added code to retrieve and update drawer status
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 20 2001 15:57:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:56   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.AssociateProductivityIfc;
import oracle.retail.stores.domain.financial.DepartmentActivityIfc;
import oracle.retail.stores.domain.financial.EmployeeActivityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.financial.TimeIntervalActivityIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteria;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

//-------------------------------------------------------------------------
/**
    The DataTransaction to perform persistent operations on
    Financial Totals
    <P>
    @version $Revision: /main/21 $
**/
//-------------------------------------------------------------------------
public class FinancialTotalsDataTransaction extends DataTransaction
                                         implements AccumulatorTransactionIfc
{

    private static final long serialVersionUID = -4259428914321209601L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction.class);
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/21 $";

    /** The name that links this transaction to a command within DataScript. **/
    public static String dataCommandName = "FinancialTotalsDataTransaction";
    /** The name that links this transaction to a command within DataScript. **/
    public static String dataSaveName    = "SaveFinancialTotalsDataTransaction";
    /** The name that writes data directly to the DB even when most
        transactions are being queued. **/
    public static String notQueuedSaveName = "NotQueuedFinancialTotalsDataTransaction";
    /** Action name constant used to read all registers in a store. */
    public static final String READ_STORE_REGISTERS     = "ReadStoreRegisters";
    /** Action name constant used to read a till status. */
    public static final String READ_TILL_STATUS         = "ReadTillStatus";
    /** Action name constant used to read a till totals. */
    public static final String READ_TILL_TOTALS         = "ReadTillTotals";
    /** Action name constant used to read a reporting period. */
    public static final String READ_REPORTING_PERIOD    = "ReadReportingPeriod";
    /** Action name constant used to read department totals. */
    public static final String READ_DEPARTMENT_TOTALS   = "ReadDepartmentTotals";
    /** Action name constant used to read time interval totals. */
    public static final String READ_TIME_INTERVAL_TOTALS = "ReadTimeIntervalTotals";
    
    /** 
     * Action name constant used to read transactions for employee info.
     * @deprecated As of release 13.1 JdbcReadTransactions.java is deprecated.
     *  */
    public static final String READ_TRANSACTIONS        = "ReadTransactions";
    
    /** Action name constant used to create till status. */
    public static final String CREATE_TILL_STATUS       = "CreateTillStatus";
    /** Action name constant used to update till status. */
    public static final String UPDATE_TILL_STATUS       = "UpdateTillStatus";
    /** Action name constant used to read register status. */
    public static final String READ_ALL_STORE_REGISTER_STATUS     = "ReadAllStoreRegisterStatus";
    /** Action name constant used to read register status. */
    public static final String READ_ALL_STORE_REGISTER_SUMMARY_STATUS     = "ReadAllStoreRegisterSummaryStatus";
    /** Action name constant used to read register status. */
    public static final String READ_REGISTER_STATUS     = "ReadRegisterStatus";
    /** Action name constant used to read register status. */
    public static final String READ_REGISTER_TOTALS     = "ReadRegisterTotals";
    /** Action name constant used to read register status. */
    public static final String CREATE_REGISTER_STATUS   = "CreateRegisterStatus";
    /** Action name constant used to read register status. */
    public static final String UPDATE_REGISTER_STATUS   = "UpdateRegisterStatus";
    /** Action name constant used to read register status. */
    public static final String UPDATE_REGISTER_TOTALS   = "UpdateRegisterTotals";
    /** Action name constant used to read drawer status. */
    public static final String READ_DRAWER_STATUS       = "ReadDrawerStatus";
    /** Action name constant used to update drawer status. */
    public static final String UPDATE_DRAWER_STATUS     = "UpdateDrawerStatus";
    /** Action name constant used to read workstation IDs by store ID. */
    public static final String READ_WORKSTATION_IDS_BY_STORE = "ReadWorkstationIDsByStore";
    /** Action name constant used to read all tills in a store. */
    public static final String READ_STORE_TILLS         = "ReadStoreTills";
    /** Action name constant used to read tax history. */
    public static final String READ_TAX_HISTORY         = "ReadTaxHistory";
    
    // See AccumulatorTransactionIfc for shared Action name constants.

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
     **/
    //---------------------------------------------------------------------
    public FinancialTotalsDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
        Class constructor.
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsDataTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of registers for the given store on the given
        business day.
        <P>
        @param  store       The store
        @param  businessDate The business day
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc[] readStoreRegisters(StoreIfc store, EYSDate businessDate)
                                            throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readStoreRegisters");

        // Build data action.
        ARTSStore aStore = new ARTSStore(store, businessDate);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aStore, READ_STORE_REGISTERS);
        setDataActions(dataActions);

        // Execute Data Action.
        RegisterIfc[] registers = (RegisterIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "FinancialTotalsDataTransaction.readStoreRegisters");

        return(registers);
    }

    //---------------------------------------------------------------------
    /**
        Returns the register for the given workstation.  This includes
        both the status and financial totals.
        <P>
        @param  workstation The workstation
        @return The register information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void closeRegister(RegisterIfc register)
                              throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "FinancialTotalsDataTransaction.closeRegister");

        // Update the register information
        updateRegisterStatus(register);

        register.resetTotals();
        addRegisterTotals(register);

        if (logger.isDebugEnabled()) logger.debug( "FinancialTotalsDataTransaction.closeRegister");
    }

    //---------------------------------------------------------------------
    /**
        Returns the register for the given workstation.  This includes
        both the status and financial totals.
        <P>
        @param  workstation The workstation
        @return The register information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc readRegister(WorkstationIfc workstation)
                                    throws DataException
    {
        RegisterIfc register = readRegisterStatus(workstation);

        if (!workstation.isTrainingMode())
        {
            /*
             * Only read financial totals if not in training mode
             */
            RegisterIfc[] registers = readRegisterTotals(register);

            /*
             * return only the most current register
             */
            register = registers[0];
        }

        return(register);
    }

    //---------------------------------------------------------------------
    /**
        Returns the register information for the given workstation on the
        given business date.
        <P>
        @param  storeID         The store ID
        @param  workstationID   The workstation ID.
        @param  businessDate    The business date
        @return The register information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc[] readRegister(String storeID,
                                      String workstationID,
                                      EYSDate businessDate)
                                      throws DataException
    {
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);

        WorkstationIfc workstation =
          DomainGateway.getFactory().getWorkstationInstance();
        workstation.setWorkstationID(workstationID);
        workstation.setStore(store);
        workstation.setTrainingMode(false);

        RegisterIfc register =
          DomainGateway.getFactory().getRegisterInstance();
        register.setWorkstation(workstation);
        register.setBusinessDate(businessDate);

        return(readRegisterTotals(register));
    }

    //---------------------------------------------------------------------
    /**
        Returns only the register status information for the given
        workstation.  No financial totals are included.
        <P>
        @param  workstation The workstation
        @return The register information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc[] readAllStoreRegisterStatus(Properties properties)
                                          throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readAllStoreRegistersStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(properties, READ_ALL_STORE_REGISTER_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        RegisterIfc[] registers = (RegisterIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "FinancialTotalsDataTransaction.readAllStoreRegistersStatus");

        return(registers);
    }

    //---------------------------------------------------------------------
    /**
        Returns only the register status information for a RegisterStatusBean
        for a given workstation.  No financial totals are included.
        <P>
        @param  workstation The workstation
        @return The register information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc[] readAllStoreRegisterSummaryStatus(Properties properties)
                                          throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readAllStoreRegistersSummaryStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(properties, READ_ALL_STORE_REGISTER_SUMMARY_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        RegisterIfc[] registers = (RegisterIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "FinancialTotalsDataTransaction.readAllStoreRegistersSummaryStatus");

        return(registers);
    }


    //---------------------------------------------------------------------
    /**
        Returns only the register status information for the given
        workstation.  No financial totals are included.
        <P>
        @param  workstation The workstation
        @return The register information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc readRegisterStatus(WorkstationIfc workstation)
                                          throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readRegisterStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(workstation, READ_REGISTER_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        RegisterIfc register = (RegisterIfc)getDataManager().execute(this);

        // get statuses for Drawer(s)
        register = readDrawerStatus(register);

        if (logger.isDebugEnabled()) logger.debug(
                    "FinancialTotalsDataTransaction.readRegisterStatus");

        return(register);
    }

    //---------------------------------------------------------------------
    /**
        Reads workstation IDs by store.
    **/
    //---------------------------------------------------------------------
    public Vector readWorkstationIDsByStore(String storeID)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readWorkstationIDsByStore");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(storeID, READ_WORKSTATION_IDS_BY_STORE);
        setDataActions(dataActions);

        // Execute data action.
        Vector workstationIDs = (Vector)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readWorkstationIDsByStore");

        return workstationIDs;
    }

    //---------------------------------------------------------------------
    /**
        Reads workstation IDs by store.
    **/
    //---------------------------------------------------------------------
    public Vector readWorkstationIDsByStore(String storeID, String workstationClass)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readWorkstationIDsByStore");

        WorkstationIfc workstation = DomainGateway.getFactory().getWorkstationInstance();
        StoreIfc       store       = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        workstation.setStore(store);
        workstation.setWorkstationClassification(workstationClass);

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(workstation, READ_WORKSTATION_IDS_BY_STORE);
        setDataActions(dataActions);

        // Execute data action.
        Vector workstationIDs = (Vector)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readWorkstationIDsByStore");

        return workstationIDs;
    }

    //---------------------------------------------------------------------
    /**
        Gets Drawer status for a given register and drawer ID.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc readDrawerStatus(RegisterIfc register)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readDrawerStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(register, READ_DRAWER_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        register = (RegisterIfc)getDataManager().execute(this);


        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readDrawerStatus");
        return register;
    }

    //---------------------------------------------------------------------
    /**
        Sets Drawer status for a given register and drawer ID.
    **/
    //---------------------------------------------------------------------
    public void updateDrawerStatus(RegisterIfc register)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateDrawerStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(register, UPDATE_DRAWER_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateDrawerStatus");
    }
    //---------------------------------------------------------------------
    /**
        Returns the register financial totals for the given register.
        <P>
        @param  register    The register
        @return The list of registers for the business day
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc[] readRegisterTotals(RegisterIfc register)
                                            throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readRegisterTotals");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(register, READ_REGISTER_TOTALS);
        setDataActions(dataActions);

        // Execute the data action.
        RegisterIfc[] registers = (RegisterIfc[])getDataManager().execute(this);
        
        //for each register object in the array get the tax history container and set it in the register
        for(int i = 0; i < registers.length; i++)
        {
            getTaxHistory(registers[i], TaxHistorySelectionCriteria.SEARCH_BY_WORKSTATION);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "FinancialTotalsDataTransaction.readRegisterTotals");

        return(registers);
    }

    /**
     * This class gets the tax history 
     * @param register
     * @param searchType
     * @throws DataException
     */
    private void getTaxHistory(RegisterIfc register, int searchType) throws DataException
    {
        DataActionIfc[] dataActions = new DataActionIfc[1];
        TaxHistorySelectionCriteriaIfc criteria = DomainGateway.getFactory().getTaxHistorySelectionCriteriaInstance();
        criteria.setStoreId(register.getWorkstation().getStoreID());
        criteria.setWorkstationId(register.getWorkstation().getWorkstationID());
        criteria.setBusinessDate(register.getBusinessDate());
        criteria.setCriteriaType(searchType);
        dataActions[0] = createDataAction(criteria, READ_TAX_HISTORY);
        setDataActions(dataActions);
        
        // Execute the data action.
        register.getTotals().setTaxes((TaxTotalsContainerIfc) getDataManager().execute(this));      
        
    }
    
    /**
     * This class gets the tax history tills.
     * @param till
     * @param storeID
     * @param businessDate
     * @throws DataException
     */
    private void getTaxHistoryTills(TillIfc till, String storeID, EYSDate businessDate) throws DataException
    {
        DataActionIfc[] dataActions = new DataActionIfc[1];
        // Make a call to the tax service
        TaxHistorySelectionCriteriaIfc criteria = DomainGateway.getFactory().getTaxHistorySelectionCriteriaInstance();

        criteria.setTillId(till.getTillID());
        criteria.setStoreId(storeID);
        criteria.setBusinessDate(businessDate);
        criteria.setCriteriaType(TaxHistorySelectionCriteria.SEARCH_BY_TILL);
        dataActions[0] = createDataAction(criteria, READ_TAX_HISTORY);
        setDataActions(dataActions);
        
       
        // Execute the data action.
        till.getTotals().setTaxes((TaxTotalsContainerIfc) getDataManager().execute(this));      
        
    }
    
    //---------------------------------------------------------------------
    /**
        Creates the register.  This method is used when opening a register
        for the first time on a given business day.  It creates both the
        status information and the financial totals for the register.
        <P>
        @param  register       The register
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void createRegister(RegisterIfc register) throws DataException
    {
        createRegisterStatus(register);
    }

    //---------------------------------------------------------------------
    /**
        Creates the register status.  This method is used when opening
        a register for the first time on a given business day.
        <P>
        @param  register       The register
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void createRegisterStatus(RegisterIfc register) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.createRegisterStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(register, CREATE_REGISTER_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "FinancialTotalsDataTransaction.createRegisterStatus");
    }

    //---------------------------------------------------------------------
    /**
        Updates the register.  This method updates both the status and
        the financial totals of an existing register.
        <P>
        @param  register       The register
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateRegister(RegisterIfc register) throws DataException
    {
        updateRegisterStatus(register);
        updateRegisterTotals(register);
    }

    //---------------------------------------------------------------------
    /**
        Updates the register status.  This method is used when changing
        the status of an existing register.
        <P>
        @param  register       The register
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateRegisterStatus(RegisterIfc register) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateRegisterStatus");

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(register, UPDATE_REGISTER_STATUS);
        setDataActions(dataActions);

        // exectute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateRegisterStatus");
    }

    //---------------------------------------------------------------------
    /**
        Updates the register totals.
        <P>
        @param  register       The register
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateRegisterTotals(RegisterIfc register) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateRegisterTotals");

        if (register.getWorkstation().isTrainingMode())
        {
            /*
             * Don't save financial totals if workstation is in training mode
             */
            return;
        }

        // Build data action.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(register, UPDATE_REGISTER_TOTALS);
        setDataActions(dataActions);

        // Execute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateRegisterTotals");
    }

    //---------------------------------------------------------------------
    /**
        Adds the register totals.  This method is used when closing
        an existing register.
        <P>
        @param  register       The register
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void addRegisterTotals(RegisterIfc register) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "FinancialTotalsDataTransaction.addRegisterTotals");

        if (register.getWorkstation().isTrainingMode())
        {
            /*
             * Don't save financial totals if workstation is in training mode
             */
            return;
        }

        DataAction dataAction = new DataAction();
        dataAction.setDataObject(register);
        dataAction.setDataOperationName(ADD_REGISTER_TOTALS);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "FinancialTotalsDataTransaction.addRegisterTotals");
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of reconciled tills for the given till and business
        day.
        <P>
        @param  storeID         The store ID
        @param  tillID          The ID of the till
        @param  businessDate    The business day
        @return The till information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TillIfc[] readTill(String storeID, String tillID, EYSDate businessDate)
                              throws DataException
    {
        TillIfc[] tills = readTillTotals(storeID, tillID, businessDate);
        return(tills);
    }

    //---------------------------------------------------------------------
    /**
        Returns the till status.
        <P>
        @param  store           The store
        @param  tillID          The ID of the till
        @return The till status information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TillIfc readTillStatus(StoreIfc store, String tillID)
                                  throws DataException
    {
        // Build the data action
        ARTSTill aTill = new ARTSTill(tillID, store.getStoreID());
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aTill, READ_TILL_STATUS);
        setDataActions(dataActions);

        // execute the data action
        TillIfc till = (TillIfc)getDataManager().execute(this);

        return(till);
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of reconciled tills for the given till and business
        day.
        <P>
        @param  till        The Till
        @return The list of reconciled tills.
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TillIfc[] readTillTotals(String storeID, String tillID, EYSDate businessDate)
                                    throws DataException
    {
        // Build the data action
        ARTSTill aTill = new ARTSTill(tillID, storeID);
        aTill.setBusinessDate(businessDate);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aTill, READ_TILL_TOTALS);
        setDataActions(dataActions);

        // Execute the data action
        TillIfc[] tills = (TillIfc[])getDataManager().execute(this);

        for(int i = 0; i < tills.length; i++)
        {
            getTaxHistoryTills(tills[i],storeID,businessDate);
        }
        
        return(tills);
    }

    //---------------------------------------------------------------------
    /**
        Returns the till with status and totals.
        <P>
        @param  store           The store
        @param  tillID          The ID of the till
        @param  businessDate    The business day
        @return The till with updated status and totals information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TillIfc readTillWithTotals(StoreIfc store, String tillID, EYSDate businessDate)
                                      throws DataException
    {
        TillIfc returnTill = null;

        if (store != null && tillID != null)
        {
            returnTill = readTillStatus(store, tillID);
            String storeID = store.getStoreID();
            if (returnTill != null)
            {
                TillIfc[] resultTills = readTillTotals(storeID, tillID, businessDate);
                for (int i = 0; i < resultTills.length; i++)
                {
                    if (resultTills[i].getTillID().equalsIgnoreCase(tillID))
                    {
                        EmployeeIfc signOnOp = returnTill.getSignOnOperator();
                        EmployeeIfc signOffOp = returnTill.getSignOffOperator();
                        int tillStatus = returnTill.getStatus();
                        int accountability = returnTill.getRegisterAccountability();
                        int tillType = returnTill.getTillType();
                        returnTill = resultTills[i];
                        returnTill.setSignOnOperator(signOnOp);
                        returnTill.addCashier(signOnOp);
                        returnTill.setSignOffOperator(signOffOp);
                        returnTill.setStatus(tillStatus);
                        returnTill.setRegisterAccountability(accountability);
                        returnTill.setTillType(tillType);
                        break;
                    }
                }
            }
        }
        return returnTill;
    }

    //---------------------------------------------------------------------
    /**
        Reads the register tills with status and totals and adds them to
        the register.
        <P>
        @param  register        The register
        @return The updated till array with status and totals information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TillIfc[] readTillsWithTotals(RegisterIfc register)
                                           throws DataException
    {
        TillIfc[] tillArray = register.getTills();
        StoreIfc store = register.getWorkstation().getStore();
        EYSDate businessDate = register.getBusinessDate();
        int tillCount = tillArray.length;

        register.setFloatingTill(false);
        for (int i=0; i<tillCount; i++)
        {
            tillArray[i] = readTillWithTotals(store, tillArray[i].getTillID(), businessDate);
            if (tillArray[i].getTillType() == AbstractStatusEntityIfc.TILL_TYPE_FLOATING)
            {
                register.setFloatingTill(true);
            }
        }
        register.setTills(tillArray);
        return tillArray;
    }

    //---------------------------------------------------------------------
    /**
        Reads the register and the register tills with status and totals
        and adds them to the register.
        <P>
        @param  register        The register
        @return The register with updated tills with status and totals
        information
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public RegisterIfc readRegisterWithTillTotals(RegisterIfc register)
                                                  throws DataException
    {
        register = readRegister(register.getWorkstation());

        //get financial data for each Till in Register
        readTillsWithTotals(register);

        return register;
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of tills for the given store on the given
        business day.
        <P>
        @param  store       The store
        @param  businessDate The business day
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TillIfc[] readStoreTills(StoreIfc store, EYSDate businessDate)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.readStoreTills");

        // Build data action.
        ARTSStore aStore = new ARTSStore(store, businessDate);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aStore, READ_STORE_TILLS);
        setDataActions(dataActions);

        // Execute Data Action.
        TillIfc[] tills = (TillIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( "FinancialTotalsDataTransaction.readStoreTills");

        return(tills);
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of reporting periods for the time interval.
        <P>
        @param  startDate   The beginning of the time interval.
        @param  endDate     The end of the time interval.
        @return The list of reporting periods.
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public ReportingPeriodIfc[] readReportingPeriod(EYSDate startDate,
                                                    EYSDate endDate)
                                                    throws DataException
    {
        // Build the data action
        ARTSReportingPeriod aPeriod = new ARTSReportingPeriod(startDate, endDate);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aPeriod, READ_REPORTING_PERIOD);
        setDataActions(dataActions);

        // execute the data action
        return((ReportingPeriodIfc[])getDataManager().execute(this));
    }

    /**
     * Returns a list of department totals for the time interval.
     * @param storeID The store ID
     * @param startDate The beginning of the time interval.
     * @param endDate The end of the time interval.
     * @param locales The locales of the department names
     * @return The list of totals by department.
     * @throws DataException DataException when an error occurs.
     * @deprecated As of Release 13.1 use {@link #readDepartmentTotals(String, EYSDate, EYSDate, LocaleRequestor)}
     */
    public DepartmentActivityIfc[] readDepartmentTotals(String storeID,
                                                        EYSDate startDate,
                                                        EYSDate endDate)
                                                        throws DataException
    {
    	return readDepartmentTotals(storeID, startDate, endDate, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }
    
    /**
     * Returns a list of department totals for the time interval.
     * @param storeID The store ID
     * @param startDate The beginning of the time interval.
     * @param endDate The end of the time interval.
     * @param locales The locales of the department names
     * @return The list of totals by department.
     * @throws DataException DataException when an error occurs.
     */
    public DepartmentActivityIfc[] readDepartmentTotals(String storeID,
                                                        EYSDate startDate,
                                                        EYSDate endDate,
                                                        LocaleRequestor locales)
                                                        throws DataException
    {
        // Build the data action
        ARTSActivity aActivity = new ARTSActivity(storeID, readReportingPeriod(startDate, endDate), locales);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aActivity, READ_DEPARTMENT_TOTALS);
        setDataActions(dataActions);

        // Execute the data action
        DepartmentActivityIfc[] activities = (DepartmentActivityIfc[]) getDataManager().execute(this);

        // Combine all department records that have the same department ID.
        Hashtable <String,DepartmentActivityIfc>  activityByDept = new Hashtable<String,DepartmentActivityIfc>(1);
        DepartmentActivityIfc da = null;
        DepartmentIfc dept = null;
        String key = null;

        // Loop through the activities and group them by department
        for (int i = 0; i < activities.length; i++)
        {
            da = activities[i];
            dept = da.getDepartment();
            key = dept.getDepartmentID();

            // If we've encountered this department before
            if (activityByDept.containsKey(key))
            {
                // add the totals
                ((DepartmentActivityIfc) activityByDept.get(key)).getTotals().add(da.getTotals());
            }
            else
            {
                // add the activity to the table
                activityByDept.put(key, da);
            }
        }

        Enumeration<DepartmentActivityIfc> e = activityByDept.elements();
        
        int i = 0;
        DepartmentActivityIfc[] combinedActivities = new DepartmentActivityIfc[activityByDept.size()];

        while (e.hasMoreElements())
        {
            combinedActivities[i++] = (DepartmentActivityIfc) e.nextElement();
        }
        return combinedActivities;
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of Associate totals for the time interval.
        @param  storeID     The store ID
        @param  startDate   The beginning of the time interval.
        @param  endDate     The end of the time interval.
        @return The list of totals by employee by day.
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public AssociateProductivityIfc[] readAssociateProductivity(String storeID,
                                                    EYSDate startDate,
                                                    EYSDate endDate)
                                                    throws DataException
    {
        DataActionIfc[] dataActions = new DataActionIfc[1];

        // build search criteria for Associate Prouctivity querry
        AssociateProductivitySearchCriteria searchCriteria =
            new AssociateProductivitySearchCriteria(storeID,startDate,endDate);

        // create Data Action
        dataActions[0] = createDataAction(searchCriteria,"ReadAssociateTotals");
        setDataActions(dataActions);
        EmployeeIfc emp = null;

        // read Associate Productivity table
        AssociateProductivityIfc[] results = (AssociateProductivityIfc[])getDataManager().execute(this);

        // read employees from employee ID
        for(int i=0; i<results.length; i++)
        {
            try 
            {
            	EmployeeTransaction eTransaction = null;
	            eTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
	            eTransaction.setQueryType(QueryTypeIfc.NUMBER);
	            eTransaction.setRetreiveAllEmployees(true);
	            dataActions[0] = createDataAction(results[i].getAssociate().getEmployeeID(), "employeelookup");
	            eTransaction.setDataActions(dataActions);
	            emp = (EmployeeIfc)getDataManager().execute(eTransaction);
	            results[i].setAssociate(emp);
            }
            catch (DataException e)
            {
            }
        }

        return results;
    }

    //---------------------------------------------------------------------
    /**
        Finds the employee in the hashtable.
        <P>
        @param  table      A hashtable of employees
        @param  employee   The employee being searched for.
    **/
    //---------------------------------------------------------------------
    protected EmployeeActivityIfc findEmployee(Hashtable table, EmployeeIfc employee)
    {
        EmployeeActivityIfc activity = null;
        String employeeID = employee.getEmployeeID();

        if (table.containsKey(employeeID))
        {
            /*
             * Employee Activity already exists in table
             */
            activity = (EmployeeActivityIfc)table.get(employeeID);
        }
        else
        {
            /*
             * Add a new Employee Activity to the table
             */
            activity = DomainGateway.getFactory().getEmployeeActivityInstance();
            activity.setEmployee(employee);
            activity.setTotals(DomainGateway.getFactory().getFinancialTotalsInstance());
            table.put(employeeID, activity);
        }

        return(activity);
    }

    //---------------------------------------------------------------------
    /**
        Returns a list of time interval totals for the time interval(s).
        <P>
        @param  storeID     The store ID
        @param  startDate   The beginning of the time interval.
        @param  endDate     The end of the time interval.
        @return The list of totals by time intervals.
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public TimeIntervalActivityIfc[] readTimeIntervalTotals(String storeID,
                                                            EYSDate startDate,
                                                            EYSDate endDate)
                                                            throws DataException
    {
        // Build data action.
        ARTSReportingPeriod aPeriod = new ARTSReportingPeriod(startDate, endDate);
        aPeriod.setStoreID(storeID);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aPeriod, READ_TIME_INTERVAL_TOTALS);
        setDataActions(dataActions);

        // Execute the data action.
        return((TimeIntervalActivityIfc[])getDataManager().execute(this));
    }

    //---------------------------------------------------------------------
    /**
        Creates the till status and financial totals.  This method is used
        when opening a till that has been reconciled or is opened on a
        different workstation.
        <P>
        @param  till        The till to create
        @param  register    The register associated with the till
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void createTill(TillIfc till, RegisterIfc register)
                           throws DataException
    {
        createTillStatus(till, register);
        saveTillTotals(till, register, CREATE_TILL_TOTALS);
    }

    //---------------------------------------------------------------------
    /**
        Creates the till status.  This method is used when opening a till
        that has been reconciled or is opened on a different workstation.
        <P>
        @param  till        The till to save
        @param  register    The register associated with the till
        @exception  DataException when an error occurs.
        @deprecated as of release 13.1, Use {@link #createTill(TillIfc, RegisterIfc)}
    **/
    //---------------------------------------------------------------------
    public void createTillStatus(TillIfc till, RegisterIfc register)
                                 throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.createTillStatus");

        // Build data action.
        ARTSTill aTill = new ARTSTill(till, register);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aTill, CREATE_TILL_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.createTillStatus");
    }

    //---------------------------------------------------------------------
    /**
        Creates the till financial totals.  This method is used when
        opening a till that has been reconciled or is opened on a
        different workstation.
        <P>
        @param  till        The till to save
        @param  register    The register associated with the till
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void saveTillTotals(TillIfc till, RegisterIfc register, String actionName)
                                 throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.saveTillTotals");

        // Create a list to store multiple actions
        ArrayList actions = new ArrayList();

        // Create the action to update the till.
        ARTSTill aTill = new ARTSTill(till, register);
        actions.add(createDataAction(aTill, actionName));

        // Create the Update Register Totals data action
        actions.add(createDataAction(register, ADD_REGISTER_TOTALS));

        // Create the Update Store Totals data action
        ARTSStore aStore = new ARTSStore(register.getWorkstation().getStore(),
                                    register.getBusinessDate());
        aStore.setFinancialTotals(register.getTotals());
        actions.add(createDataAction(aStore, ADD_STORE_TOTALS));

        // create array and copy list of actions to it.
        DataActionIfc[] dataActions = new DataActionIfc[actions.size()];
        actions.toArray(dataActions);
        setDataActions(dataActions);

        // Execute the data actions.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.saveTillTotals");
    }

    //---------------------------------------------------------------------
    /**
        Updates the status and financial totals of the till.  This method
        is used when changing an existing till.
        <P>
        @param  till        The till to save
        @param  register    The register associated with the till
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateTill(TillIfc till, RegisterIfc register)
                           throws DataException
    {
        updateTillStatus(till, register);
        saveTillTotals(till, register, UPDATE_TILL_TOTALS);
    }

    //---------------------------------------------------------------------
    /**
        Updates the status of the till.  This method is used when changing
        the status of an existing till.
        <P>
        @param  till        The till to save
        @param  register    The register associated with the till
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateTillStatus(TillIfc till, RegisterIfc register)
                                 throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateTillStatus");

        // Build data action.
        ARTSTill aTill = new ARTSTill(till, register);
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(aTill, UPDATE_TILL_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateTillStatus");
    }

    //---------------------------------------------------------------------
    /**
        Updates the status of the till, register and drawer when the till is
        closed.        <P>
        @param  till        The till to save
        @param  register    The register associated with the till
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateTillCloseStatuses(TillIfc till, RegisterIfc register)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateTillCloseStatuses");

        // Build data action.
        ARTSTill aTill = new ARTSTill(till, register);
        DataActionIfc[] dataActions = new DataActionIfc[3];
        dataActions[0] = createDataAction(aTill, UPDATE_TILL_STATUS);
        dataActions[1] = createDataAction(register, UPDATE_REGISTER_STATUS);
        dataActions[2] = createDataAction(register, UPDATE_DRAWER_STATUS);
        setDataActions(dataActions);

        // Execute data action.
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                     "FinancialTotalsDataTransaction.updateTillCloseStatuses");
    }
    //---------------------------------------------------------------------
    /**
        Gets last trainging sequence number of customer
        closed.        <P>
        @param  register    The register associated with the till
       @exception  DataException when an error occurs.
       @deprecated as of 13.3 use non-training mode sequence instead       
    **/
    //---------------------------------------------------------------------
    public int getLastTrainingCustomerSequenceNumber(RegisterIfc register) throws DataException
    {
        if ( logger.isDebugEnabled() ) 
        {
            logger.debug("CustomerReadDataTransaction.getLastTrainingCustomerSequenceNumber");
        }
        
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("GetLastTrainingCustomerSequenceNumber");
        dataAction.setDataObject(register);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        Integer seqNumber = (Integer) getDataManager().execute(this);

        return(seqNumber.intValue());
    }

    //---------------------------------------------------------------------
    /**
        Creates a data transaction.
        @param object the serialized object to be used in the data operation.
        @param name the name of the data action and operation.
        @return the new data action.
    **/
    //---------------------------------------------------------------------
    protected DataAction createDataAction(Serializable object, String name)
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(object);
        dataAction.setDataOperationName(name);
        return dataAction;
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number of this class.
        <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of this object.
       <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: FinancialTotalsDataTransaction (Revision "
                                        + getRevisionNumber() + ") @"
                                        + hashCode());
        return(strResult);
    }
}
