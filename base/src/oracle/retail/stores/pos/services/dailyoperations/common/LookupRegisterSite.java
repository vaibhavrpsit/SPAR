/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/LookupRegisterSite.java /main/15 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     07/02/14 - Removed methods deprecated in 13.3.
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    06/02/10 - removed the training mode increment id dependency
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         1/17/2008 12:10:18 AM  Manas Sahu      Event
 *       originator changes
 *  5    360Commerce 1.4         2/6/2007 2:47:58 PM    Edward B. Thorne Merge
 *       from LookupRegisterSite.java, Revision 1.2.1.0
 *  4    360Commerce 1.3         12/26/2006 3:18:21 PM  Charles D. Baker CR
 *       23955 - Updated the sequence number for store credit which was being
 *       lost when the application is stopped. This number is stored in the
 *       register
 *       object which isn't refreshed with each transaction. Corrected some
 *       other
 *       minor issues which had the potential to cause problems.
 *  3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
 * $
 * Revision 1.18  2004/09/03 19:57:39  kmcbride
 * @scr 7143: Setting register transaction sequence to value from database.
 *
 * Revision 1.17  2004/08/16 21:14:52  lzhao
 * @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.
 *
 * Revision 1.16  2004/07/23 22:17:26  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.15  2004/07/13 22:01:20  dcobb
 * @scr 1783 Cashier List is deleted from hard totals when exiting POS
 * Restore cashier list for current till.
 *
 * Revision 1.14  2004/07/06 03:50:07  crain
 * @scr 5822 Training mode is turning off when logging into POS
 *
 * Revision 1.13  2004/06/03 14:47:43  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.12  2004/04/20 13:13:09  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.11  2004/04/13 12:57:46  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.10  2004/04/09 19:31:44  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.9  2004/04/02 21:59:07  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.8  2004/04/01 16:04:10  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.7  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.6  2004/03/24 17:35:03  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.5 2004/03/14 21:24:26 tfritz @scr 3884 - New Training Mode
 * Functionality
 *
 * Revision 1.4 2004/03/10 18:44:08 pkillick @scr 3926- Within last conditional statement of Arrive(BusIfc bus) removed
 * the increment of variable databaseTransactionSequenceNumber. Line 326
 *
 * Revision 1.3 2004/02/12 16:49:36 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:40:02 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:15 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Jan 16 2004 10:17:32 cdb When running off line, the register business date is null? Need to prevent app
 * crash in this case. Resolution for 3727: Offline not working in Current Build (pturnk-70)
 *
 * Rev 1.1 Nov 05 2003 22:35:16 cdb Removed deprecation error Resolution for 3430: Sale Service Refactoring
 *
 * Rev 1.0 Aug 29 2003 15:56:20 CSchellenger Initial revision.
 *
 * Rev 1.7 Mar 10 2003 17:10:08 DCobb Renamed and moved methods from TillUtility to FinancialTotalsDataTransaction.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 * Rev 1.6 Feb 16 2003 10:43:24 mpm Merged 5.1 changes. Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 * Rev 1.5 Feb 12 2003 18:42:38 DCobb Moved getTillTotals to TillUtility.getTillsFromDatabase(register). Resolution for
 * POS SCR-1867: POS 6.0 Floating Till
 *
 * Rev 1.4 Jan 06 2003 12:10:36 DCobb Set till type in getTillTotals method. Resolution for POS SCR-1867: POS 6.0
 * Floating Till
 *
 * Rev 1.3 Dec 20 2002 11:32:04 DCobb Add floating till. Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 * Rev 1.2 Dec 06 2002 15:57:56 crain Changed "AAAA" to "0000" for default unique id Resolution for 1849: Store Credit
 * Receipt Number Includes Vowels in the Barcode when completing a Return Transaction.
 *
 * Rev 1.1 24 Jun 2002 11:45:18 jbp merge from 5.1 SCR 1726 Resolution for POS SCR-1726: Void - Void of new special
 * order gets stuck in the queue in DB2
 *
 * Rev 1.0 Apr 29 2002 15:31:34 msg Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.common.StoreStatusCargo;

/**
 * Query the database for store status.
 *
 * @version $Revision: /main/15 $
 */
public class LookupRegisterSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -8197131377695005320L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Send a store status lookup inquiry to the database manager.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter; // letter to mail to determine next destination

        // transaction sequence numbers from different sources
        int hardTotalsTransactionSequenceNumber = -1;
        // customer sequence numbers from different sources
        int hardTotalsCustomerSequenceNumber = -1;
        int databaseCustomerSequenceNumber = -1;
        int databaseTransactionSequenceNumber = -1;
        String hardTotalsUniqueIdentifier = "0000";
        String databaseUniqueIdentifier = "0000";
        DrawerIfc drawers[] = null;
        EmployeeIfc[] cashiers = null;
        String tillID = null;

        // ok indicator
        boolean bOk = true;

        // set default letter
        letter = new Letter(CommonLetterIfc.CONTINUE);

        // set transaction
        FinancialTotalsDataTransaction db = null;

        db = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        // get cargo
        StoreStatusCargo cargo = (StoreStatusCargo) bus.getCargo();

        // look up configuration setting
        boolean trainingModeOn = false;
        WorkstationIfc oldWS = null;

        RegisterIfc register = cargo.getRegister();
        if (register != null)
        {
            trainingModeOn = register.getWorkstation().isTrainingMode();
            oldWS = register.getWorkstation();
        }

        WorkstationIfc ws =
            AbstractFinancialCargo.createWorkstation(cargo.getStoreStatus().getStore(), logger, trainingModeOn);

        if (ws == null)
        {
            logger.error("Workstation configuration parameter not available.");
            letter = new Letter("ParameterError");

            // set error code as if data were not found
            cargo.setDataExceptionErrorCode(DataException.NO_DATA);
            bOk = false;
        }
        else
        {
            if (oldWS != null)
            {
                ws.setTransReentryMode(oldWS.isTransReentryMode());
            }
        }

        if (bOk)
        { // begin evaluate workstation IDs and read
            // set hard totals transaction sequence number
            if (register != null)
            {
                hardTotalsTransactionSequenceNumber = register.getLastTransactionSequenceNumber();
                hardTotalsCustomerSequenceNumber = register.getLastCustomerSequenceNumber();

                hardTotalsUniqueIdentifier = register.getCurrentUniqueID();
                drawers = register.getDrawers();

                tillID = register.getCurrentTillID();
                if (tillID != null)
                {
                    bOk = validateTillID(cargo, register);

                    if (bOk)
                    {
                        // The validateTillID() method may have change the value of the
                        // TillID
                        tillID = register.getCurrentTillID();
                    }
                    else
                    {
                        // This condition should only occur it one or more offline registers open the same till
                        // or open a till that is already opened on an online register.  The open till transaction of the
                        // last register to update the database ends up the "owner" of the till.  This is a 
                        // 'should not occur' condition.  
                        letter = new Letter("InvalidRegister");
                        logger.error("Till " +tillID +"is opened on another register.  This error will persist until the till is closed on the other register");
                    }
                }

                if (bOk)
                {                    
                    TillIfc currentTill = register.getTillByID(tillID);
                    if (currentTill != null)
                    {
                        cashiers = register.getTillByID(tillID).getCashiers();
                    }
                }

                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + hardTotalsUniqueIdentifier + "] will be used.");
            }    
        }
        if (bOk)
        {
            register = null;
            StoreStatusIfc ss = cargo.getStoreStatus();

            // attempt to do the database lookup
            try
            {
                // look up register status
                register = db.readRegisterStatus(ws);

                if (register == null)
                {
                    if (logger.isInfoEnabled())
                        logger.info("Can't find register in database");
                }
                else
                {
                    register.setWorkstation(ws);

                    // get register totals (may not find any if register is not open)
                    try
                    {
                        RegisterIfc[] registerArray = db.readRegisterTotals(register);
                        if (registerArray[0] != null)
                        {
                            register.setTotals(registerArray[0].getTotals());
                        }
                    }
                    catch (Exception e)
                    {
                        // do nothing. There may be no totals if register is closed
                    }

                    //get financial data for each Till in Register
                    db.readTillsWithTotals(register);

                    databaseUniqueIdentifier = register.getCurrentUniqueID();

                    // pull transaction sequence number
                    databaseTransactionSequenceNumber = register.getLastTransactionSequenceNumber();
                     // pull customer sequence number
                    databaseCustomerSequenceNumber = register.getLastCustomerSequenceNumber();
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Register read.");
                    }
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Unique identifier [" + databaseUniqueIdentifier + "] will be used.");
                    }
                }
            }
            // catch problems on the lookup
            catch (DataException e)
            {
                // regardless of error, we will dummy up a register object if
                // we don't have one in hard totals
                register = cargo.getRegister();
                if (register == null)
                { // begin create dummy register
                    logger.warn("Hard totals does not contain a valid register.");

                    register = AbstractFinancialCargo.createRegister(ws, ss.getBusinessDate());
                    register.setCurrentUniqueID("0000"); // start over
                    setRegisterDefaults(register, ws, ss);

                    cargo.setRegister(register);

                    // MPM todo: we need last transaction sequence number from hard totals,
                    // but if the register isn't defined in the database, what
                    // significance does the hard totals have?
                } // end create dummy register

                // set error code
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                logger.error("Register lookup error: \n" + e.toString() + "");
            }

            // display workstation ID
            if (register != null)
            {

                String registerBusinessDate = "null";
                // compare business dates
                if (register != null && register.getBusinessDate() != null)
                {
                    registerBusinessDate = register.getBusinessDate().toFormattedString("yyyy-MM-dd HH:mm:ss.SSS");
                }
                if (logger.isInfoEnabled())
                    logger.info(
                        "Store business date ["
                            + ss.getBusinessDate().toFormattedString("yyyy-MM-dd HH:mm:ss.SSS")
                            + "] register ["
                            + registerBusinessDate
                            + "]");

                // if business dates not equal, create set business date on register object
                if (register.getBusinessDate() == null || !register.getBusinessDate().equals(ss.getBusinessDate()))
                {
                    logger.warn(
                        ""
                            + "The business date in the store and the register do not match.  "
                            + ""
                            + "The application is setting the register's business date to match the store's."
                            + "");

                    //register = AbstractFinancialCargo.createRegister(ws, ss.getBusinessDate());
                    register.setBusinessDate(ss.getBusinessDate());
                }

                // set drawers as read from Hard Totals back into Register
                // if no drawer exists, then create drawer
                if (register.getDrawers().length == 0)
                {
                    if (drawers == null || drawers.length == 0)
                    {
                        // add drawer to register
                        DrawerIfc drawer = DomainGateway.getFactory().getDrawerInstance();
                        drawer.setDrawerID(DrawerIfc.DRAWER_PRIMARY);
                        drawer.setDrawerStatus(AbstractStatusEntityIfc.DRAWER_STATUS_UNOCCUPIED, "");
                        register.addDrawer(drawer);
                    }
                    else
                    {
                        for (int i = 0; i < drawers.length; ++i)
                        {
                            register.addDrawer(drawers[i]);
                        }
                    }

                    // update drawer status in database
                    try
                    {
                        db.updateDrawerStatus(register);
                    }
                    catch (DataException e)
                    {
                        if (logger.isInfoEnabled())
                            logger.info("Update of drawer status failed.");
                    }
                }

                // restore the cashiers list
                if ((tillID != null) && (tillID.equals(register.getCurrentTillID())))
                {
                    if (cashiers != null)
                    {
                        TillIfc till = register.getTillByID(tillID);
                        for (int i = 0; i < cashiers.length; i++)
                        {
                            till.addCashier(cashiers[i]);
                        }
                    }
                }

                // set the register in the cargo to the record that was found
                cargo.setRegister(register);
            }

            if (logger.isInfoEnabled())
                logger.info(
                    "Reconciling the transaction sequence number read from hard totals ["
                        + Integer.toString(hardTotalsTransactionSequenceNumber)
                        + "] and the database ["
                        + Integer.toString(databaseTransactionSequenceNumber)
                        + "] ...");

            // reconcile sequence numbers
            if (hardTotalsTransactionSequenceNumber > databaseTransactionSequenceNumber
                || databaseTransactionSequenceNumber == -1)
            { // begin reset sequence number to match hard totals
                if (logger.isInfoEnabled())
                    logger.info(
                        "The transaction sequence number read from hard totals ["
                            + Integer.toString(hardTotalsTransactionSequenceNumber)
                            + "] will be used.");
                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + hardTotalsUniqueIdentifier + "] will be used.");
                register = cargo.getRegister();
                register.setLastTransactionSequenceNumber(hardTotalsTransactionSequenceNumber);
                register.setCurrentUniqueID(hardTotalsUniqueIdentifier);
                try
                {
                    db.updateRegisterStatus(register);
                    db.updateDrawerStatus(register);
                }
                catch (DataException e)
                {
                    if (logger.isInfoEnabled())
                        logger.info("Update of transaction sequence number failed.");
                }
            } // end reset sequence number to match hard totals
            else if (
                databaseTransactionSequenceNumber > hardTotalsTransactionSequenceNumber
                    && hardTotalsTransactionSequenceNumber == -1)
            { // begin reset sequence number to match database
                if (logger.isInfoEnabled())
                    logger.info(
                        "The transaction sequence number read from the database ["
                            + Integer.toString(databaseTransactionSequenceNumber)
                            + "] will be used.");
                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + databaseUniqueIdentifier + "] will be used.");
                cargo.getRegister().setLastTransactionSequenceNumber(databaseTransactionSequenceNumber);
                cargo.getRegister().setCurrentUniqueID(databaseUniqueIdentifier);
            } // end reset sequence number to match hard totals
            else if (
                databaseTransactionSequenceNumber > hardTotalsTransactionSequenceNumber
                    && hardTotalsTransactionSequenceNumber != -1)
            { // begin reset sequence number to match database
                cargo.getRegister().setLastTransactionSequenceNumber(databaseTransactionSequenceNumber);
                cargo.getRegister().setCurrentUniqueID(databaseUniqueIdentifier);
                // Don't increment unique ID. THis has the potential to "skip" store credit
                // sequence numbers.
                //cargo.getRegister().getNextUniqueID(); // used to increment ID. Throw away result.

                if (logger.isInfoEnabled())
                    logger.info(
                        "The transaction sequence number read from the database ["
                            + Integer.toString(databaseTransactionSequenceNumber)
                            + "] will be used.");
                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + cargo.getRegister().getCurrentUniqueID() + "] will be used.");

            } // end reset sequence number to match hard totals

            if (logger.isInfoEnabled())
                logger.info(
                    "Reconciling the customer sequence number read from hard totals ["
                        + Integer.toString(hardTotalsCustomerSequenceNumber)
                        + "] and the database ["
                        + Integer.toString(databaseCustomerSequenceNumber)
                        + "] ...");

            // reconcile sequence numbers
            if (hardTotalsCustomerSequenceNumber > databaseCustomerSequenceNumber
                || databaseCustomerSequenceNumber == -1)
            { // begin reset customer sequence number to match hard totals
                if (logger.isInfoEnabled())
                    logger.info(
                        "The customer sequence number read from hard totals ["
                            + Integer.toString(hardTotalsCustomerSequenceNumber)
                            + "] will be used.");
                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + hardTotalsUniqueIdentifier + "] will be used.");
                register = cargo.getRegister();
                register.setLastCustomerSequenceNumber(hardTotalsCustomerSequenceNumber);
                register.setCurrentUniqueID(hardTotalsUniqueIdentifier);
                try
                {
                    db.updateRegisterStatus(register);
                    db.updateDrawerStatus(register);
                }
                catch (DataException e)
                {
                    if (logger.isInfoEnabled())
                        logger.info("Update of customer sequence number failed.");
                }
            } // end reset customer sequence number to match hard totals
            else if (
                databaseCustomerSequenceNumber > hardTotalsCustomerSequenceNumber
                    && hardTotalsCustomerSequenceNumber == -1)
            { // begin reset customer sequence number to match database
                if (logger.isInfoEnabled())
                    logger.info(
                        "The customer sequence number read from the database ["
                            + Integer.toString(databaseCustomerSequenceNumber)
                            + "] will be used.");
                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + databaseUniqueIdentifier + "] will be used.");
                cargo.getRegister().setLastCustomerSequenceNumber(databaseCustomerSequenceNumber);
                cargo.getRegister().setCurrentUniqueID(databaseUniqueIdentifier);
            } // end reset sequence number to match hard totals
            else if (
                databaseCustomerSequenceNumber > hardTotalsCustomerSequenceNumber
                    && hardTotalsCustomerSequenceNumber != -1)
            { // begin reset customer sequence number to match database
                cargo.getRegister().setLastCustomerSequenceNumber(databaseCustomerSequenceNumber);
                cargo.getRegister().setCurrentUniqueID(databaseUniqueIdentifier);
                // Don't increment unique ID. THis has the potential to "skip" store credit
                // sequence numbers.
                 if (logger.isInfoEnabled())
                    logger.info(
                        "The transaction sequence number read from the database ["
                            + Integer.toString(databaseCustomerSequenceNumber)
                            + "] will be used.");
                if (logger.isInfoEnabled())
                    logger.info("Unique identifier [" + cargo.getRegister().getCurrentUniqueID() + "] will be used.");

            } // end reset sequence number to match hard totals
            // end mahipal

            if (logger.isInfoEnabled())
                logger.info("Register set in cargo.");

        } // end evaluate workstation IDs and read

        EventOriginatorInfoBean.setEventOriginator("LookupRegisterSite.arrive");
        bus.mail(letter, BusIfc.CURRENT);
        
    } // end arrive()

        
    /**
     * Check to see if some other register has opened its till with the same ID
     * as the current till. This is an edge case that only occurs when one or more
     * of the registers opens in off-line mode.
     * 
     * @param cargo
     * @param register
     * @return true if the workstationID in the database matches the current workstationID
     * 
     * @since 14.1
     */
    protected boolean validateTillID(StoreStatusCargo cargo, RegisterIfc register)
    {
        boolean tillOk = true;

        try
        {
            FinancialTotalsDataTransaction db = (FinancialTotalsDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            TillIfc till = db.readTillStatus(register.getWorkstation().getStore(), register.getCurrentTillID());

            // The workstation IDs match, then check to see if the status is open.
            if (!till.getRegisterID().equals(register.getWorkstation().getWorkstationID())
                    && !register.getWorkstation().isTrainingMode())
            {
                // If the till is open, both registers are using the same till which is an error condition. 
                // If the till status is closed set the till id to null so that the register can open a new till.
                if (till.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
                {
                    cargo.setInvalidWorkstationID(till.getRegisterID());
                    tillOk = false;
                }
                else
                {
                    register.setCurrentTillID(null);
                }
            }
        }
        catch (DataException e)
        {
            // There is an error retrieving the till status, since this is an edge condition, log it and go on.
            logger.warn("Unable to verify that the till belongs to register "
                    + register.getWorkstation().getWorkstationID() + ".", e);
        }
        return tillOk;
    }
    
    /**
     * Sets default values for register created when hard totals doesn't provide a valid register number. This is added
     * here for extensibility.
     *
     * @param register
     *            register object
     * @param ws
     *            workstation object
     * @param ss
     *            store status object
     */
    protected void setRegisterDefaults(RegisterIfc register, WorkstationIfc ws, StoreStatusIfc ss)
    {
    }

}
