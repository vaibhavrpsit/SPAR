/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/HardTotalsSerialized.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:17 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:54  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   May 20 2003 08:39:12   jgs
 * Initial revision.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class defines the data written to and read from the hard totals
     device. This consists of store status and register objects plus a 
     last-update time stamp.
     <P>
     This class allows the hard totals to be serialized and compressed
     with the participation of the domain objects themselves.
     
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class HardTotalsSerialized extends HardTotals 
implements HardTotalsIfc, HardTotalsDataIfc
{                                       // begin class HardTotals
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5555123954361542105L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        store status data
    **/
    protected StoreStatusIfc storeStatus = null;
    /**
        register data
    **/
    protected RegisterIfc register = null;
    /**
        timestamp of last update to hard totals
    **/
    protected EYSDate lastUpdate = null;

    //---------------------------------------------------------------------
    /**
        Constructs HardTotalsSerialized object. <P>
    **/
    //---------------------------------------------------------------------
    public HardTotalsSerialized()
    {
    }

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        HardTotalsSerialized c = new HardTotalsSerialized();
        
        // set clone attributes
        setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = false;
        
        if (obj instanceof HardTotalsSerialized)
        {
            HardTotalsSerialized c = (HardTotalsSerialized) obj;
            // compare all the attributes of HardTotals
            if (Util.isObjectEqual(storeStatus, c.getStoreStatus()) &&
                Util.isObjectEqual(register, c.getRegister()) &&
                Util.isObjectEqual(lastUpdate, c.getLastUpdate()))
            {
                isEqual = true;             // set the return code to true
            }
        }

        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves store status data. <P>
        @return store status data
    **/
    //----------------------------------------------------------------------------
    public StoreStatusIfc getStoreStatus()
    {                                   // begin getStoreStatus()
        return(storeStatus);
    }                                   // end getStoreStatus()

    //----------------------------------------------------------------------------
    /**
        Sets store status data. <P>
        @param value  store status data
    **/
    //----------------------------------------------------------------------------
    public void setStoreStatus(StoreStatusIfc value)
    {                                   // begin setStoreStatus()
        storeStatus = value;
    }                                   // end setStoreStatus()

    //----------------------------------------------------------------------------
    /**
        Retrieves register data. <P>
        @return register data
    **/
    //----------------------------------------------------------------------------
    public RegisterIfc getRegister()
    {                                   // begin getRegister()
        return(register);
    }                                   // end getRegister()

    //----------------------------------------------------------------------------
    /**
        Sets register data. <P>
        @param value  register data
    **/
    //----------------------------------------------------------------------------
    public void setRegister(RegisterIfc value)
    {                                   // begin setRegister()
        register = value;
    }                                   // end setRegister()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp of last update to hard totals. <P>
        @return timestamp of last update to hard totals
    **/
    //----------------------------------------------------------------------------
    public EYSDate getLastUpdate()
    {                                   // begin getLastUpdate()
        return(lastUpdate);
    }                                   // end getLastUpdate()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of last update to hard totals. <P>
        @param value  timestamp of last update to hard totals
    **/
    //----------------------------------------------------------------------------
    public void setLastUpdate(EYSDate value)
    {                                   // begin setLastUpdate()
        lastUpdate = value;
    }                                   // end setLastUpdate()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of last update to hard totals to the current time. <P>
    **/
    //----------------------------------------------------------------------------
    public void setLastUpdate()
    {                                   // begin setLastUpdate()
        lastUpdate = DomainGateway.getFactory().getEYSDateInstance();
    }                                   // end setLastUpdate()

    //---------------------------------------------------------------------
    /**
        This method converts hard totals information using the builder
        String. <P>
        @return String
    **/
    //--------------------------------------------------------------------- 
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        ((HardTotalsSerializedBuilder)builder).setHardTotals(this);
    }

    //---------------------------------------------------------------------
    /**
        This method populates this object from the builder.
        <P>
        @param int      offset of the current record
        @param String   String containing hard totals data.
    **/
    //--------------------------------------------------------------------- 
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        // The builder has already done all most of the work.  This is
        // just a little clean up.
        ((HardTotalsSerializedBuilder)builder).setHardTotals(null);
    }
    
    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = "**** START HARD TOTALS **** START HARD TOTALS **** START HARD TOTALS\n";
        strResult += "Class:  HardTotalsSerialized (Revision " + getRevisionNumber() + ") @" + hashCode();
        if (lastUpdate == null)
        {
            strResult += "\nlastUpdate:                         [null]";
        } 
        else
        {
            strResult += "\nlastUpdate:                         " + lastUpdate.toString();
        }
        strResult += "\n\n";
        // add attributes to string
        if (storeStatus == null)
        {
            strResult += "storeStatus:                        [null]";
        } 
        else
        {
            strResult += storeStatus.toString();
        }
        if (register == null)
        {
            strResult += "register:                           [null]";
        } 
        else
        {
            strResult += register.toString();
        }
        strResult += "\n\n**** END HARD TOTALS **** END HARD TOTALS **** END HARD TOTALS";

        // pass back result
        return(strResult);
    }                                   // end toString()

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
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        HardTotalsSerialized main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        StoreStatus ss = new StoreStatus();
        Register r = new Register();
        EYSDate d = DomainGateway.getFactory().getEYSDateInstance();
        oracle.retail.stores.domain.store.Store s = 
            new oracle.retail.stores.domain.store.Store();
        s.setStoreID("12345");
        ss.setStore(s);
        
        HardTotalsSerialized c = new HardTotalsSerialized();
        c.setStoreStatus(ss);
        c.setRegister(r);
        c.setLastUpdate(d);
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class HardTotalsSerialized
