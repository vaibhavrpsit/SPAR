/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ShippingMethodBeanModel.java /main/19 2013/06/21 09:32:11 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/21/13 - Remove NullPointerException.
 *    mkutiana  05/16/13 - retaining the values of the ShippingBeanModel upon
 *                         error on the SelectShippingMethodSite
 *    yiqzhao   03/14/13 - Remove unnecessary comments
 *    yiqzhao   03/13/13 - Add reason code for shipping charge override for
 *                         cross channel and store send.
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/18/09 - prevent null pointers when no shipping charge codes
 *                         are available
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:51:28 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/06/29 20:42:07  rsachdeva
 *   @scr 4670 Send: Multiple Sends
 *
 *   Revision 1.4  2004/04/27 17:24:31  cdb
 *   @scr 4166 Removed unintentional null pointer exception potential.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:49:40   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 14:52:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:48   msg
 * Initial revision.
 * 
 *    Rev 1.8   21 Jan 2002 10:54:06   sfl
 * Removed the system.out.println line.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.7   Jan 19 2002 10:31:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   02 Jan 2002 18:43:34   baa
 * updates for send
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   13 Dec 2001 18:00:16   baa
 * updates to support offline
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   06 Dec 2001 18:49:08   baa
 * additional updates for  send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   04 Dec 2001 15:33:24   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   Sep 21 2001 11:36:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;

/**
 * This is the model used to pass customer information
 * 
 * @version $Revision: /main/19 $
 */
public class ShippingMethodBeanModel extends MailBankCheckInfoBeanModel
{
    private static final long serialVersionUID = 6396821293430350235L;

    // list of available shipping methods
    protected ShippingMethodIfc methodsList[];

    // indicate whether the db is offline
    protected boolean offline = false;

    // Special Instructions
    protected String instructions = "";

    protected int selectedIndex = 0;

    protected String calculationType = "";

    protected CurrencyIfc shippingCharge = null;

    protected CurrencyIfc itemsCharge;

    /**
     * Get the calculation type from the parameter value
     * 
     * @return the value of offline
     */
    public String getCalculationType()
    {
        return calculationType;
    }

    public void setCalculationType(String value)
    {
        calculationType = value;
    }

    /**
     * Get the calculated shipcharge
     * 
     * @return the value of offline
     */
    public CurrencyIfc getItemsShippingCharge()
    {
        return itemsCharge;
    }

    /**
     * Get the value of offline
     * 
     * @return the value of offline
     */
    public boolean isOffline()
    {
        return offline;
    }

    /**
     * Get the value of the Selected Shipping method. May return null if there
     * are no shipping methods in the list.
     * 
     * @return the value of methodsList
     */
    public ShippingMethodIfc getSelectedShipMethod()
    {
        if (selectedIndex > -1 && selectedIndex < methodsList.length)
        {
            return methodsList[selectedIndex];
        }
        return null;
    }
    
    /**
     * Get the value of the Selected Shipping method. Will return default 0 even if there
     * are no shipping methods in the list.
     * 
     * @return the value of methodsList
     */
    public int getSelectedShipMethodindex()
    {
        return selectedIndex;
    }

    /**
     * Get the value of the Special Instructions field
     * 
     * @return the value of instructions
     */
    public String getInstructions()
    {
        return instructions;
    }

    /**
     * Get the value of the shipVia field
     * 
     * @return the value of shipMethod
     */
    public ShippingMethodIfc[] getShipMethodsList()
    {
        return methodsList;
    }

    /**
     * Get the value of the ShippingCharge field. Will return zero if
     * {@link #isOffline()} is true or {@link #getSelectedShipMethod()} is null.
     * 
     * @return the value of shippingCharge
     */
    public CurrencyIfc getShippingCharge()
    {
        if ( offline )
        {
            shippingCharge = DomainGateway.getBaseCurrencyInstance("0.00");
        }
        else if (shippingCharge == null && getSelectedShipMethod() != null)
        {
            shippingCharge = getSelectedShipMethod().getCalculatedShippingCharge();
        }
        return shippingCharge;
    }

    /**
     * Sets the itemSum shipping charge
     * 
     * @param value Currency
     */
    public void setItemsShippingCharge(CurrencyIfc value)
    {
        itemsCharge = value;
    }

    /**
     * Sets offline status
     * 
     * @param offline boolean
     */
    public void setOffline(boolean value)
    {
        offline = value;
    }

    /**
     * Set the index of the selected ship method
     * 
     * @param int the index value
     */
    public void setSelectedShipMethod(int value)
    {
        selectedIndex = value;
    }

    /**
     * Sets the value of the Special Instructions field. Only sets the instruc-
     * tions onto the shipping method if it selected.
     * 
     * @param String instructions
     * @see #getSelectedShipMethod()
     */
    public void setInstructions(String value)
    {
        instructions = value;
        if (getSelectedShipMethod() != null)
        {
            getSelectedShipMethod().setShippingInstructions(instructions);
        }
    }

    /**
     * Sets the value of the shipVia field
     * 
     * @param String shipMethod
     */
    public void setShipMethodsList(ShippingMethodIfc value[])
    {
        methodsList = value;
    }

    /**
     * Sets the value of the ShippingCharge field. Only set the value if the
     * selection is non-null.
     * 
     * @param String shippingCharge
     * @see #getSelectedShipMethod()
     */
    public void setShippingCharge(CurrencyIfc value)
    {
        if (value == null)
        {
            shippingCharge = DomainGateway.getBaseCurrencyInstance("0.00");
        }
        else
        {
            shippingCharge = value;
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();
        buff.append("Class: ShippingMethodBeanModel Revision: " + revisionNumber + "\n");
        if (methodsList != null && methodsList.length > 0 && selectedIndex < methodsList.length && getSelectedShipMethod() != null
                && getSelectedShipMethod().getBaseShippingCharge() != null)
        {
            buff.append("ShippingCharge [" + getSelectedShipMethod().getBaseShippingCharge().toString() + "]\n");
        }
        else
        {
            buff.append("ShippingCharge [null]\n");
        }
        buff.append("Offline [" + offline + "]\n");
        buff.append("Special Instructions[" + instructions + "]\n");
        return (buff.toString());
    }
}
