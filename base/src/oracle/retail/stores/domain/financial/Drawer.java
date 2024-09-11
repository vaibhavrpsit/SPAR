/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/Drawer.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/07/13 22:33:39  cdb
 *   @scr 5970 in Services Impact Tracker database - removed hardcoding of class names
 *   in all getHardTotalsData methods.
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 10 2003 11:50:36   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.1   May 20 2003 07:23:52   jgs
 * Deprecated getHardtotalsData() and setHardtotalsData() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.0   Jun 03 2002 16:51:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:16   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:20:20   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Feb 2002 16:24:24   epd
 * translation
 * Resolution for POS SCR-954: Domain - Arts Translation
 * 
 *    Rev 1.1   03 Dec 2001 16:10:16   epd
 * changed drawer ID from int to String
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   12 Nov 2001 10:42:30   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

// java imports

/**
    A cash register might support multiple cash drawers.  It is with
    this in mind that we create a representation of such a drawer.                
*/    
public class Drawer implements DrawerIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8963236552783419083L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     *  The drawer ID
     */
    protected String drawerID = "000";

    /**
     *  The drawer status
     * @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc#DRAWER_STATUS_DESCRIPTORS
     */
    protected int drawerStatus = AbstractStatusEntity.DRAWER_STATUS_UNOCCUPIED;

    /**
     *  The occupying Till ID
     */
    protected String tillID = "";

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public Object clone()
    {
        Drawer newDrawer = new Drawer();
        newDrawer.drawerID = drawerID;
        newDrawer.drawerStatus = drawerStatus;
        newDrawer.tillID = tillID;
        return newDrawer;
    }

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof Drawer)
        {
            DrawerIfc d = (DrawerIfc)obj;
            
            if (drawerID.equals(d.getDrawerID()) &&
                drawerStatus == d.getDrawerStatus() &&
                tillID.equals(d.getOccupyingTillID()))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }



    /**
     *  Set the drawer ID
     *  @param drawer ID
     */
     public void setDrawerID(String drawerID)
     {
         this.drawerID = drawerID;
     }

     /**
      * Get the drawer ID
      * @return drawer ID
      */
     public String getDrawerID()
     {
         return drawerID;
     }

     /**
      * Set the drawer status
      * @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc#DRAWER_STATUS_DESCRIPTORS
      * @param drawer status
      * @param the ID of occupying till OR a blank ("") if setting drawer unoccupied.
      */
     public void setDrawerStatus(int drawerStatus, String tillID)
     {
         this.drawerStatus = drawerStatus;
         this.tillID = tillID;
     }

     /**
      * Set the drawer status
      * @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc#DRAWER_STATUS_DESCRIPTORS
      * @param drawer status
      */
     public void setDrawerStatus(int drawerStatus)
     {
         this.drawerStatus = drawerStatus;
     }
     
     /**
      * Get the drawer status
      * @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc#DRAWER_STATUS_DESCRIPTORS
      * @return drawer status
      */
     public int getDrawerStatus()
     {
         return drawerStatus;
     }

     /**
      * Set the occupying Till ID
      * @param ID of the occupying Till ID
      */
     public void setOccupyingTillID(String tillID)
     {
         this.tillID = tillID;
     }
     
     /**
      * Get the occupying Till ID
      * @return ID of the occupying Till ID
      */
     public String getOccupyingTillID()
     {
         return tillID;
     }

     //---------------------------------------------------------------------
     /**
         This method converts hard totals information to a comma delimited
         String. <P>
     **/
     //---------------------------------------------------------------------
     public void getHardTotalsData(HardTotalsBuilderIfc builder)
     {
         builder.appendStringObject(getClass().getName());
         builder.appendStringObject(drawerID);
         builder.appendInt(drawerStatus);
         builder.appendStringObject(tillID);
     }

     //---------------------------------------------------------------------
     /**
         This method populates this object from a comma delimited string.
         <P>
     **/
     //---------------------------------------------------------------------
         public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
     {
         drawerID       = builder.getStringObject();
         drawerStatus   = builder.getIntField();
         tillID         = builder.getStringObject();
     }

     //---------------------------------------------------------------------
     /**
         Method to default display string function. <P>
         @return String representation of object
     **/
     //---------------------------------------------------------------------
     public String toString()
     {
         // build result string
         StringBuffer strResult = new StringBuffer("Class:  Drawer (Revision " + getRevisionNumber() + ") @" + hashCode() + "\n");

         // Add Register specific attributes
         strResult.append("drawerID:            [").append(drawerID).append("]\n");
         strResult.append("drawerStatus:        [").append(drawerStatus).append("]\n");
         strResult.append("occupyingTillID:     [").append(tillID).append("]\n");
         
         // pass back result
         return(strResult.toString());
     }

    //---------------------------------------------------------------------
    /**
     Retrieves the source-code-control system revision number. <P>
     @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
     // return string
     return(revisionNumber);
    }                                  // end getRevisionNumber()

}

