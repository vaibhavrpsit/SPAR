/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ISODateGlobalButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         5/20/2007 7:40:09 PM   Mathews Kochummen fix
 *        field length
 *   3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *  Revision 1.1  2004/03/19 21:02:56  mweis
 *  @scr 4113 Enable ISO_DATE datetype
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;


//-------------------------------------------------------------------------
/**
   This class forces the minimum length of the date field to be 8
   so that it will handle "m/d/yyyy".  Depending on the actual date
   being entered, it might reset the minimum length of the date field
   to be 10 ("mm/dd/yyyy").  <p>
   The net result is that the "Next/Enter" button will be enabled
   or disabled based on the actual text's length in the date field
   versus the minimum needed to continue on.
*/
//-------------------------------------------------------------------------
public class ISODateGlobalButtonBean extends GlobalNavigationButtonBean 
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    private static final int MIN_DEFAULT_LENGTH =  6;  //   example 1/1/07
    private static final int MAX_DEFAULT_LENGTH = 10;  // example 12/31/2007

    /**
     * Default constructor.
     */
    public ISODateGlobalButtonBean()
    {
        super();
        setMinLength(MIN_DEFAULT_LENGTH);
    }
    
    /**
     * Constructor that initializes with actions.
     * @param actions Two dimensional list of actions.
     */
    public ISODateGlobalButtonBean(UIAction[][] actions)
    {
        super(actions);
        setMinLength(MIN_DEFAULT_LENGTH);
    }
    
    /**
     * Called as part of the DocumentListener interface when
     * the date field is appended to.
     * @param evt The document event. 
     */
    public void changedUpdate(DocumentEvent evt)
    {
        resetMinLength(evt.getDocument());
        super.changedUpdate(evt);
    }

    /**
     * Called as part of the DocumentListener interface when
     * the date field is inserted into.
     * @param evt The document event. 
     */
    public void insertUpdate(DocumentEvent evt)
    {
        resetMinLength(evt.getDocument());
        super.insertUpdate(evt);
    }
    
    /**
     * Called as part of the DocumentListener interface when
     * the date field has a delete.
     * @param evt The document event. 
     */
    public void removeUpdate(DocumentEvent evt)
    {
        resetMinLength(evt.getDocument());
        super.removeUpdate(evt);
    }
    
    /**
     * Dynamically resets the minimum required length of the date field.
     * @param doc The document from the document event.
     */
    protected void resetMinLength(Document doc)
    { 
    	if (doc instanceof DateDocument)
    	{
    		DateDocument dateDoc = (DateDocument) doc;
    		setMinLength(MIN_DEFAULT_LENGTH);
    	}
    }
}
