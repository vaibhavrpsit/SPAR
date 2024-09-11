/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/ModifyTransactionSuspendCargo.java /main/14 2013/06/26 14:36:58 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/26/13 - implemented RetailTransactionCargoIfc and removed
 *                         some deprecated methods.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    mdecama   10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
     $
     Revision 1.6  2004/09/27 22:32:03  bwf
     @scr 7244 Merged 2 versions of abstractfinancialcargo.

     Revision 1.5  2004/09/15 16:34:22  kmcbride
     @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions

     Revision 1.4  2004/02/24 16:21:27  cdb
     @scr 0 Remove Deprecation warnings. Cleaned code.

     Revision 1.3  2004/02/12 16:51:16  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:47  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:02:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Mar 20 2003 11:08:06   HDyer
 * Fixed deprecation warning.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Feb 14 2003 16:16:26   crain
 * Refactored getReasonCodes()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.1   Jan 10 2003 17:09:06   DCobb
 * Added printer offline behavior.
 * Resolution for POS SCR-1892: The printer offline message does not appear for transaction suspension or retrieval
 *
 *    Rev 1.0   Apr 29 2002 15:15:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:54   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.suspend;

import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;

/**
 * Cargo class for ModifyTransactionSuspend service.
 * 
 * @version $Revision: /main/14 $
 */
public class ModifyTransactionSuspendCargo extends AbstractFinancialCargo
    implements RetailTransactionCargoIfc, CargoIfc, DBErrorCargoIfc
{

    /**
     * Generated Version UID
     */
    private static final long serialVersionUID = 9187892464995492543L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ModifyTransactionSuspendCargo.class);

    /**
     * the transaction
     */
    protected RetailTransactionIfc transaction = null;

    /**
     * selected reason code index
     * 
     * @deprecated as of 13.1 Use {@link #selectedLocalizedReason}
     */
    protected String selectedReason = new String("Unknown");

    /**
     * selected reason code index
     * 
     * @deprecated as of 13.1 Use {@link #selectedLocalizedReason}
     */
    protected int selectedReasonCodeIndex = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;

    /**
     * reason codes
     */
    protected CodeListIfc reasonCodes = null;

    /**
     * Selected Reason Code
     */
    protected LocalizedCodeIfc selectedLocalizedReason = DomainGateway.getFactory().getLocalizedCode();

    /**
     * Constructs ModifyTransactionSuspendCargo object.
     */
    public ModifyTransactionSuspendCargo()
    {
    }

    /**
     * Returns the retail transaction.
     * 
     * @return the retail transaction
     */
    public void setTransaction(RetailTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Returns the retail transaction.
     * 
     * @return the retail transaction
     */
    public RetailTransactionIfc getTransaction()
    {
        return transaction;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc#getRetailTransaction()
     */
    @Override
    public RetailTransactionIfc getRetailTransaction()
    {
        return transaction;
    }

    /**
     * Not implemented. Returns null.
     *
     * @see oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc#getOriginalReturnTransactions()
     */
    @Override
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc#getTenderableTransaction()
     */
    @Override
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return transaction;
    }

    /**
     * Retrieves the till ID. May return empty string if the current register
     * has not been set or the current till does not exist.
     *
     * @return String till ID
     * @see oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc#getTillID()
     */
    @Override
    public String getTillID()
    {
        String id = "";
        RegisterIfc register = getRegister();

        // Make sure the register exists
        if (register != null)
        {
            TillIfc till = register.getCurrentTill();

            // Make sure the till exists
            if (till != null)
            {
                String tillID = till.getTillID();

                // Make sure the ID exists
                if (tillID != null)
                {
                    id = tillID;
                }
            }
        }

        return id;
    }

    /**
     * Returns array of reason codes.
     * 
     * @return array of reason codes
     */
    public CodeListIfc getReasonCodes()
    {
        return reasonCodes;
    }

    /**
     * Sets the reason code list
     * 
     * @param reasonCodes
     */
    public void setReasonCodes(CodeListIfc reasonCodes)
    {
        this.reasonCodes = reasonCodes;
    }

    /**
     * @return the selectedLocalizedReason
     */
    public LocalizedCodeIfc getSelectedLocalizedReason()
    {
        return selectedLocalizedReason;
    }

    /**
     * @param selectedLocalizedReason the selectedLocalizedReason to set
     */
    public void setSelectedLocalizedReason(LocalizedCodeIfc reasonSelected)
    {
        this.selectedLocalizedReason = reasonSelected;
    }

    /**
     * Retrieves a single string value from the config file
     * 
     * @param pm is the ParameterManagerIfc
     * @param paramName is the String name of the parameter in the config file
     *            for which we want the value
     * @return the String value of the paramName parameter
     */
    public String getParameterValue(ParameterManagerIfc pm, String paramName)
    {
        Serializable[] values = null;
        String returnValue = null;

        try
        {
            values = pm.getParameterValues(paramName);
            returnValue = (String) values[0];
            if (logger.isInfoEnabled())
                logger.info("Parameter read: " + paramName + " =[" + returnValue + "]");
        }
        catch (ParameterException e)
        {
            logger.error("" + Util.throwableToString(e) + "");
        }

        return returnValue;
    }

    /**
     * Returns the string representation of the object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class:  ModifyTransactionSuspendCargo");
        strResult.append(" (Revision ").append(getRevisionNumber())
                .append(")").append(hashCode());
        if (getTransaction() == null)
        {
            strResult.append("Transaction:                        [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("Transaction:                        [").append(getTransaction().getTransactionID())
                    .append("]").append(Util.EOL);
        }
        return (strResult.toString());
    }

    /**
     * Returns the revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

}
