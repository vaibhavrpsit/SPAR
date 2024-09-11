/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/tdo/MainTDO.java /main/28 2013/11/13 16:03:07 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   11/12/13 - Enhancements to support additional transaction
 *                         recovery
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   03/30/12 - convert Hashtables and Vectors to Maps and Lists
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    mchellap  08/30/11 - Removed parameter file backup and restore
 *                         funcationality
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    tzgarba   02/22/10 - Added support for starting the scheduled IDDI jobs
 *                         after the initial data sets are loaded
 *    sgu       02/12/10 - exteranlize initialization error
 *    abondala  01/03/10 - update header date
 *    cgreene   05/15/09 - generic performance improvements
 *    mchellap  12/23/08 - initApplication - uncommentted
 *                         initializeOfflineSupport(bus)
 *    vchengeg  12/22/08 - ej defect fixes
 *    vchengeg  12/17/08 - enabling offline support
 *    vchengeg  12/17/08 - ej defect fixes
 *    vchengeg  12/16/08 - ej defect fixes
 *    mdecama   11/04/08 - I18N - Eliminate the Loading and Caching of the
 *                         CodeListMap. CodeListMap is deprecated by
 *                         CodeListManager.
 * ===========================================================================
  $Log:
   19   360Commerce 1.18        11/22/2007 10:59:01 PM Naveen Ganesh   PSI Code
         checkin
   18   360Commerce 1.17        7/12/2007 5:46:55 PM   Alan N. Sinton  CR 27494
         Enhanced initialization failure conditions.
   17   360Commerce 1.16        7/10/2007 2:44:02 PM   Ranjan X Ojha   Changed
        Parameter IDDIOfflineSupport
   16   360Commerce 1.15        7/9/2007 3:07:51 PM    Anda D. Cadar   I18N
        changes for CR 27494: POS 1st initialization when Server is offline
   15   360Commerce 1.14        7/5/2007 2:26:55 PM    Alan N. Sinton  CR 25803
         - Corrected the date issue where opening the store for the first time
         (no store history) on the 1st of the month erroneously prompts to
        open the store on the last of the month.
   14   360Commerce 1.13        7/3/2007 2:03:25 PM    Alan N. Sinton  CR 27474
         - Read store information even if store history table is empty.
   13   360Commerce 1.12        6/5/2007 5:29:42 AM    Vikram Gopinath Calling
        initializeOfflineSupport from manager, also added parameter for
        checking for offline supoprt.
   12   360Commerce 1.11        6/1/2007 7:52:51 AM    Vikram Gopinath Added
        function offlineSupported to read IDDIOfflineSupport parameter from
        application.xml
   11   360Commerce 1.10        5/29/2007 5:13:25 AM   Manikandan Chellapan
        Code refactoring
   10   360Commerce 1.9         5/25/2007 3:15:18 PM   Michael P. Barnett
        Uncomment the creation of the offline database.
   9    360Commerce 1.8         5/25/2007 6:42:54 AM   Vikram Gopinath Changed
        function name from loadofflineschema to loadofflinedata
   8    360Commerce 1.7         5/24/2007 5:53:35 PM   Tony Zgarba     Added
        exception handling around the client scheduler factory load call to
        ensure the client starts even if the installer does not uncomment the
        scheduler's bean definition.
   7    360Commerce 1.6         5/23/2007 11:36:38 AM  Chengegowda Venkatesh
        Initialization Changes Updated
   6    360Commerce 1.5         5/18/2007 7:59:08 PM   Michael P. Barnett Added
         initialization of IDDI client scheduler bean.
   5    360Commerce 1.4         4/25/2007 11:50:33 AM  Rohit Sachdeva  26435:
        Adding functionality to use locale map loader for locale map settings
        as was done for WebApps. Conduit script is not used for default locale
         any longer.
   4    360Commerce 1.3         10/20/2006 11:46:14 AM Rohit Sachdeva  21237:
        Change Password Flow Updates
   3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse
   2    360Commerce 1.1         3/10/2005 10:23:25 AM  Robert Pearse
   1    360Commerce 1.0         2/11/2005 12:12:31 PM  Robert Pearse
  $
  Revision 1.19  2004/08/16 21:14:52  lzhao
  @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.

  Revision 1.18  2004/07/24 16:43:10  bwf
  @scr 1882 Log tenderlimits parameter missing.

  Revision 1.17  2004/07/24 03:31:34  blj
  @scr 5421 - unused imports

  Revision 1.16  2004/07/23 22:17:25  epd
  @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode

  Revision 1.15  2004/07/12 18:09:22  rsachdeva
  @scr 3976 Online Listener was not being set in the correct tdo (Installer 4690 issues)

  Revision 1.14  2004/07/01 21:27:13  dcobb
  @scr 3982 4690: register reboot resets parameter values

  Revision 1.13  2004/06/10 14:21:33  jdeleau
  @scr 2775 Use the new tax data for the tax flat files

  Revision 1.12  2004/06/03 15:36:28  bwf
  @scr 5368 Fixed erik's unused imports.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main.tdo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.DomainVersion;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.FoundationVersion;
import oracle.retail.stores.foundation.iddi.ifc.IDDIConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataManager;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.DataSetManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.BaseException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.PosVersion;
import oracle.retail.stores.pos.ado.register.RegisterException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.utility.tdo.TaxTDO;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.OnlineListener;
import oracle.retail.stores.pos.services.main.MainCargo;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;

/**
 * @author rwh
 */
public class MainTDO extends TDOAdapter implements MainTDOIfc
{

    /**
     * Number of seconds the Data Manager waits before attempting the next
     * transaction.
     */
    protected static final int TRANSACTION_INTERVAL = 5;

    /**
     * Tender Limits reference. Held here because it is used across methods;
     * ultimately being set in the register.
     */
    private TenderLimitsIfc limits;

    /** The logger to which log messages will be sent.  **/
    private static final Logger logger = Logger.getLogger(MainTDO.class);;

    /**
     * Default Constructor for MainTDO
     */
    public MainTDO()
    {
    }

    /**
     * Perform application initialization operations
     *
     * @param bus
     * @throws TDOException
     *             on error
     */
    public void initApplication(BusIfc bus) throws TDOException
    {
        getLogger().debug("MainTDO.initApplication() - start");

        logVersionInfo(bus);
        verifyParameters(bus);
        createRegister(bus);
        otherInit(bus);
        initializeOfflineSupport(bus);
        checkCurrencyInstantiation(bus);
        cancelInprocessTransactions(bus);
        getLogger().debug("MainTDO.initApplication() - end");

    }

    protected void cancelInprocessTransactions(BusIfc bus) throws TDOException 
    {
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("MainTDO.cancelInprocessTransactions");
            }
            TransactionUtilityManagerIfc tranMgr = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            tranMgr.cancelRegisterInprocessTransactions();
        }
        catch (Exception e)
        {
            logger.error("Failed to cancel inprocess transactions", e);
        }
    }

	/**
     * Log the version information for the relevant application libraries
     *
     * @param bus
     * @throws TDOException
     *             on error
     */
    protected void logVersionInfo(BusIfc bus) throws TDOException
    {
        String maximumTransactionSequenceNumber =
            DomainGateway.getProperty(
                RegisterIfc.MAXIMUM_TRANSACTION_SEQUENCE_NUMBER_PROPERTY,
                Integer.toString(
                    RegisterIfc.DEFAULT_MAXIMUM_TRANSACTION_SEQUENCE_NUMBER));

        if ((maximumTransactionSequenceNumber != null)
            && (maximumTransactionSequenceNumber.length()
                != TransactionID.getSequenceNumberLength()))
        {
            TDOException exception =
                new TDOException("The transaction sequence number length is invalid.");
            exception.setErrorTextResourceName("TransSeqNumberLengthError");
            exception.setErrorTextDefault("Trans Seq Num Length Error");
            throw exception;
        }

        String registerNumber =
            Gateway.getProperty("application", "WorkstationID", null);
        if (Util.isEmpty(registerNumber))
        {
            TDOException exception =
                new TDOException("The register could not be retrieved from properties.");
            exception.setErrorTextResourceName("RegisterParameterError");
            exception.setErrorTextDefault("Reg Param Error");
            throw exception;
        }

        if ((registerNumber != null)
            && (registerNumber.length()
                != TransactionID.getWorkstationIDLength()))
        {
            TDOException exception =
                new TDOException("The workstation ID length is invalid.");
            exception.setErrorTextResourceName("WorkstationIDLengthError");
            exception.setErrorTextDefault("WS ID Length Error");
            throw exception;
        }

        // Log the application has started and build numbers
        PosVersion posVersion = new PosVersion();
        FoundationVersion fndVersion = new FoundationVersion();
        DomainVersion domVersion = new DomainVersion();

        StringBuffer release = new StringBuffer();
        release
            .append(posVersion.getVersion())
            .append(":")
            .append(posVersion.getBuild())
            .append(" ")
            .append(domVersion.getVersion())
            .append(":")
            .append(domVersion.getBuild())
            .append(" ")
            .append(fndVersion.getVersion())
            .append(":")
            .append(fndVersion.getBuild());

        getLogger().debug(release);
    }

    /**
     * Obtain store from properties; then, obtain code list map (reason codes)
     *
     * @param bus
     * @throws TDOException
     *             on error
     * @deprecated as of 13.1 No Callers
     */
    protected void loadReasonCodes(BusIfc bus) throws TDOException
    {
        String storeID;

            storeID = Gateway.getProperty("application", "StoreID", "");

            if (Util.isEmpty(storeID))
            {
                TDOException exception =
                    new TDOException("The store number could not be retrieved from properties.");
                exception.setErrorTextResourceName("StoreStatusParameterError");
                exception.setErrorTextDefault("Store Param Error");
                throw exception;
            }

            if ((storeID != null) && (storeID.length() != TransactionID.getStoreIDLength()))
            {
                TDOException exception =
                    new TDOException("The store ID length is invalid.");
                exception.setErrorTextResourceName("StoreIDLengthError");
                exception.setErrorTextDefault("Store ID Length Error");
                throw exception;
            }
        }

    /**
     * Obtain Tender Limits
     *
     * @param bus
     * @throws TDOException
     *             on error
     */
    protected void verifyParameters(BusIfc bus) throws TDOException
    {
        // look up parameter that is present.
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        limits = DomainGateway.getFactory().getTenderLimitsInstance();

        //set tender limits in register (ADO) after the register is
        // established.
        String currentKey = null;
        try
        { // begin get tender limits try block
            int size = TenderLimitsIfc.TENDER_LIMIT_KEYS.length;

            for (int i = 0; i < size; i++)
            { // begin loop through tender limits keys
                currentKey = TenderLimitsIfc.TENDER_LIMIT_KEYS[i];
                String parm =
                    pm.getStringValue(currentKey);

                limits.setCurrencyLimit(currentKey, parm);
            } // end loop through tender limits keys
            size = TenderLimitsIfc.TENDER_PERCENT_LIMIT_KEYS.length;

            for (int i = 0; i < size; i++)
            { // begin loop through tender percent keys
                currentKey = TenderLimitsIfc.TENDER_PERCENT_LIMIT_KEYS[i];
                String parm =
                    pm.getStringValue(currentKey);
                limits.setPercentageLimit(currentKey, parm);
            } // end loop through tender percent keys
        } // end get tender limits try block
        catch (ParameterException e)
        {
            TDOException exception =
                new TDOException("The tender limits parameter data, " + currentKey + ", was not found.");
            exception.setErrorTextResourceName("TenderLimitsParameterError");
            exception.setErrorTextDefault("T.L. Param Error");
            throw exception;
        }
    }

    /**
     * Get register object from factory. Read Hard Totals.
     *
     * @param bus
     * @throws TDOException
     *             on error
     */
    protected void createRegister(BusIfc bus) throws TDOException
    {
        try
        {
            RegisterADO register =
                StoreFactory.getInstance().getRegisterADOInstance();
            StoreADO store = StoreFactory.getInstance().getStoreADOInstance();
            register.setStoreADO(store);
            readHardTotals(bus, register, store);
            register.setTenderLimits(limits);
            setupWorkstation(register, false);

            MainCargo cargo = (MainCargo) bus.getCargo();
            cargo.setRegisterADO(register);

            //dbRegisterCheck(bus, register); see LookupRegisterSite
            //dbStoreCheck(bus, register); see LookupStoreStatusSite
        }
        catch (TDOException e) // was Exception
        {
            TDOException exception =
                new TDOException("A RegisterException has occurred.");
            exception.setErrorTextResourceName(e.getErrorTextResourceName());
            exception.setErrorTextDefault(e.getErrorTextDefault());
            throw exception;
        }
    }

    /**
     * Get training register object from factory. Read Hard Totals.
     *
     * @param bus
     * @throws TDOException
     *             on error
     * @deprecated in 13.3. Training mode register is removed
     *  and actual register is used for training mode instead.
     */
    protected void createTrainingRegister(BusIfc bus) throws TDOException
    {
        // Enable Training Register, so the getRegisterADOInstance returns the training register
        RegisterADO register =
            StoreFactory.getInstance().getRegisterADOInstance();
        // Disable Training Register
        StoreADO store = StoreFactory.getInstance().getStoreADOInstance();
        register.setStoreADO(store);
        readHardTotals(bus, register, store);
        register.setTenderLimits(limits);
        setupWorkstation(register, true);

        MainCargo cargo = (MainCargo) bus.getCargo();
        cargo.setTrainingRegisterADO(register);

        ((RegisterIfc)register.toLegacy()).setOtherRegister(((RegisterIfc)cargo.getRegisterADO().toLegacy()));
        ((RegisterIfc)cargo.getRegisterADO().toLegacy()).setOtherRegister((RegisterIfc)register.toLegacy());
    }

    /**
     * Set timing for control of Queue Monitor
     *
     * @param bus
     * @throws TDOException
     *             on error
     */
    protected void setOfflineListener(BusIfc bus) throws TDOException
    {
        DataManagerIfc dm =
            (DataManagerIfc) bus.getManager(DataManagerIfc.TYPE);
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        OnlineListener ol = new OnlineListener(ui);
        dm.addOnlineListener(ol);
        if (dm instanceof DataManager)
        {
            ((DataManager) dm).setQueueMonitorInterval(TRANSACTION_INTERVAL);
        }
    }

    /**
     * Load the IDDI client scheduler factory bean. This bean is configured
     * through Spring in ServiceContext.xml. It will create scheduled jobs for
     * retrieving datasets from the store server. It should schedule
     * automatically, but for some reason the bean must be loaded in order for
     * the scheduling to occur.
     */
    public void loadIDDIClientSchedulerFactory()
    {
        try {
            BeanLocator.getServiceBean(IDDIConstantsIfc.CLIENT_SCHEDULER_FACTORY);
        } catch(Exception e) {
            logger.error("Failed to start the client scheduler factory.  No offline data updates will be available.", e);
        }
    }

    /**
     * Manage journal sequence number. Obtain tax rules.
     *
     * @param bus
     * @throws TDOException
     *             on error
     */
    protected void otherInit(BusIfc bus) throws TDOException
    {
        String storeID = null;
        TransactionIDIfc transactionID = null;

        // Journal the last Transaction
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (journal != null)
        {
            String sequenceNumber = journal.getSequenceNumber();
            if (sequenceNumber != null && sequenceNumber != "")
            {
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
                utility.indexTransactionInJournal(sequenceNumber);
            }
            else
            {
                // Initialize the journal transaction ID if it doesn't have
                // one.
                transactionID =
                    DomainGateway.getFactory().getTransactionIDInstance();

                journal.setSequenceNumber(
                    transactionID.getTransactionIDString());
            }
        }

        // Read tax rules from database and save on Utility Manager
        readAndConfigureTaxRules(bus, storeID);
    }

    /**
     * @param bus
     * @param storeID
     * @param utility
     * @throws TDOException
     */
    protected void readAndConfigureTaxRules(BusIfc bus, String storeID) throws TDOException
    {
        ////////// start Tax Rules
        // ////////////////////////////////////////////////
        MainCargo cargo = (MainCargo) bus.getCargo();
        RegisterADO vReg = cargo.getRegisterADO();
        StoreADO storeADO = vReg.getStoreADO();

        // Read local store tax rules and store them in the UtilityManager
        try
        {
            StoreStatusIfc storeStatus = (StoreStatusIfc)storeADO.toLegacy();
            StoreIfc si = storeStatus.getStore();
            String state = si.getAddress().getState();
            storeID = si.getStoreID();

            TaxTDO taxTDO = (TaxTDO)TDOFactory.create("tdo.utility.Tax");
            Map<Integer,List<TaxRuleIfc>> taxGroupTaxRulesMapping = taxTDO.createTaxGroupTaxRuleMappingForStore(storeID, state);
            // Save the tax group and tax rules mapping in utility.
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            if (utility != null)
            {
                utility.setTaxGroupTaxRulesMapping(storeID, taxGroupTaxRulesMapping);
            }
            else
            {
                getLogger().error("Error retrieving utility manager instance");
                throw new TDOException("The utility manager instance could not be obtained.");
            }
        }
        catch (TDOException tdoe)
        {
            getLogger().warn("Error intializing local store tax rules", tdoe);
            throw new TDOException(
                "Error initializing local store tax rules",
                tdoe);
        }
        catch (BaseException se)
        {
            if (storeID != null)
            {
                String errorText =
                    "MainTDO.otherInit() - Local store tax rule read failed";
                getLogger().warn(errorText, se);
            }
        }
    }

    /**
     * Miscellaneous operations needed for setup of WS; Logger and creation of
     * Workstation
     *
     * @param reg
     */
    protected void setupWorkstation(RegisterADO reg, boolean isTrainingMode)
    {
        StoreADO storeADO = reg.getStoreADO();
        RegisterIfc regLegacy = (RegisterIfc) reg.toLegacy();
        if (regLegacy != null)
        {
            EYSDomainIfc eysd = storeADO.toLegacy();
            StoreIfc si = ((StoreStatusIfc) eysd).getStore();

            Logger logger =
                Logger.getLogger(
                    oracle.retail.stores.pos.services.main.tdo.MainTDO.class);
            ;
            WorkstationIfc ws =
                AbstractFinancialCargo.createWorkstation(si, logger, isTrainingMode);
            regLegacy.setWorkstation(ws);
        }
        else
        {
            System.out.println(
                "MainTDO.setupWorkstation() - register legacy is null");
        }
    }

    /**
     * Reads the financial information from persistent storage and refreshes
     * the internal register state
     *
     * @exception RegisterException
     *                is thrown on error
     */
    public void readHardTotals(
        BusIfc bus,
        RegisterADO register,
        StoreADO store)
        throws TDOException
    {
        SessionBusIfc sessionBus = (SessionBusIfc) bus;

        Serializable data = null;
        boolean bOK = true;

        Logger logger =
            Logger.getLogger(
                oracle.retail.stores.pos.services.main.tdo.MainTDO.class);
        ;
        POSDeviceActions pda = new POSDeviceActions(sessionBus);
        HardTotalsIfc ht = null;

        // retrieve from hard totals
        try
        {
            data = register.getHardTotalsStream();

            if (data == null)
            {
                data = pda.readHardTotals();
            }

            HardTotalsBuilderIfc builder =
                DomainGateway.getFactory().getHardTotalsBuilderInstance();
            builder.setHardTotalsInput(data);

            ht = (HardTotalsIfc) builder.getFieldAsClass();

            ht.setHardTotalsData(builder);
            register.fromLegacy(ht.getRegister());
            store.fromLegacy(ht.getStoreStatus());
            /*******************************************************************
             * System.out.println( "last transaction seq. # is ==>" +
             * ht.getRegister().getLastTransactionSequenceNumber() **");
             ******************************************************************/
        }
        catch (DeviceException de)
        {
            // begin catch device exception
            bOK = false;
            logger.warn("Hard totals could not be read", de);
        } // end catch device exception
        catch (HardTotalsFormatException htfe)
        {
            bOK = false;
            logger.warn("Hard totals could not be read", htfe);
            //throw new RegisterException(htfe.toString(), htfe);
        }
        if (bOK == false)
        {
            boolean bOk2 = true;

            String storeID =
                Gateway.getProperty("application", "StoreID", null);
            if (storeID == null)
            {
                //System.out.println("store ID prop not found");
                logger.error("Store ID property not found.");
                // set error code as if data were not found
                bOk2 = false;
            }
            else
            {
                //System.out.println("store id read from config");
                logger.info(
                    "store identifier read from configuration: " + storeID);
            }

            // if no error, load store status in cargo
            if (bOk2)
            { // begin read store status
                // initialize store object based on store ID parameter
                StoreIfc store2 = DomainGateway.getFactory().getStoreInstance();
                store2.setStoreID(storeID);
                StoreStatusIfc storeStatus =
                    DomainGateway.getFactory().getStoreStatusInstance();
                storeStatus.setStore(store2);
                // add business date (set to yesterday with status open)
                // in case database read fails. operation will then proceed
                // on previous business date.
                EYSDate businessDate =
                    DomainGateway.getFactory().getEYSDateInstance();
                businessDate.initialize(EYSDate.TYPE_DATE_ONLY);
                // roll back one day, default business date will be today
                if(businessDate.getDay() == 1)
                {
                    businessDate.roll(Calendar.MONTH, false);
                    // if it's december, well roll back the year
                    if(businessDate.getMonth() == 12)
                    {
                        businessDate.roll(Calendar.YEAR, false);
                    }
                }
                businessDate.roll(Calendar.DATE, false);
                storeStatus.setBusinessDate(businessDate);
                storeStatus.setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);

                EYSDate eDate = storeStatus.getBusinessDate();
                if (eDate == null)
                {
                    throw new TDOException("No valid business date");
                }
                // if date in hard totals, we will use it to prime business
                // date
                logger.info("Business date initialized to "
                            + eDate.toFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)));

                //this.fromLegacy(ht.getRegister()); ---- Don't have a
                // register yet!!!
                store.fromLegacy(storeStatus);

                // No hard totals, so make up a Register
                RegisterIfc registerRDO = DomainGateway.getFactory().getRegisterInstance();
                register.fromLegacy(registerRDO);
            } // end read store status
        }
    }

    protected boolean offlineSupported(BusIfc bus)
    {
        boolean supported = false;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            Boolean value = pm.getBooleanValue(ParameterConstantsIfc.BASE_IDDIOfflineSupportRequired);
            supported = value.booleanValue();
        }
        catch (Exception e)
        {
            getLogger().warn("Error while getting IDDI_OFFLINE_SUPPORT_PARAMETER");
            supported = false;
        }
        return supported;
    }

    protected void initializeOfflineSupport(BusIfc bus) throws TDOException
    {
        // Following line to be uncommented for IDDI to load schema and seed
        // data in case local derby db does not exist
        DataSetManagerIfc dsman = (DataSetManagerIfc) bus.getManager(DataSetManagerIfc.TYPE);
        try
        {
            logger.info("Initializing offline support.");
            dsman.initializeOfflineSupport();
        }
        catch (Exception e)
        {
            if (offlineSupported(bus))
            {
                TDOException tdoEx = new TDOException("Error initializing offline data to start application.");
                tdoEx
                        .setErrorTextDefault("The register configuration parameter data was not found.Operations cannot proceed.");
                tdoEx.setErrorTextResourceName("DialogSpec.RegisterParameterError");
                throw tdoEx;
            }
        }

        try
        {
            // After all data sets are consumed, start the scheduler for future updates.
            logger.info("Starting IDDI consumer scheduler.");
            Scheduler scheduler =
                (Scheduler)BeanLocator.getServiceBean(IDDIConstantsIfc.CLIENT_SCHEDULER_FACTORY);
            scheduler.start();
        }
        catch (Exception e)
        {
            logger.error("Failed to start the IDDI client scheduler factory.  No offline data updates will be available.", e);
        }

    }

    /**
     * Tests if the application can instantiate an instance of CurrencyIfc.  If
     * not then throw a TDOException.
     *
     * @throws TDOException
     */
    protected void checkCurrencyInstantiation(BusIfc bus) throws TDOException
    {
        try
        {
            DomainGateway.getBaseCurrencyInstance("0");
        }
        catch(Exception e)
        {
            logger.fatal("Could not instantiate CurrencyIfc.", e);
            UtilityManagerIfc utilityMgr = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            TDOException tdoException = new TDOException("Error instantiating Currency instance.  Application cannot proceed");
            String arg = utilityMgr.retrieveDialogText("InitializationFailure.keyCurrencyData", "Currency data is missing.");
            tdoException.setErrorTextDefault(arg);
            tdoException.setErrorTextResourceName("InitializationFailure");
            throw tdoException;
        }
    }
}
