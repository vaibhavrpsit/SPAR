/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/CheckAccountabilitySite.java /main/12 2013/11/21 11:38:43 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/21/13 - do not unlock container when setting model
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:10:51 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/18/2005 15:35:05    Deepanshu       CR
 *         4932: Set password required to true if
 *         'IdentifyCashierEveryTransaction' parameter is set to 'Yes'
 *    3    360Commerce1.2         3/31/2005 15:27:23     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:04     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:52     Robert Pearse
 *
 *   Revision 1.6  2004/06/10 23:06:35  jriggins
 *   @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 *   Revision 1.5  2004/03/23 16:14:29  jdeleau
 *   @scr 4040 For Automatic logoff, never ask for operator ID
 *   always use whatever is in the cargo.
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Nov 17 2003 14:59:02   cdb
 * In preparation for service changes, null operator is tested for and login required for such. The goal is to remove code block checking for user accountability.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.1   08 Nov 2003 01:24:28   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 04 2003 19:03:36   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This site checks the accountability for Register or Cashier and does the
 * appropriate processing.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class CheckAccountabilitySite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    protected static final String LETTER_OPERATOR_ID = "OperatorID";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        boolean callOperatorID = false;
        String letterName = null;
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();

        // Clear UI status
        clearStatus(bus);

        // Clear transaction information
        clearCargo(cargo);

        RegisterIfc register = cargo.getRegister();

        // If timeout occured, use whatever ID is already in the cargo
        // for logout.  Can not ask for ID on automatic timeout.
        if(((TimedCargoIfc)cargo).isTimeout())
        {
            callOperatorID = false;
        }
        else if (cargo.getOperator() == null)
        {
            callOperatorID = true;
        }
        else if (cargo.isPasswordRequired())
        {
            callOperatorID = true;
        }
        else if (register.getAccountability()
                 == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER)
        {
            callOperatorID = determineOperatorFromOpenTill(bus, cargo, register);
        }
        else if (register.getAccountability()
                 == AbstractFinancialEntityIfc.ACCOUNTABILITY_REGISTER)
        {
            //Requirements ask for password required in this mode
            cargo.setPasswordRequired(true);
            callOperatorID = true;
        }

        if (callOperatorID)
        {
            letterName = LETTER_OPERATOR_ID;
        }
        else
        {
            letterName = CommonLetterIfc.CONTINUE;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * @param cargo
     */
    protected void clearCargo(SaleCargoIfc cargo)
    {
        cargo.setTransaction(null);
        cargo.resetOriginalReturnTransactions();
        cargo.resetOriginalPriceAdjustmentTransactions();
        cargo.setIndex(-1);
    }

    /**
     * @param bus
     */
    protected void clearStatus(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        statusModel.setCashierName("");
        statusModel.setSalesAssociateName("");
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel, false);
    }

    /**
     * @param bus
     * @param cargo
     * @param register
     * @return
     */
    protected boolean determineOperatorFromOpenTill(BusIfc bus, SaleCargoIfc cargo,
            RegisterIfc register)
    {
        boolean callOperatorID = false;
        // Check IdentifyCashierEveryTransaction parameter first
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            Boolean value = pm.getBooleanValue("IdentifyCashierEveryTransaction");

            if (value != null && value == Boolean.TRUE)
            {
                cargo.setPasswordRequired(true);
                callOperatorID = true;
            }
        }
        catch (ParameterException e)
        {
            logger.error("Unable to get parameter value for IdentifyCashierEveryTransaction.", e);
        }

        // See if there is more than one open till
        if (!callOperatorID)
        {
            TillIfc[] tills = register.getTills();
            int openTills = 0;

            if (tills != null)
            {
                for (int i = 0; i < tills.length; ++i)
                {
                    if (tills[i].getStatus() == TillIfc.STATUS_OPEN)
                    {
                        ++openTills;

                        // If there is only one open till, this will default the proper cashier
                        cargo.setOperator(tills[i].getSignOnOperator());
                    }
                }
            }

            if (openTills > 1)
            {
                // more than one open till, we need to identify the operator
                callOperatorID = true;
            }
        }
        return callOperatorID;
    }

}
