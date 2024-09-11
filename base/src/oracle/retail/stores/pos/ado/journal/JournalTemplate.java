/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/JournalTemplate.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:42 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

/**
 *  
 */
public class JournalTemplate implements JournalTemplateIfc
{
    protected int journalLineWidth = 40;

    protected String endOfLine = "\n";

    /**
     * Return the width of the journal String
     * 
     * @return
     */
    public final int getJournalLineWidth()
    {
        return journalLineWidth;
    }

    /**
     * Return the End of Line String
     * 
     * @return
     */
    public String getEndOfLine()
    {
        return endOfLine;
    }
}
