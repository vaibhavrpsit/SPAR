/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/StoreStatusAndTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:01:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:21:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// java imports
// foundation imports
import oracle.retail.stores.domain.store.StoreIfc;

//------------------------------------------------------------------------------ 
/**
    This class represents the status for a store.  It is to be differentiated
    from the {@link oracle.retail.stores.domain.store.Store} class, which 
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4556490211006817456L;

    represents the physical store and its attributes. <P>    
    The store status class allows an application to set the sign-on operator,
    status, open and close time and business day.  Methods are also provided
    to maintain the financial totals for the store. <P>
    In a typical EYS POS implementation, the store status class would be used
    primarily as a vehicle for database maintenance.  The store status would
    probably not be maintained in cargo through the entire application. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------ 
public class StoreStatusAndTotals extends AbstractFinancialEntity implements StoreStatusAndTotalsIfc
{                                       // begin class StoreStatusAndTotals

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        physical store object
    **/
    protected StoreIfc store = null;

    //---------------------------------------------------------------------
    /**
        Constructs StoreStatusAndTotals object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public StoreStatusAndTotals()
    {                                   // begin StoreStatusAndTotals()

    }                                  // end StoreStatusAndTotals()
    
    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        StoreStatusAndTotalsIfc st = new StoreStatusAndTotals();
                
                // set clone attributes
                setCloneAttributes(st);

        // pass back Object
        return((Object) st);
    }                                   // end clone()
    
        //---------------------------------------------------------------------
        /**
                Sets attributes in clone. <P>
        @param newClass new instance of class
        **/
        //--------------------------------------------------------------------- 
        protected void setCloneAttributes(StoreStatusAndTotalsIfc newClass)
        {                                   // begin setCloneAttributes()
        super.setCloneAttributes((AbstractFinancialEntity) newClass);
        if (store != null)
        {
            newClass.setStore((StoreIfc) store.clone());
        }
        }                                   // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Sets reference to store interface. <P>
        @param value reference to store interface
    **/
    //--------------------------------------------------------------------- 
    public void setStore(StoreIfc value)
    {                                   // begin setStore()
        store = value;
    }                                   // end setStore()

    //---------------------------------------------------------------------
    /**
        Retrieves reference to store interface. <P>
        @return reference to store interface
    **/
    //--------------------------------------------------------------------- 
    public StoreIfc getStore()
    {                                   // begin getStore()
        return(store);
    }                                   // end getStore()
    
    //---------------------------------------------------------------------
    /**
        Copy StoreStatusIfc values to this object. <P>
        @param object hold store staus info
    **/
    //--------------------------------------------------------------------- 
    public void copyStoreStatus(StoreStatusIfc value)
    {
        setStore(value.getStore());
        setStatus(value.getStatus());
        setPreviousStatus(value.getPreviousStatus());
        setLastStatusChangeTime(value.getLastStatusChangeTime());
        setSignOnOperator(value.getSignOnOperator());
        setSignOffOperator(value.getSignOffOperator());
        setOpenTime(value.getOpenTime());
        setCloseTime(value.getCloseTime());
        setBusinessDate(value.getBusinessDate());
    }
    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  StoreStatusAndTotals (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n" +
                     attributesToString() + "\n";                              
        if (store == null)
        {
            strResult += "store:                                  [null]\n";
        }
        else
        {
            strResult += "Sub" + store.toString() + "\n";
        }
        // pass back result
        return(strResult);
    }                                  // end toString()

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

    //---------------------------------------------------------------------
    /**
        StoreStatusAndTotals main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        StoreStatusAndTotalsIfc clsStoreStatusAndTotals = new StoreStatusAndTotals();
        // output toString()
        System.out.println(clsStoreStatusAndTotals.toString());
    }                                  // end main()
    
}                                      // end class StoreStatusAndTotals


