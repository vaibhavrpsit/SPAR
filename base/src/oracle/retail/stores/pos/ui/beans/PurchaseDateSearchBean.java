/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PurchaseDateSearchBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
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
 *    acadar    04/22/09 - translate date/time labels
 *    mkochumm  02/12/09 - use default locale for dates
 *    acadar    02/10/09 - use default locale for date/time display
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:27 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:29 PM  Robert Pearse
 * $
 * Revision 1.1  2004/07/20 13:08:06  aachinfiev
 * @scr 5438 - Added PurchaseDateSearchBean to eliminate system date from showing up in date fields
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.text.BadLocationException;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
//import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 * This class was created to eliminate defect described by SCR: 5438.
 *
 * This class is used to display and gather date range
 * information for inquiries. It doesn't have any default values
 * for the fields, and doesn't restore system date after comming
 * back from an error screen.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class PurchaseDateSearchBean extends DateSearchBean
{
    /** revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
     * This method is used to return date object if entered date is valid.
     * Otherwise it will return null.
     *
     * @param dateField DateField with entered text
     * @return EYSDate instance if date is valid, null otherwise
     */
    //--------------------------------------------------------------------------
    public EYSDate getEYSDate(EYSDateField dateField)
    {
        // Start with null
        EYSDate eysdate = null;
        if (dateField.getDocument() instanceof DateDocument)
        {
            // Setup date document
            DateDocument doc = (DateDocument) dateField.getDocument();
            doc.setLocale(getDefaultLocale());
            try
            {
                // Get text entered
                String dateString = doc.getText(0, doc.getLength());

                SimpleDateFormat formatter = DomainGateway.getSimpleDateFormat(getDefaultLocale(), doc.getFormat());
                eysdate = DomainGateway.getFactory().getEYSDateInstance();
                eysdate.setType(EYSDate.TYPE_DATE_ONLY);

                // parse() will throw ParseException if date is invalid
                eysdate.initialize(formatter.parse(dateString));
            }
            catch(ParseException e)
            {
                eysdate = null;
            }
            catch (BadLocationException e)
            {
                eysdate = null;
            }
        }
        return (eysdate);
    }

    //--------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //--------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setStartDate(getEYSDate(startDateField));
        beanModel.setEndDate(getEYSDate(endDateField));
    }

    //---------------------------------------------------------------------
    /**
     * Update the bean from the model
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        startDateField.setDate(beanModel.getStartDate());
        endDateField.setDate(beanModel.getEndDate());
    }
}
