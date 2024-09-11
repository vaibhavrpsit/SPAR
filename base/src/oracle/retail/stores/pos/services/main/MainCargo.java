/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/MainCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/09/09 - remove methods that exist in super class
 *                         StoreStatusCargo
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:31 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/08/16 21:14:52  lzhao
 *   @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.
 *
 *   Revision 1.5  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.4  2004/03/14 21:12:40  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/02/12 16:48:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:24:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Dec 30 2003 14:22:24   rwh
 * Refactored classes in Main to use RegisterADO in place of VirtualRegisterADO. Added methods to RegisterADO, tender limits and operator. Moved read financials method from VirtualRegisterADO to MainTDO
 * Resolution for POS SCR-3653: RegisterADO Refactor
 * 
 *    Rev 1.2   Dec 16 2003 13:29:06   bjosserand
 * Main Refactor. Rewrite initialization using ADO and TDO objects.
 * 
 *    Rev 1.1   08 Nov 2003 01:12:52   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 06 2003 00:21:52   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:01:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   29 Nov 2001 08:24:30   epd
 * Now extends StoreStatusCargo
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:21:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.StoreStatusCargo;

/**
 * This class represents the main service cargo. <>P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class MainCargo extends StoreStatusCargo implements TourCamIfc
{
    private static final long serialVersionUID = -4883993416159252933L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public final static int MAIN_NO_ERROR = 0;
    public final static int MAIN_STORE_PROPERTIES_ERROR = 1;
    public final static int MAIN_NOT_FOUND_ERROR = 2;
    public final static int MAIN_DATABASE_ERROR = 3;
    public final static int MAIN_NOT_SUSPENDED_ERROR = 4;
    public final static int MAIN_NOT_FLOATING_ERROR = 5;
    public final static int MAIN_ACCOUNTABILITY_ERROR = 6;
    public final static int MAIN_CASHIER_ERROR = 7;
    public final static int MAIN_DRAWER_ERROR = 8;

    /**
     * This flag indicates if training mode is currently on.
     */
    protected boolean trainingMode = false;

    private RegisterADO registerADO = null;

    /**
     * This register holds the training mode register
     * @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead. 
     */
    private RegisterADO trainingRegisterADO = null;

    private CustomerInfoIfc customerInfo;
    private String errorTextKey;
    private String errorTextDefault;
    private String errorTextResourceName;

    /**
     * This method is here to prevent any Main code from updating the RDO
     * register directly. We must use the ADO registers.
     */
    final RegisterIfc register = null;

    public void setRegisterADO(RegisterADO register)
    {
        this.registerADO = register;
    }

    public RegisterADO getRegisterADO()
    {
        return registerADO;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.AbstractFinancialCargo#getRegister()
     */
    public RegisterIfc getRegister()
    {
        return (RegisterIfc) registerADO.toLegacy();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.AbstractFinancialCargo#setRegister(oracle.retail.stores.domain.financial.RegisterIfc)
     */
    public void setRegister(RegisterIfc value)
    {
        assert (false) : "Use ADO register instead";
        throw new UnsupportedOperationException("Use ADO register instead of domain RegisterIfc");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.AbstractFinancialCargo#setStoreStatus(oracle.retail.stores.domain.financial.StoreStatusIfc)
     */
    public void setStoreStatus(StoreStatusIfc value)
    {
        assert (false) : "Use ADO register instead to save status";
        throw new UnsupportedOperationException("Use ADO register instead of domain RegisterIfc to save status");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.AbstractFinancialCargo#getStoreStatus()
     */
    public StoreStatusIfc getStoreStatus()
    {
        return (StoreStatusIfc) registerADO.getStoreADO().toLegacy();
    }

    /**
     * Sets training mode Register ADO.
     * 
     * @param trainingRegister new training mode Register ADO
     * @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead. 
     */
    public void setTrainingRegisterADO(RegisterADO trainingRegister)
    {
        this.trainingRegisterADO = trainingRegister;
    }

    /**
     * Returns training mode Register ADO.
     * 
     * @return training mode Register ADO
     * @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead. 
     */
    public RegisterADO getTrainingRegisterADO()
    {
        RegisterIfc register = (RegisterIfc) registerADO.toLegacy();
        RegisterIfc trainingRegister = (RegisterIfc) register.clone();
        StoreStatusIfc ss = (StoreStatusIfc) registerADO.getStoreADO().toLegacy().clone();
        trainingRegister.getWorkstation().setTrainingMode(true);
        trainingRegisterADO.fromLegacy(trainingRegister);
        trainingRegisterADO.getStoreADO().fromLegacy(ss);
        register.setOtherRegister(trainingRegister);
        trainingRegister.setOtherRegister(register);
        return trainingRegisterADO;
    }


    /**
     * data exception error code
     */
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
     * Indicates if the Online Listener has been set. The application uses it to
     * prevent more than one listener from being set.
     */
    protected boolean onlineListenerHasBeenSet = false;

    /**
     * Returns the data exception error code.
     * 
     * @return The data exception error code.
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the online listener has been set indicator.
     * 
     * @param boolean true if the listener has been set.
     */
    public void setOnlineListenerHasBeenSet(boolean value)
    {
        onlineListenerHasBeenSet = value;
    }

    /**
     * Gets the online listener has been set indicator.
     * 
     * @return boolean true if the listener has been set.
     */
    public boolean getOnlineListenerHasBeenSet()
    {
        return onlineListenerHasBeenSet;
    }

    /**
     * Sets the data exception error code.
     * 
     * @param value The data exception error code.
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Returns the customer info.
     * 
     * @return The customer info
     */
    public CustomerInfoIfc getCustomerInfo()
    {
        return customerInfo;
    }

    /**
     * Sets the customer info.
     * 
     * @param CustomerInfo the customer info
     */
    public void setCustomerInfo(CustomerInfoIfc customerInfo)
    {
        this.customerInfo = customerInfo;
    }

    /**
     * Returns the error text string.
     * 
     * @return The error text
     */
    public String getErrorTextKey()
    {
        return errorTextKey;
    }

    /**
     * Sets the error text string.
     * 
     * @param String the error text
     */
    public void setErrorTextKey(String errorTextKey)
    {
        this.errorTextKey = errorTextKey;
    }

    /**
     * Returns the error text default.
     * 
     * @return The error text default
     */
    public String getErrorTextDefault()
    {
        return errorTextDefault;
    }

    /**
     * Sets the error text default.
     * 
     * @param int the error text default
     */
    public void setErrorTextDefault(String errorTextDefault)
    {
        this.errorTextDefault = errorTextDefault;
    }

    /**
     * Returns the error text resource name.
     * 
     * @return The error text resource name
     */
    public String getErrorTextResourceName()
    {
        return errorTextResourceName;
    }

    /**
     * Sets the error text resource name.
     * 
     * @param int the error text resource name
     */
    public void setErrorTextResourceName(String errorTextResourceName)
    {
        this.errorTextResourceName = errorTextResourceName;
    }

    /**
     * Sets the trainingMode flag.
     * 
     * @param value boolean
     */
    public void setTrainingMode(boolean value)
    {
        trainingMode = value;
    }

    /**
     * Returns the trainingMode flag.
     * 
     * @return boolean
     */
    public boolean isTrainingMode()
    {
        return trainingMode;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.StoreStatusCargo#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  MainCargo (Revision ");
        strResult.append(getRevisionNumber() + ") @" + hashCode());
        strResult.append("\n");
        // add attributes to string
        strResult.append(abstractToString()).append("\n");
        strResult.append("Data exception error code:              [").append(dataExceptionErrorCode).append("]\n");
        if (storeStatusList == null)
        {
            strResult.append("Store status list:                      [null]\n");
        }
        else
        {
            strResult.append("Store status list length:               [").append(storeStatusList.length).append("]\n");
            for (int i = 0; i < storeStatusList.length; i++)
            {
                strResult.append("Store status ").append(i + 1).append("\nSub").append(storeStatusList[i]).append("\n");
            }
        }

        // pass back result
        return strResult.toString();
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}