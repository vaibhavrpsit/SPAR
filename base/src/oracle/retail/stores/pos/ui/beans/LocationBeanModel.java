/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LocationBeanModel.java /main/15 2012/05/14 15:40:02 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/11/12 - check order customer null pointer
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/27/2006 5:37:44 PM  Charles D. Baker
 *
 *   Revision 1.6.2.1  2004/10/15 18:50:31  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.8  2004/10/12 18:53:52  mweis
 *   @scr 7012 Consolodate inventory UI model work under InventoryBeanModelIfc.
 *
 *   Revision 1.7  2004/10/12 16:38:51  mweis
 *   @scr 7012 Make common getters/setters for Inventory methods in preparation for Sale, Layaway, and Order sharing code.
 *
 *   Revision 1.6  2004/09/30 16:35:49  mweis
 *   @scr 7201 For Orders, ignore customer's middle name.
 *
 *   Revision 1.5  2004/09/27 18:27:40  mweis
 *   @scr 7012 Special Order restoration of "oder list" (and fixes for SCR 7243).
 *
 *   Revision 1.4  2004/06/29 22:03:30  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.1  2004/06/14 17:48:08  aachinfiev
 *   Inventory location/state related modifications
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:48:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:06   msg
 * Initial revision.
 *
 *    Rev 1.2   Jan 24 2002 16:15:02   dfh
 * updates to display the order location, cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//domain imports
import java.util.Vector;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//--------------------------------------------------------------------------
/**
 * This is the bean model used by the LocationBean. <P>
 *
 * @version $Revision: /main/15 $;
 * @see oracle.retail.stores.pos.ui.beans.LocationBean
 */
//--------------------------------------------------------------------------

public class LocationBeanModel extends POSBaseBeanModel implements InventoryBeanModelIfc
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/15 $";
    /** Constant for max name length */
    public static final int MAX_NAME_LENGTH = 50;
    /**  Order Id */
    protected String  orderIDField      = null;
    /**  Customer name */
    protected String  customerNameField = null;
    /**  Order date */
    protected EYSDate orderDateField    = null;
    /**  Order status */
    protected String  orderStatusField  = null;
    /**  Edit true or false */
    protected boolean editMode          = false;
    /** order locations from the reason codes list */
    protected CodeListIfc orderLocationsList = null;
    /**  Order location */
    protected String selectedLocation   = null;
    /** localized not availlable location */
    protected String localizedNotAvailbleLocation   = null;
    /** The Inventory location IDs */
    protected Vector invLocationIds     = new Vector();
    /** The Inventory location names */
    protected Vector invLocationNames   = new Vector();
//  TODO: inventory
//    /** The selected Inventory location ID */
//    protected Integer selectedInventoryLocationId = new Integer(InventoryLocationIfc.SALES_FLOOR);
    protected Integer selectedInventoryLocationId = new Integer(0);



    /**
     * No argument constructor.
     * Must call {@link #loadOrder(OrderIfc) loadOrder(order)} before this bean is useful.
     */
    public LocationBeanModel()
    {
    }

    /**
     * Constructor that uses Order to load critical elements of this bean.
     * @param order The order used to get critical information for this bean.
     * @see #loadOrder(OrderIfc)
     */
    public LocationBeanModel(OrderIfc order)
    {
        this();
        loadOrder(order);
    }

    /**
     * Populates this bean with critical information from the Order object.
     * @param order The order used to get critical information for this bean.
     */
    public void loadOrder(OrderIfc order)
    {
        if (order == null)
        {
            return;
        }
        orderIDField      = order.getOrderID();

        // SCR 7201: Use first and last name.
        CustomerIfc customer = order.getCustomer();
        if (customer != null)
        {
            customerNameField = customer.getCustomerName();
        }

        orderDateField    = order.getTimestampCreated();
        selectedLocation  = order.getStatus().getLocation();
        orderStatusField  = OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS[order.getOrderStatus()];
    }

    //----------------------------------------------------------------------
    //
    //  Getter and Setter methods.
    //  Order Location is display only, whereas Edit Location allows the
    //  operator to choose a location from a drop-down list. This model is used
    //  for both scenarios; display and edit.
    //
    //----------------------------------------------------------------------

    // order number
    public void setOrderID(String ID)
    {
        orderIDField = ID;
    }

    public String getOrderID()
    {
        return(orderIDField);
    }

    // customer name
    public void setCustomerName(String name)
    {
        customerNameField = name;
    }

    public String getCustomerName()
    {
        return(customerNameField);
    }


    // Order date. The date that the order began.
    public void setOrderDate(EYSDate value)
    {
        orderDateField = value;
    }

    public EYSDate getOrderDate()
    {
        return(orderDateField);
    }

    // order status - new, printed, partial, filled, completed, cancelled
    public void setStatus(String status)
    {
        orderStatusField = status;
    }

    public String getStatus()
    {
        return(orderStatusField);
    }

    //----------------------------------------------------------------------------
    /**
        Sets the edit mode flag, allowing the bean
        to have knowledge of whether display or edit mode is needed <p>
        @param boolean
    **/
    //----------------------------------------------------------------------------
    public void setEditMode(boolean value)
    {
        editMode = value;
    }

    public boolean getEditMode()
    {
        return(editMode);
    }

    //----------------------------------------------------------------------------
    /**
        Sets selected location. <P>
        @param selectedLocation as String
    **/
    //----------------------------------------------------------------------------
    public void setSelectedLocation(String location)
    {
        selectedLocation = location;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves selected location. <P>
        @return selectedLocation as String
    **/
    //----------------------------------------------------------------------------
    public String getSelectedLocation()
    {
        return selectedLocation;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves invLocationIds. <P>
        @return invLocationIds as Vector
    **/
    //----------------------------------------------------------------------------
    public Vector getInvLocationIds()
    {
        return(invLocationIds);
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves invLocationNames. <P>
        @return invLocationNames as Vector
    **/
    //----------------------------------------------------------------------------
    public Vector getInvLocationNames()
    {
        return(invLocationNames);
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves selectedLocationId. <P>
        @return selectedLocationId as Integer
    **/
    //----------------------------------------------------------------------------
    public Integer getSelectedInvLocationId()
    {
        return(selectedInventoryLocationId);
    }

    //----------------------------------------------------------------------
    /**
        Gets the order locations <P>
        @return CodeListIfc
    **/
    //----------------------------------------------------------------------
    public CodeListIfc getOrderLocationsList()
    {
        return orderLocationsList;
    }

    //----------------------------------------------------------------------
    /**
        Gets the localized NotAvailbleLocation. <P>
        @return string
    **/
    //----------------------------------------------------------------------
    public String getLocalizedNotAvailbleLocation()
    {
        return localizedNotAvailbleLocation;
    }

    //----------------------------------------------------------------------------
    /**
        Sets invLocationIds. <P>
        @param value as Vector
    **/
    //----------------------------------------------------------------------------
    public void setInvLocationIds(Vector value)
    {
        invLocationIds = value;
    }

    //----------------------------------------------------------------------------
    /**
        Sets invLocationNames. <P>
        @param value as Vector
    **/
    //----------------------------------------------------------------------------
    public void setInvLocationNames(Vector value)
    {
        invLocationNames = value;
    }

    //----------------------------------------------------------------------------
    /**
        Sets selectedLocationId. <P>
        @param value as Integer
    **/
    //----------------------------------------------------------------------------
    public void setSelectedInvLocationId(Integer value)
    {
        selectedInventoryLocationId = value;
    }

    //----------------------------------------------------------------------
    /**
        Sets the order Locations. <P>
        @param CodeListIfc
    **/
    //--------------------------------------------------------------------------
    public void setOrderLocationsList(CodeListIfc orderLocationsList)
    {
        this.orderLocationsList = orderLocationsList;
    }

    //----------------------------------------------------------------------
    /**
        Sets the localized NotAvailbleLocation. <P>
        @param string localizedNotAvailbleLocation
    **/
    //--------------------------------------------------------------------------
    public void setLocalizedNotAvailbleLocation(String localizedNotAvailbleLocation)
    {
        this.localizedNotAvailbleLocation = localizedNotAvailbleLocation;
    }

}
