/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/register/VirtualRegisterADO.java /main/12 2011/12/05 12:16:16 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.register;

import java.io.Serializable;
import java.util.Calendar;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.employee.EmployeeADOIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.store.StoreADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.Register;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.device.POSDeviceActions;

/**
 * VirtualRegisterADO provides an implementation of VirtualRegisterADOIfc for
 * the base 360 POS product
 */
public class VirtualRegisterADO extends ADO implements VirtualRegisterADOIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2648090461168664638L;

    /** Reference to the associated store */
    protected StoreADOIfc store;

    /** Reference to the RegisterJournal */
    protected RegisterJournalIfc journal;

    /** Reference to the rdo register */
    protected RegisterIfc rdoRegister;

    /** Reference to the current operator */
    protected EmployeeADOIfc operator;

    /** Reference to the Tender Limits */
    protected TenderLimitsIfc tenderLimits;

    /**
     * Identifier of last reprintable transaction
     */
    protected String lastReprintableTransactionID = "";

    /**
     *  our logger
     **/
    protected transient Logger logger = Logger.getLogger(VirtualRegisterADO.class);

    /**
     * Constructor for VirtualRegisterADO
     * 
     * @param store
     *            associated store
     * @param journal
     *            assoicated journal
     */
    public VirtualRegisterADO(StoreADOIfc store, RegisterJournalIfc journal)
    {
        super();
        this.store = store;
        this.journal = journal;
    }

    /*
     * Returns the underlying rdo RegisterIfc @return RegisterIfc
     */
    public EYSDomainIfc toLegacy()
    {
        return rdoRegister;
    }

    /**
     * If an ADO is composed of multiple RDO types, it may make sense to
     * request the particular type that we need.
     * 
     * @param type
     *            The RDO object corresponding to the desired type.
     * @return an RDO.
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }

    /*
     * Sets the reference rdo RegisterIfc to the param @param rdo - reference
     * to RegisterIfc
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        rdoRegister = (RegisterIfc) rdo;
    }

    /**
     * Returns a reference to the associated store
     * 
     * @return reference to store
     */
    public StoreADOIfc getStore()
    {
        return this.store;
    }

    /**
     * Returns a reference to the associated journal
     * 
     * @return journal
     */
    public RegisterJournalIfc getJournal()
    {
        return this.journal;
    }

    /**
     * Sets the current operator
     * 
     * @param operator
     *            current operator, can be null
     */
    public void setOperator(EmployeeADOIfc operator)
    {
        this.operator = operator;
    }

    /**
     * Returns a reference to the current operator
     */
    public EmployeeADOIfc getOperator()
    {
        return operator;
    }

    /**
     * Saves the financial information to persistent storage. This class stores
     * the information to the hard totals device
     * 
     * @exception RegisterException
     *                thrown on error
     */
    public void saveFinancials() throws RegisterException
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Saving financials...");   
        }
        
        // Do not write hard totals if the register is in training mode
        if (!rdoRegister.getWorkstation().isTrainingMode())
        {    
            POSDeviceActions pda =
                new POSDeviceActions((SessionBusIfc) getContext().getBus());
    
            // set up hard totals
            HardTotalsIfc ht = DomainGateway.getFactory().getHardTotalsInstance();
            ht.setStoreStatus((StoreStatusIfc) store.toLegacy());
            ht.setRegister(rdoRegister);
            ht.setLastUpdate();
    
            // write out hard totals
            try
            {
                HardTotalsBuilderIfc builder =
                    DomainGateway.getFactory().getHardTotalsBuilderInstance();
                ht.getHardTotalsData(builder);
                pda.writeHardTotals(builder.getHardTotalsOutput());
            }
            catch (Exception e)
            { // begin catch device exception
                //System.out.println("Error writing hard totals");
                RegisterException exception =
                    new RegisterException("Error writing hard totals", e);
                exception.setErrorTextResourceName("WriteHardTotalsError");
                exception.setErrorTextDefault("H.T. Wrt. Fail");
                throw exception;
                //throw new RegisterException("VirtualRegisterADO.saveFinancials",
                // e);
            }
        }
    }

    /**
     * Factory method for creating transactions based on the specified
     * prototype
     * 
     * @param tranType
     *            specifies the type of the transaction
     * @return reference to created transaction
     */
    public RetailTransactionADOIfc createTransaction(TransactionPrototypeEnum tranType)
        throws RegisterException
    {
        return null;
    }

    /**
     * Reads the financial information from persistent storage and refreshes
     * the internal register state
     * 
     * @exception RegisterException
     *                is thrown on error
     */
    public void readFinancials() throws RegisterException
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Reading financials...");   
        }

        //////////////////////////// new blj //////////////////
        SessionBusIfc theBus = ((SessionBusIfc) getContext().getBus());
        AbstractFinancialCargo cargo =
            (AbstractFinancialCargo) theBus.getCargo();
        ////////////////////////////////////////////////////////
        boolean bOK = true;

// KLM: Adding this declaration as an instance
// for use by other methods
/*        Logger logger =
            Logger.getLogger(
                oracle.retail.stores.pos.ado.register.VirtualRegisterADO.class);
*/

        POSDeviceActions pda =
            new POSDeviceActions((SessionBusIfc) getContext().getBus());
        HardTotalsIfc ht = null;

        // retrieve from hard totals
        try
        {
            Serializable data = pda.readHardTotals();
            HardTotalsBuilderIfc builder =
                DomainGateway.getFactory().getHardTotalsBuilderInstance();
            builder.setHardTotalsInput(data);

            ht = (HardTotalsIfc) builder.getFieldAsClass();

            ht.setHardTotalsData(builder);
            this.fromLegacy(ht.getRegister());
            store.fromLegacy(ht.getStoreStatus());

            //////////////////////////// new blj //////////////////
            cargo.setStoreStatus(ht.getStoreStatus());
            cargo.setRegister(ht.getRegister());
            ////////////////////////////////////////////////////////
        }
        catch (DeviceException de)
        { // begin catch device exception
            //System.out.println("caught DeviceException trying hard totals");
            bOK = false;
            logger.warn(de);
        } // end catch device exception
        catch (HardTotalsFormatException htfe)
        {
            //System.out.println("caught HardTotalsFormatException trying hard
            // totals");
            bOK = false;
            logger.warn(htfe);
            //throw new RegisterException(htfe.toString(), htfe);
        }
        if (bOK == false)
        {
            boolean bOk2 = true;

            String storeID =
                Gateway.getProperty("application", "StoreID", null);
            if (storeID == null)
            {
//                System.out.println("store ID prop not found");
                logger.error("StoreID property not found in configuration.");
                // set error code as if data were not found
                bOk2 = false;
            }
            else
            {
//                System.out.println("store id read from config");
                if (logger.isInfoEnabled())
                {
                    logger.info("Store identifier successfully read from configuration: " + storeID);
                }
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
                // roll back two days (default date will be one day back)
                businessDate.roll(Calendar.DATE, false);
                businessDate.roll(Calendar.DATE, false);
                storeStatus.setBusinessDate(businessDate);
                storeStatus.setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);

                EYSDate eDate = storeStatus.getBusinessDate();
                if (eDate == null)
                {
//                    System.out.println("NO good business date");
                    RegisterException exception =
                        new RegisterException("A Business Date could not be obtained.");
                    exception.setErrorTextResourceName("InvalidBusinessDate");
                    exception.setErrorTextDefault("Invalid Date");
                    throw exception;
                    //throw new RegisterException("Could not obtain a business
                    // date");
                }
                // if date in hard totals, we will use it to prime business
                // date
                else
                {
//                    System.out.println("bus date init to {0}");
                    if (logger.isInfoEnabled())
                    {
                    	logger.info("Business date initialized to: " + eDate.toFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)));
                    }
                }

                //this.fromLegacy(ht.getRegister()); ---- Don't have a
                // register yet!!!
                store.fromLegacy(storeStatus);

                // No hard totals, so make up a Register
                RegisterIfc registerRDO = new Register();

                this.fromLegacy(registerRDO);
                //////////////////////////// new blj //////////////////
                cargo.setStoreStatus(storeStatus);
                cargo.setRegister(registerRDO);
                ////////////////////////////////////////////////////////
            } // end read store status
        }
    }

    /**
     * Returns the status of the till for the current operator
     * 
     * @return TillStatus
     */
    public TillStatus getOperatorTillStatus() throws RegisterException
    {
        // Not Implemented
        return null;
    }

    /**
     * Sets the Tender limits
     * 
     * @param TenderLimitsIfc
     *            object
     */
    public void setTenderLimits(TenderLimitsIfc tenderLimits) //throws
    // RegisterException
    {
        this.tenderLimits = tenderLimits;
    }

    /**
     * Returns the Tender limits
     * 
     * @return TenderLimitsIfc object
     */
    public TenderLimitsIfc getTenderLimits() //throws RegisterException
    {
        return tenderLimits;
    }

    //    ---------------------------------------------------------------------
    /**
     * Sets identifier of last reprintable transaction.
     * <P>
     * 
     * @param value
     *            new identifier of last reprintable transaction
     */
    //---------------------------------------------------------------------
    public void setLastReprintableTransactionID(String value)
    {
        lastReprintableTransactionID = value;
    }

    //---------------------------------------------------------------------
    /**
     * Returns identifier of last reprintable transaction.
     * <P>
     * 
     * @return identifier of last reprintable transaction
     */
    //---------------------------------------------------------------------
    public String getLastReprintableTransactionID()
    {
        return (lastReprintableTransactionID);
    }

    //---------------------------------------------------------------------
    /**
     * Returns the Logger instance.
     * <P>
     * 
     * @return logger (from super class)
     */
    //---------------------------------------------------------------------
    public Logger getLogger()
    {
        return (this.logger);
    }

    /*
     * private void RecoverFromHardTotalsErrors() { Logger logger =
     * Logger.getLogger(oracle.retail.stores.pos.ado.register.VirtualRegisterADO.class);
     * SessionBusIfc bus = ((SessionBusIfc) getContext().getBus());
     * AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo(); //
     * get cargo //MainCargo cargo = (MainCargo) bus.getCargo(); // initialize
     * store object based on store ID parameter and set in cargo //StoreIfc
     * store = DomainGateway.getFactory().getStoreInstance();
     * //store.setStoreID(storeID); //StoreStatusIfc storeStatus =
     * DomainGateway.getFactory().getStoreStatusInstance();
     * //storeStatus.setStore(store); // add business date (set to yesterday
     * with status open) // in case database read fails. operation will then
     * proceed // on previous business date. EYSDate businessDate =
     * DomainGateway.getFactory().getEYSDateInstance();
     * businessDate.initialize(EYSDate.TYPE_DATE_ONLY); // roll back two days
     * (default date will be one day back) businessDate.roll(Calendar.DATE,
     * false); businessDate.roll(Calendar.DATE, false); StoreStatusIfc
     * storeStatus = (StoreStatusIfc)store.toLegacy();
     * storeStatus.setBusinessDate(businessDate);
     * storeStatus.setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED); if
     * (logger.isInfoEnabled()) logger.info( businessDate);
     * cargo.setStoreStatus(storeStatus);
     */
}
