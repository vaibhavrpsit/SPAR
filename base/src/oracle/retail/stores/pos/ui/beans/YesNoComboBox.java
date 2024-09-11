/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/YesNoComboBox.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.2.1.0     12/26/2007 9:54:39 AM  Maisa De Camargo CR
 *         29822 - I18N - Fixed Collapsing of Input Fields when labels are
 *         expanded.
 *    3    360Commerce 1.2         3/31/2005 4:30:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:27:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:16:09 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 01 2003 14:01:36   baa
 * yes/no combo box issues
 * Resolution for 3468: Drop down boxes display incorrect data on Customer Details during Customer Search
 * 
 *    Rev 1.0.1.0   Dec 01 2003 12:00:22   baa
 * Address issues with updating yes/no combo boxes
 * Resolution for 3468: Drop down boxes display incorrect data on Customer Details during Customer Search
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//-------------------------------------------------------------------------
/**
 *  Encapsulates the BooleanComboModel in a combobox used for yes/no selection
 */
//-------------------------------------------------------------------------
public class YesNoComboBox extends ValidatingComboBox
{
    //---------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //---------------------------------------------------------------------
    public YesNoComboBox ()
    {
        super(new BooleanComboModel());
        setEditable(false);
    } 
} 
