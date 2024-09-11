/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeRenderer.java /main/19 2013/05/21 14:16:35 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  05/21/13 - Made the width calculation dynamic to that of the
 *                         screen dimensions
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:20 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *
 *  Revision 1.8  2004/07/14 22:47:05  bvanschyndel
 *  @scr 5268 Added logs to header and removed extra constant
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing imports
import java.util.Locale;

import javax.swing.JLabel;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
    Contains the visual rendering for the Prescription Selection list
    @version $Revision: /main/19 $
**/
//-------------------------------------------------------------------------
public class EmployeeRenderer extends AbstractListRenderer
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/19 $";

    public static int NAME       = 0;
    public static int ID         = 1;
    public static int ROLE       = 2;
    public static int MAX_FIELDS = 3;

    public static int[] EMPLOYEE_WEIGHTS = {62,18,20};


    //---------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //---------------------------------------------------------------------
    public EmployeeRenderer()
    {
        super();
        setName("EmployeeRenderer");

        // set default in case lookup fails
        firstLineWeights = EMPLOYEE_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("labelWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = ROLE;
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        labels[NAME].setHorizontalAlignment(JLabel.LEFT);
        labels[ID].setHorizontalAlignment(JLabel.LEFT);
        labels[ROLE].setHorizontalAlignment(JLabel.LEFT);
    }

    //---------------------------------------------------------------------
    /**
     * This sets the fields of this Renderer.
     * @param prescription the Prescription data that is to be rendered
     */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        EmployeeIfc employee = (EmployeeIfc)value;
        PersonNameIfc name = employee.getPersonName();

        // get the user's locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);        

        // get the last name
        StringBuffer nm = new StringBuffer(name.getLastName());

        // append a comma and the first name
        nm.append(", ")
          .append(name.getFirstName());

        // append middle name, if any
        String middle = name.getMiddleName();
        if (middle != null && !"".equals(middle))
        {
            nm.append(" ")
              .append(middle);
        }

        labels[NAME].setText(nm.toString());
        labels[ID].setText(employee.getLoginID());
        if (employee.getRole() !=null)
        {
          labels[ROLE].setText(employee.getRole().getTitle(userLocale));
        }
    }

    //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency and quantities.
    */
    //---------------------------------------------------------------------
    protected void setPropertyFields()
    {
        // Get the format string spec from the UI model properties.

        if (props != null)
        {
        }
    }

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TenderLineItemIfc the prototype cell
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        return new Object();
    }
    
    //---------------------------------------------------------------------
    /**
     * Returns the width of the list in number of pixels
     * @return int width of list
     */
    //---------------------------------------------------------------------
    public int getWidth()
    {
        return ((int)getBounds().getWidth());
    }

    //----------------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @param none
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  EmployeeMasterBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        if (labels[NAME] !=null)
        {

            strResult += "\n\nnameField text = ";
            strResult +=  labels[NAME].getName();
        }
        else
        {
           strResult += "\nnameField = null\n";
        }

        if (labels[ID] !=null)
        {

            strResult += "\n\nidField text = ";
            strResult +=  labels[ID].getName();
        }
        else
        {
           strResult += "\nidField = null\n";
        }

        if (labels[ROLE] !=null)
        {

            strResult += "\n\nroleField text = ";
            strResult +=  labels[ROLE].getText();

        }
        else
        {
           strResult += "\nroleField = null\n";
        }


        // pass back result
        return(strResult);

    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @param none
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
      public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }

   //--------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
        EmployeeRenderer renderer = new EmployeeRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
