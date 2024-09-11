/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PromptAndLiveResponseBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/11/09 - override activate method to be sure that amount due
 *                         is prompted
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *       merge
 *  3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:26 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:28 PM  Robert Pearse   
 *
 * Revision 1.3  2004/03/16 17:15:18  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:27  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 27 2002 16:50:24   dfh
 * fix tender amounts
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.1   Aug 14 2002 18:18:24   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

/**
 * This bean adds the automatic populating of the response field from the
 * transaction totals for the Tender Options and Refund Options.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $;
 */
public class PromptAndLiveResponseBean extends PromptAndResponseBean implements PropertyChangeListener
{
    private static final long serialVersionUID = -2125326991858526441L;
    /** revision number  */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Overridden to allow for each activation to set the prompt response text.
     * 
     * @see oracle.retail.stores.pos.ui.beans.PromptAndResponseBean#activate()
     */
    @Override
    public void activate()
    {
        setLivePrompt();
        currentResponseText = promptModel.getResponseText();
        super.activate();
    }

    /**
     * Respond to property change events from the TenderBean.
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        setLivePrompt();
    }

    /**
     * Determine and set the live prompt onto the response field.
     */
    protected void setLivePrompt()
    {
        if (beanModel instanceof TenderBeanModel)
        {
            TenderBeanModel model = (TenderBeanModel)beanModel;
            TransactionTotalsIfc totals = model.getTransactionTotals();

            if (totals != null)
            {
                // get balance due, if any, and set as default
                CurrencyIfc balance = totals.getBalanceDue();

                // We want balance to be selected for both positive and negative amounts
                if (balance.signum() == CurrencyIfc.POSITIVE && activeResponseField.isEditable())
                {
                    ((CurrencyTextField)activeResponseField).setCurrencyValue(balance);
                    activeResponseField.selectAll();
                }
                else if (balance.signum() == CurrencyIfc.NEGATIVE && activeResponseField.isEditable())
                {
                    ((CurrencyTextField)activeResponseField).setCurrencyValue(balance.abs());
                    activeResponseField.selectAll();
                }
                else
                {
                    activeResponseField.setText("");
                }
            }
            else
            {
                activeResponseField.setText("");
            }
            updateModel();
        }
    }
}
