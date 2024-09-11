/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/LineItemFormatter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:42 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         7/18/2007 8:43:35 AM   Alan N. Sinton  CR
 *       27651 - Made Post Void EJournal entries VAT compliant.
 *  3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.0 Nov 04 2003 11:11:14 epd Initial revision.
 *
 * Rev 1.0 Oct 17 2003 12:31:22 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.util.Locale;
import java.util.Map;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 *
 */
public class LineItemFormatter implements RegisterJournalFormatterIfc
{
    // journal memento keys
    public static final String ID = "ID";
    // Journal locale
    Locale journalLocale =  LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

    /* Our application context */
    protected ADOContextIfc context;

    /**
     * Handle to the ParameterManagerIfc.
     */
    protected ParameterManagerIfc parameterManager;

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.journal.RegisterJournalFormatterIfc#format(oracle.retail.stores.ado.journal.JournalTemplateIfc,
     *      oracle.retail.stores.ado.journal.JournalableADOIfc,
     *      oracle.retail.stores.ado.journal.JournalActionEnum)
     */
    public String format(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        JournalActionEnum action)
    {

    	StringBuffer sb = new StringBuffer(256);

        if (action == JournalActionEnum.GIFT_CARD_INQUIRY)
        {
            formatGiftCardInquiry(template, journalable, sb);
        }

        return sb.toString();
    }

    protected void formatGiftCardInquiry(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        StringBuffer sb)
    {
        // TODO: uncomment assert() when moved to JDK1.4
        //asert(journalable instanceof GiftCardLineItemADO);
        Object[] dataArgs = new Object[]{(String) journalable.getJournalMemento().get(ID)};
        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_INQUIRY_LABEL,null,journalLocale));
        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_NUMBER_LABEL, dataArgs,journalLocale));
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.ContextSensitiveIfc#setContext(oracle.retail.stores.ado.context.ADOContextIfc)
     */
    public void setContext(ADOContextIfc context)
    {
        this.context = context;
    }

    /**
     * Sets the ParmaeterManager instance.
     * @param pm
     */
    public void setParameterManager(ParameterManagerIfc pm)
    {
        parameterManager = pm;
    }
}
