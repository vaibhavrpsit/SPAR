/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/GeoCodeVO.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:30:49  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.1  2004/06/03 16:22:41  jdeleau
 *   @scr 2775 Initial Drop of send item tax support.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.io.Serializable;

/**
 * This class gets its contents of the GEO_TX_JUR table.  It contains
 * a GeoCode and its name.  
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class GeoCodeVO implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4474078661580339555L;

    /**
     * This is the geoCodeID
     */
    String geoCode;
    
    /**
     * This is the name of the geoCode.
     */
    String name = "";
    
    /**
     * Default constructor
     */
    public GeoCodeVO()
    {
    }
    
    /**
     * @return Returns the geoCode.
     */
    public String getGeoCode()
    {
        return geoCode;
    }
    
    /**
     * @param geoCode The geoCode to set.
     */
    public void setGeoCode(String geoCode)
    {
        this.geoCode = geoCode;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * String representation of the GeoCodeVO
     *  
     * @return String representation of GeoCode
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.getName();
    }
}
