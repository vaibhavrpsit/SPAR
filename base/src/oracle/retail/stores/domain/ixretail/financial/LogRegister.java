/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogRegister.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
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
 *   Revision 1.3  2004/02/12 17:13:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:18   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 11 2002 10:05:44   mpm
 * Implemented refactored register open/close transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 06 2002 19:39:46   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

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
     * @param doc parent document
     * @param el parent element
     * @param name element name
     * @return Element representing quantity
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(RegisterIfc register, Document doc, Element el, String name)
            throws XMLConversionException
    {
        setParentDocument(doc);
        setParentElement(el);

        Element registerElement = parentDocument.createElement(name);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID, register.getWorkstation().getStoreID(),
                registerElement);

        boolean registerOpen = false;
        if (register.getStatus() == AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            registerOpen = true;
            // add sign-on operator
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SIGN_ON_OPERATOR, register.getSignOnOperator()
                    .getEmployeeID(), registerElement);
        }
        else
        {
            // add sign-off operator
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SIGN_OFF_OPERATOR, register.getSignOffOperator()
                    .getEmployeeID(), registerElement);
        }

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_STATUS,
                AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[register.getStatus()], registerElement);

        // get open/close time
        if (registerOpen)
        {
            createTimestampTextNodeElement(IXRetailConstantsIfc.ELEMENT_OPEN_TIME, register.getOpenTime(),
                    registerElement);
        }
        else
        {
            createTimestampTextNodeElement(IXRetailConstantsIfc.ELEMENT_CLOSE_TIME, register.getCloseTime(),
                    registerElement);
        }

        createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE, register.getBusinessDate(),
                registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_WORKSTATION_ID,
                register.getWorkstation().getWorkstationID(), registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_LAST_TRANSACTION_SEQUENCE_NUMBER, register
                .getLastTransactionSequenceNumber(), registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CURRENT_TILL_ID, register.getCurrentTillID(),
                registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ACCOUNTABILITY,
                AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[register.getAccountability()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_FLOAT_AMOUNT, register.getTillFloatAmount(),
                registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_COUNT_TILL_AT_RECONCILE,
                FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountTillAtReconcile()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_COUNT_FLOAT_AT_OPEN,
                FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountFloatAtOpen()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_COUNT_FLOAT_AT_RECONCILE,
                FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountFloatAtReconcile()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_COUNT_CASH_LOAN,
                FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountCashLoan()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_COUNT_CASH_PICKUP,
                FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountCashPickup()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_COUNT_CHECK_PICKUP,
                FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountCheckPickup()], registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_RECONCILE_FLAG, register.isTillReconcile(),
                registerElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_LAST_UNIQUE_ID, register.getCurrentUniqueID(),
                registerElement);

        // only report financial totals if register is closed
        if (!registerOpen)
        {
            createFinancialTotalsElements(register.getTotals(), registerElement);
        }

        createTillsElements(register.getTills(), register, registerElement);

        createDrawersElements(register.getDrawers(), registerElement);

        parentElement.appendChild(registerElement);

        return (registerElement);
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
        return (createElement(register, doc, el, IXRetailConstantsIfc.ELEMENT_REGISTER));
    }

    /**
     * Creates elements for financial totals.
     * 
     * @param totals financial totals
     * @param el element to which financial total elements are to be added
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createFinancialTotalsElements(FinancialTotalsIfc totals, Element el) throws XMLConversionException
    {
        if (totals != null)
        {
            LogFinancialTotalsIfc logTotals = IXRetailGateway.getFactory().getLogFinancialTotalsInstance();

            logTotals.createElement(totals, parentDocument, el);
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
    protected void createTillsElements(TillIfc[] tills, RegisterIfc register, Element el) throws XMLConversionException
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
    protected void createDrawersElements(DrawerIfc[] drawers, Element el) throws XMLConversionException
    {
        if (drawers != null)
        {
            LogDrawerIfc logDrawer = IXRetailGateway.getFactory().getLogDrawerInstance();

            for (int i = 0; i < drawers.length; i++)
            {
                logDrawer.createElement(drawers[i], parentDocument, el, IXRetailConstantsIfc.ELEMENT_DRAWER);
            }
        }
    }
}
