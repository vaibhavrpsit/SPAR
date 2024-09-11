/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DeviceFilter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:44 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:56 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:34 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



import jpos.JposException;
import jpos.MSR;
import jpos.Scanner;
import jpos.events.DirectIOEvent;
import jpos.events.DirectIOListener;

import org.apache.log4j.Logger;

public class DeviceFilter implements DirectIOListener
{
    /** The logger to which log messages will be sent.    */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.DeviceFilter.class);

    private Object handler;
    private Scanner scanner;
    private MSR msr;
    private String header;
    private String terminator;
    private String data;
    private int headerLength;
    private int terminatorLength;
    private int mode;
    private int type;
    private int p = 0;
    private int track;
    private boolean enabled = false;

    public static final int WATCHING_FOR_HEADER = 0;
    public static final int PENDING_HEADER = 1;
    public static final int WATCHING_FOR_TERMINATOR = 2;
    public static final int PENDING_TERMINATOR = 3;

    public static final int DEFAULT = 0;
    public static final int WAIT = 1;
    public static final int CANCEL_HDR = 2;
    public static final int CANCEL_TRM = 6;
    public static final int DONE = 3;
    public static final int SEND = 4;
    public static final int CLAIM = 5;

    public static final int SCANNER_DATA = 0;
    public static final int MSR_TRACK1_DATA = 1;
    public static final int MSR_TRACK2_DATA = 2;
    public static final int MSR_TRACK3_DATA = 3;

    public static final int SCANNER_HEADER = 1;
    public static final int SCANNER_TERMINATOR = 2;
    public static final int MSR_TRACK1_HEADER = 10;
    public static final int MSR_TRACK1_TERMINATOR = 11;
    public static final int MSR_TRACK2_HEADER = 12;
    public static final int MSR_TRACK2_TERMINATOR = 13;
    public static final int MSR_TRACK3_HEADER = 14;
    public static final int MSR_TRACK3_TERMINATOR = 15;

    public static final int SCANNER_ENABLED = 99;
    public static final int MSR_ENABLED = 88;


    public DeviceFilter(Scanner scanner)
    {
        this.scanner = scanner;
        msr = null;
        mode = WATCHING_FOR_HEADER;
        data = "";
        track = 0;

        scanner.addDirectIOListener(this);

        try
        {
            scanner.directIO(SCANNER_HEADER,null,null);
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in DeviceFilter.");
        }
        try
        {
            scanner.directIO(SCANNER_TERMINATOR,null,null);
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in DeviceFilter.");
        }
        try
        {
            enabled = scanner.getDeviceEnabled();
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in DeviceFilter.");
        }

        headerLength = header.length() - 1;
        terminatorLength = terminator.length() - 1;
    }

    public DeviceFilter(MSR msr, int t)
    {
        this.msr = msr;
        scanner = null;
        mode = WATCHING_FOR_HEADER;
        data = "";
        track = t;
        int getHeaderCommand = 0;
        int getTerminatorCommand = 0;

        msr.addDirectIOListener(this);

        if (track == 1)
        {
            getHeaderCommand = MSR_TRACK1_HEADER;
            getTerminatorCommand = MSR_TRACK1_TERMINATOR;
        }
        if (track == 2)
        {
            getHeaderCommand = MSR_TRACK2_HEADER;
            getTerminatorCommand = MSR_TRACK2_TERMINATOR;
        }
        if (track == 3)
        {
            getHeaderCommand = MSR_TRACK3_HEADER;
            getTerminatorCommand = MSR_TRACK3_TERMINATOR;
        }

        try
        {
            msr.directIO(getHeaderCommand,null,null);
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in DeviceFilter: " + je + "");
        }
        try
        {
            msr.directIO(getTerminatorCommand,null,null);
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in DeviceFilter: " + je + "");
        }
        try
        {
            enabled = msr.getDeviceEnabled();
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in DeviceFilter: " + je + "");
        }


        headerLength = header.length() - 1;
        terminatorLength = terminator.length() - 1;

    }


    public Object getHandler()
    {
        return handler;
    }

    public int getType()
    {
        return type;
    }

    public void addChar(char c)
    {
        data = data + c;
    }

    public void clearData()
    {
        data = "";
    }

    public String getData()
    {
        return data;
    }
    public void sendData()
    {
        int[] i = {};
        try
        {
            if(scanner != null)
            {
                logger.error( "DeviceFilter: send data to scanner driver = " + getData() + "");
                scanner.directIO(SCANNER_DATA,i,getData());
            }
            else if(msr != null)
            {
                logger.error(
                             "DeviceFilter: send data to msr driver = " + getData() + " track = " + new Integer(track) + "");
                msr.directIO(track,i,getData());
            }
        }
        catch(JposException je)
        {
            logger.error( "DeviceFilter: Jpos exception in sendData.");
        }
    }

    public int nextChar(char c)
    {
        int rc = DEFAULT;
        // Per Paul Habermehl, device should eat characters regardless whether
        // it is enabled or disabled.  Otherwise, the input source will be
        // treated as keyboard.  Therefore, the check for enabled that was
        // previously here has been removed.
        switch (mode){
            case WATCHING_FOR_HEADER:
            p = 0;
            if(c == header.charAt(p)){
                mode = PENDING_HEADER;
                rc = WAIT;
            }
            break;
            case PENDING_HEADER:
            p++;
            if(c == header.charAt(p)){
                if(p == headerLength){
                    mode = WATCHING_FOR_TERMINATOR;
                    rc = CLAIM;
                }else{
                    mode = PENDING_HEADER;
                    rc = WAIT;
                }
            }else{
                mode = WATCHING_FOR_HEADER;
                rc = CANCEL_HDR;
            }
            break;
            case WATCHING_FOR_TERMINATOR:
            p = 0;
            rc = SEND;
            if(c == terminator.charAt(p)){
                if(p == terminatorLength){
                    mode = WATCHING_FOR_HEADER;
                    rc = DONE;
                    sendData();
                    clearData();
                }else{
                    mode = PENDING_TERMINATOR;
                    rc = WAIT;
                }
            }else{
                addChar(c);
            }
            break;
            case PENDING_TERMINATOR:
            p++;
            if(c == terminator.charAt(p)){
                if(p == terminatorLength){
                    mode = WATCHING_FOR_HEADER;
                    rc = DONE;
                    sendData();
                    clearData();
                }else{
                    mode = PENDING_TERMINATOR;
                    rc = WAIT;
                }
            }else{
                mode = WATCHING_FOR_TERMINATOR;
                rc = CANCEL_TRM;
            }
            break;
        }
        return(rc);
    }

    public void directIOOccurred(DirectIOEvent dioe)
    {
        if(dioe.getData() == SCANNER_HEADER)
        {
            header = (String)dioe.getObject();
        }
        if(dioe.getData() == SCANNER_TERMINATOR)
        {
            terminator = (String)dioe.getObject();
        }
        if(dioe.getData() == SCANNER_ENABLED)
        {
            Boolean b = (Boolean)dioe.getObject();
            enabled = b.booleanValue();
            logger.error(
                         "Device Filter enabled = " + new Boolean(enabled) + "");
        }
        if(track == 1 && dioe.getData() == MSR_TRACK1_HEADER)
        {
            header = (String)dioe.getObject();
        }
        if(track == 1 && dioe.getData() == MSR_TRACK1_TERMINATOR)
        {
            terminator = (String)dioe.getObject();
        }
        if(track == 2 && dioe.getData() == MSR_TRACK2_HEADER)
        {
            header = (String)dioe.getObject();
        }
        if(track == 2 && dioe.getData() == MSR_TRACK2_TERMINATOR)
        {
            terminator = (String)dioe.getObject();
        }
        if(track == 3 && dioe.getData() == MSR_TRACK3_HEADER)
        {
            header = (String)dioe.getObject();
        }
        if(track == 3 && dioe.getData() == MSR_TRACK3_TERMINATOR)
        {
            terminator = (String)dioe.getObject();
        }
        if(dioe.getData() == MSR_ENABLED)
        {
            Boolean b = (Boolean)dioe.getObject();
            enabled = b.booleanValue();
        }

    }
}
