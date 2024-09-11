/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TillStatusRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
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
 *    tzgarba   02/25/09 - Removed test class dependencies from shipping
 *                         source.
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:26:15 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 *Revision 1.3  2004/03/16 17:15:18  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.6   Jul 16 2003 17:36:10   bwf
 * Only display active cashiers.
 * Resolution for 1782: Cashier Logoff is not removing the cashier from the list of active cashiers
 * Resolution for 3173: Cashier Logoff causes logged off cashier to be the only active cashier
 * 
 *    Rev 1.5   Apr 09 2003 17:50:30   baa
 * I18n database conversion
 * Resolution for POS SCR-1866: I18n Database  support
 * 
 *    Rev 1.4   Mar 24 2003 16:27:14   bwf
 * Code Cleanup
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.3   Mar 24 2003 16:22:56   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.2   Sep 06 2002 17:25:38   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 14 2002 18:19:02   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:55:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 06 2002 09:56:48   mpm
 * Initial revision.
 * Resolution for POS SCR-1513: Add Till Status screen
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
   This is the renderer for the till status table. <P>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class TillStatusRenderer extends AbstractListRenderer
{
    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * till id field constant
     */
    public static int TILL_ID = 0;
    /**
     * till status field constant
     */
    public static int TILL_STATUS = 1;
    /**
     * till cashiers field constant
     */
    public static int TILL_CASHIERS = 2;
    /**
     * maximum fields constants
     */
    public static int MAX_FIELDS    = 3;
    /**
     * renderer weights
     */
    public static int[] STATUS_WEIGHTS = {20,20,60};
    protected String localized_status[]= new String[AbstractFinancialEntityIfc.STATUS_DESCRIPTORS.length];
    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public TillStatusRenderer()
    {
        super();
        setName("TillStatusRenderer");
        firstLineWeights = STATUS_WEIGHTS;
        setFirstLineWeights("labelWeights");
        fieldCount = MAX_FIELDS;
        lineBreak  = MAX_FIELDS;
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {

        labels[TILL_ID].setHorizontalAlignment(JLabel.LEFT);
        labels[TILL_STATUS].setHorizontalAlignment(JLabel.LEFT);
        labels[TILL_CASHIERS].setHorizontalAlignment(JLabel.LEFT);
    }

    //---------------------------------------------------------------------
    /**
        Render data into cells. <P>
        @param value object to be rendered
     */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        TillIfc till = (TillIfc) value;
        
        // get correct status
        String tempStr = AbstractFinancialEntityIfc.STATUS_DESCRIPTORS[till.getStatus()];
        String tillStatus = UIUtilities.retrieveCommonText(tempStr, tempStr);
                                              
        labels[TILL_ID].setText(till.getTillID());
        labels[TILL_STATUS].setText(tillStatus);
 
        // pull cashiers list
        EmployeeIfc[] emp = till.getCashiers();
        StringBuffer cashierIDs = new StringBuffer();
        boolean activeEmployee = false;
        for (int i = 0; i < emp.length; i++)
        {
            if(emp[i].getLoginStatus() != EmployeeIfc.LOGIN_STATUS_INACTIVE)
            {
                if (i > 0 &&
                    activeEmployee)
                {
                    cashierIDs.append(", ");
                }           
                cashierIDs.append(emp[i].getLoginID());
                activeEmployee = true;
            }
        }
        labels[TILL_CASHIERS].setText(cashierIDs.toString());

    }

    //---------------------------------------------------------------------
    /**
        Sets property fields.  In this instance, it is a no-op.  It is
        required to satisfy the AbstractListRenderer interface.
    **/
    //---------------------------------------------------------------------
    public void setPropertyFields()
    {                                   // begin setPropertyFields()
    }                                   // end setPropertyFields()

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        TillIfc dummyTill = DomainGateway.getFactory().getTillInstance();
        dummyTill.setBusinessDate(new EYSDate());
        dummyTill.setCloseTime();
        dummyTill.setOpenTime();
        dummyTill.setTillID("dummy");
        dummyTill.setStatus(AbstractFinancialEntityIfc.STATUS_RECONCILED);
        return(dummyTill);
    }

     //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  TillStatusRenderer (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }
}
