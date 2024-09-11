/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/HardTotals.java /main/11 2012/12/14 09:46:20 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         4/3/2008 12:06:08 PM   Christian Greene 24966
 *       Base64 encode the employee ssn into the hardttotals so that it is not
 *        clear text.
 *  3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:59 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:17 PM  Robert Pearse   
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class defines the data written to and read from the hard totals device.
 * <P>
 * Initially, this consists of store status and register objects plus a
 * last-update time stamp.
 * 
 * @version $Revision: /main/11 $
 */
public class HardTotals implements HardTotalsIfc, HardTotalsDataIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 5455739213507654831L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/11 $";

    /**
     * store status data
     */
    protected StoreStatusIfc storeStatus = null;

    /**
     * register data
     */
    protected RegisterIfc register = null;

    /**
     * timestamp of last update to hard totals
     */
    protected EYSDate lastUpdate = null;

    /**
     * Constructs HardTotals object.
     */
    public HardTotals()
    {
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        HardTotals c = new HardTotals();

        // set clone attributes
        setCloneAttributes(c);

        // pass back Object
        return c;
    } // end clone()

    /**
     * Sets attributes in clone.
     * 
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(HardTotals newClass)
    {
        // set values
        if (storeStatus != null)
        {
            newClass.setStoreStatus((StoreStatusIfc)storeStatus.clone());
        }
        if (register != null)
        {
            newClass.setRegister((RegisterIfc)register.clone());
        }
        if (lastUpdate != null)
        {
            newClass.setLastUpdate((EYSDate)lastUpdate.clone());
        }
    } // end setCloneAttributes()

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean isEqual = false;
    	if (obj instanceof HardTotals)
    	{
    		HardTotals c = (HardTotals)obj; // downcast the input object
    		// compare all the attributes of HardTotals
    		if (Util.isObjectEqual(storeStatus, c.getStoreStatus())
    				&& Util.isObjectEqual(register, c.getRegister())
    				&& Util.isObjectEqual(lastUpdate, c.getLastUpdate()))
    		{
    			isEqual = true; // set the return code to true
    		}

    	}
    	return isEqual;
    } // end equals()

    /**
     * Retrieves store status data.
     * 
     * @return store status data
     */
    public StoreStatusIfc getStoreStatus()
    {
        return storeStatus;
    }

    /**
     * Sets store status data.
     * 
     * @param value store status data
     */
    public void setStoreStatus(StoreStatusIfc value)
    {
        storeStatus = value;
    }

    /**
     * Retrieves register data.
     * 
     * @return register data
     */
    public RegisterIfc getRegister()
    {
        return register;
    }

    /**
     * Sets register data.
     * 
     * @param value register data
     */
    public void setRegister(RegisterIfc value)
    {
        register = value;
    }

    /**
     * Retrieves timestamp of last update to hard totals.
     * 
     * @return timestamp of last update to hard totals
     */
    public EYSDate getLastUpdate()
    {
        return (lastUpdate);
    }

    /**
     * Sets timestamp of last update to hard totals.
     * 
     * @param value timestamp of last update to hard totals
     */
    public void setLastUpdate(EYSDate value)
    {
        lastUpdate = value;
    }

    /**
     * Sets timestamp of last update to hard totals to the current time.
     */
    public void setLastUpdate()
    {
        lastUpdate = DomainGateway.getFactory().getEYSDateInstance();
    }

    /**
     * This method converts hard totals information to a comma delimited String.
     * 
     * @return String
     */
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());

        if (lastUpdate == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            lastUpdate.getHardTotalsData(builder);
        }

        if (storeStatus == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            storeStatus.getHardTotalsData(builder);
        }

        if (register == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            register.getHardTotalsData(builder);
        }
    }

    /**
     * This method populates this object from a comma delimited string.
     * 
     * @param int offset of the current record
     * @param String String containing hard totals data.
     */
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        lastUpdate = (EYSDate)builder.getFieldAsClass();
        if (lastUpdate != null)
        {
            lastUpdate.setHardTotalsData(builder);
        }

        storeStatus = (StoreStatusIfc)builder.getFieldAsClass();
        if (storeStatus != null)
        {
            storeStatus.setHardTotalsData(builder);
        }

        register = (RegisterIfc)builder.getFieldAsClass();
        if (register != null)
        {
            register.setHardTotalsData(builder);
        }
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuffer buff = new StringBuffer("**** START HARD TOTALS **** START HARD TOTALS **** START HARD TOTALS\n");
        buff.append("Class:  HardTotals (Revision ");
        buff.append(getRevisionNumber());
        buff.append(") @");
        buff.append(hashCode());
        if (lastUpdate == null)
        {
            buff.append("\nlastUpdate:                         [null]");
        }
        else
        {
            buff.append("\nlastUpdate:                         ");
            buff.append(lastUpdate);
        }
        buff.append("\n\n");
        // add attributes to string
        if (storeStatus == null)
        {
            buff.append("storeStatus:                        [null]");
        }
        else
        {
            buff.append(storeStatus);
        }
        if (register == null)
        {
            buff.append("register:                           [null]");
        }
        else
        {
            buff.append(register);
        }
        buff.append("\n\n**** END HARD TOTALS **** END HARD TOTALS **** END HARD TOTALS");

        // pass back result
        return buff.toString();
    } // end toString()

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    /**
     * HardTotals main method.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>toString() output
     * </UL>
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        StoreStatus ss = new StoreStatus();
        Register r = new Register();
        EYSDate d = DomainGateway.getFactory().getEYSDateInstance();
        oracle.retail.stores.domain.store.Store s = new oracle.retail.stores.domain.store.Store("1275");
        ss.setStore(s);

        HardTotals c = new HardTotals();
        c.setStoreStatus(ss);
        c.setRegister(r);
        c.setLastUpdate(d);
        // output toString()
        System.out.println(c.toString());
    }

} // end class HardTotals
