/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogRegister.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/01/10 - removed and updated deprecated methods in Register
 *                         class
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:48:38  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:11:12  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.3  2004/04/28 11:25:36  mwright
 *   test case done
 *
 *   Revision 1.1.2.2  2004/04/26 07:31:13  mwright
 *   Changed to expect a POSSOD360 element to populate, instead of the Register element used previously. This new element is a v2.1 combination of the ixretail register open control transaction element, and the v1.0 Register extension element. It uses a TCSettle element to store the financial totals.
 *
 *   Revision 1.1.2.1  2004/04/19 07:06:02  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.Drawer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSSOD360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCSettleIfc;

import oracle.retail.stores.domain.ixretail.financial.LogRegisterIfc;
import oracle.retail.stores.domain.ixretail.financial.LogDrawerIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;

/**
 * This class creates the TLog in IXRetail format for the Retail Transaction
 * View for a register
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class LogRegister extends AbstractIXRetailTranslator implements LogRegisterIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Constructs LogRegister object.
     */
    public LogRegister()
    {
    }

    /**
     * Creates element for the specified register object.
     * 
     * @param register register reference
     * @param doc parent document (unused)
     * @param el register element to update
     * @param name element name (unused)
     * @return Updated element
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(RegisterIfc register, Document doc, // unused
            Element el, String name) // unused
            throws XMLConversionException
    {

        POSSOD360Ifc registerElement = (POSSOD360Ifc)el;

        // registerElement.setStoreID(register.getWorkstation().getStoreID());

        boolean registerOpen = false;
        if (register.getStatus() == AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            registerOpen = true;
            // add sign-on operator
            registerElement.setSignOnOperator(register.getSignOnOperator().getEmployeeID());
        }
        else
        {
            // add sign-off operator
            registerElement.setSignOffOperator(register.getSignOffOperator().getEmployeeID());
        }

        registerElement.setStatus(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[register.getStatus()]);

        /*
         * // get open/close time if (registerOpen) {
         * registerElement.setOpenTime(register.getOpenTime()); } else {
         * registerElement.setCloseTime(register.getCloseTime()); }
         */
        // registerElement.setBusinessDayDate(register.getBusinessDate());
        // registerElement.setWorkstationID(register.getWorkstation().getWorkstationID());

        if (!registerOpen)
        {
            registerElement.setLastTransactionSequenceNumber(Integer.toString(register
                    .getLastTransactionSequenceNumber()));
        }

        // registerElement.setCurrentTillID(register.getCurrentTillID());
        registerElement.setAccountability(AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[register
                .getAccountability()]);
        registerElement.setTillFloatAmount(currency(register.getTillFloatAmount()));
        registerElement.setTillCountAtReconcileDescriptor(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountTillAtReconcile()]);
        registerElement.setTillCountFloatAtOpenDescriptor(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountFloatAtOpen()]);
        registerElement.setTillCountFloatAtReconcileDescriptor(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountFloatAtReconcile()]);
        registerElement.setTillCountCashLoanDescriptor(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountCashLoan()]);
        registerElement.setTillCountCashPickupDescriptor(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountCashPickup()]);
        registerElement.setTillCountCheckPickupDescriptor(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountCheckPickup()]);
        registerElement.setTillReconcileFlag(new Boolean(register.isTillReconcile()));
        registerElement.setLastUniqueID(register.getCurrentUniqueID());

        // only report financial totals if register is closed
        if (!registerOpen)
        {
            createFinancialTotalsElements(register.getTotals(), registerElement);
        }

        // stub:
        createTillsElements(register.getTills(), register, registerElement);

        createDrawersElements(register.getDrawers(), registerElement);

        return registerElement;
    }

    /**
     * Creates element for the specified register object.
     * 
     * @param register register reference
     * @param doc parent document
     * @param el parent element
     * @return Element representing quantity
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(RegisterIfc register, Document doc, Element el) throws XMLConversionException
    {
        return createElement(register, null, el, null);
    }

    /**
     * Creates elements for financial totals.
     * 
     * @param totals financial totals
     * @param el element to which financial total elements are to be added
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createFinancialTotalsElements(FinancialTotalsIfc totals, POSSOD360Ifc el)
            throws XMLConversionException
    {
        if (totals != null)
        {
            LogFinancialTotalsIfc logTotals = IXRetailGateway.getFactory().getLogFinancialTotalsInstance();
            // FinancialTotals360Ifc totalsElement =
            // getSchemaTypesFactory().getFinancialTotals360Instance();
            POSLogTCSettleIfc settleElement = getSchemaTypesFactory().getPOSLogTCSettleInstance();

            logTotals.createElement(totals, null, settleElement);
            // el.setFinancialTotals(totalsElement);
            el.setSessionSettle(settleElement);
        }
    }

    /**
     * Creates elements for tills assigned to this register.
     * 
     * @param tills array of tills assigned to this register
     * @param register register refefence
     * @param el parent element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTillsElements(TillIfc[] tills, RegisterIfc register, POSSOD360Ifc el)
            throws XMLConversionException
    {
        // this will be done when till element is available
    }

    /**
     * Creates elements for drawers
     * 
     * @param drawers array of drawers
     * @param el element to which drawers total elements are to be added
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createDrawersElements(DrawerIfc[] drawers, POSSOD360Ifc el) throws XMLConversionException
    {
        if (drawers != null)
        {
            LogDrawerIfc logDrawer = IXRetailGateway.getFactory().getLogDrawerInstance();

            for (int i = 0; i < drawers.length; i++)
            {
                Drawer360Ifc drawerElement = getSchemaTypesFactory().getDrawer360Instance();
                logDrawer.createElement(drawers[i], null, drawerElement, null);
                el.addDrawer(drawerElement);
            }
        }
    }

}
