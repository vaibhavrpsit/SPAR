/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ServiceSelectBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.alert.AlertListIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
 * This is the bean model that is used by the ServiceSelectBean. <P>
 * This bean model is used to access arrays of data pertaining to
 * Status of pickups and E-Mail communications.
 * @see oracle.retail.stores.pos.ui.beans.ServiceSelectBean
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 * @deprecated as of release 5.0.0
 */
//--------------------------------------------------------------------------
public class ServiceSelectBeanModel extends POSBaseBeanModel implements UIModelIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8427667261312712091L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "ServiceSelectBeanModel";

    /**
        alert entry
    **/
    protected AlertListIfc alertList = null;
    /**
        selected entry
    **/
    protected AlertEntryIfc selectedEntry = null;

    //----------------------------------------------------------------------------
    /**
        ServiceSelectBeanModel constructor comment.
    **/
    //----------------------------------------------------------------------------
    public ServiceSelectBeanModel()
    {
    }
    //----------------------------------------------------------------------------
    /**
        ServiceSelectBeanModel constructor comment.
    **/
    //----------------------------------------------------------------------------
    public ServiceSelectBeanModel(AlertListIfc list)
    {
        alertList = list;
    }
    //----------------------------------------------------------------------------
    /**
        Gets the alertEntry property value.
        @return AlertEntryIfc[] the alertEntry property value array.
        @see #setAlertEntry
    **/
    //----------------------------------------------------------------------------
    public AlertListIfc getAlertList()
    {
        return alertList;
    }

    //----------------------------------------------------------------------------
    /**
    * Sets the alertEntry property value.
    * @param serviceAlertEntry the new value for the property.
    * @see #getAlertEntry
    */
    //----------------------------------------------------------------------------
    public void setAlertList(AlertListIfc alertList)
    {
        this.alertList = alertList;
    }

    //----------------------------------------------------------------------------
    /**
        Gets the selectedEntry property value.
        @return AlertEntryIfc the alertEntry property value.
        @see #setSelectedEntry
    **/
    //----------------------------------------------------------------------------
    public AlertEntryIfc getSelectedEntry()
    {
        return selectedEntry;
    }

    //----------------------------------------------------------------------------
    /**
    * Sets the selectedEntry property value.
    * @param selectedEntry the new value for the property.
    * @see #getSelectedEntry
    */
    //----------------------------------------------------------------------------
    public void setSelectedEntry(AlertEntryIfc serviceEntry)
    {
        // copy the selected service alert entry obtained from the
        // site into this bean model's selected entry value
        selectedEntry = serviceEntry;
    }

    //---------------------------------------------------------------------
        /**
                Method to default display string function. <P>
            @return String representation of object
        **/
        //---------------------------------------------------------------------
        public String toString()
        {
                // result string
                String strResult = new String("Class: " + CLASSNAME + " (Revision "
                                                          + getRevisionNumber() + ")" + hashCode());
                // pass back result
                return(strResult);
        }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
