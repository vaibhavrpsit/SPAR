/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TelephoneTypeComboModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:46 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Apr 10 2003 13:41:52   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.3   Apr 02 2003 13:52:22   baa
 * I18n Database support for customer groups
 * Resolution for POS SCR-1866: I18n Database  support
 * 
 *    Rev 1.2   Mar 06 2003 09:21:22   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * @deprecated Replaced by ValidatingComboBox
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
//----------------------------------------------------------------------------
/**
 * This class contains the Telephone Types needed for either a ListBox or a ComboBox
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//----------------------------------------------------------------------------
public class TelephoneTypeComboModel extends DefaultListModel implements ComboBoxModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8605322518155084735L;

    /** revision number supplied by Team Connection **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** Selected telephone type **/
    protected String selectedType = null;
    
    

    //----------------------------------------------------------------------------
    /**
     * StateComboModel constructor. This initializes the model.
     */
    //----------------------------------------------------------------------------
    public TelephoneTypeComboModel() 
    {
        super();
        initialize();    
    }
    //----------------------------------------------------------------------------
    /**
     * This method returns the default telephone type for this combo list model. <P>
     * @return java.lang.String
     */
    //----------------------------------------------------------------------------
    public String getDefaultValue() 
    {
        return UIUtilities.retrieveCommonText(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[0]);
    }
    //----------------------------------------------------------------------------
    /**
     * Returns the selected item.
     * @return java.lang.Object
     */
    //----------------------------------------------------------------------------
    public Object getSelectedItem() 
    {
        return selectedType;
    }
    //----------------------------------------------------------------------------
    /**
     * Returns types of phone numbers used in Customer. <P>
     * @return java.lang.String[] An array phone number type strings.
     */
    //----------------------------------------------------------------------------
    public String[] getValues()
    {
        return PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR;
    }
    //----------------------------------------------------------------------------
    /**
     * Initializes the listmodel
     */
    //----------------------------------------------------------------------------
    protected void initialize() 
    {
        for(int i=0;i<PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length;i++)
        {
            addElement(UIUtilities.retrieveCommonText(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[i]));
        }
        selectedType = getDefaultValue();
    }
    //----------------------------------------------------------------------------
    /**
     * Sets the selected item.
     * @param item java.lang.Object
     */
    //----------------------------------------------------------------------------
    public void setSelectedItem(Object item)
    {
        if (!contains(item))
        {
            addElement(item);
        }
        if (!item.equals(selectedType))
        {
            selectedType = (String) item;
            fireContentsChanged(this, -1, -1);
        }
    }
}
