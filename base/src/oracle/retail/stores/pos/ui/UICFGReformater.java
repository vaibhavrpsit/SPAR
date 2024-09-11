/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/UICFGReformater.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:36 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:20 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:52   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.util.ArrayList;
import java.util.StringTokenizer;

//--------------------------------------------------------------------------
/**
   This class takes the string output from the Foundation class 
   XMLUtility.buildXMLString and reformats to a more readable format.

   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
public class UICFGReformater
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Holds the tokenizer version of the UICFG text
    **/
    protected StringTokenizer st = null;

    /**
        Holds the formated version of the UICFG text
    **/
    protected StringBuffer buf   = null;
    
    /**
        Holds the formated version of the UICFG text
    **/
    protected String EOL         = null;

    /**
        Indent
    **/
    public final static String INDENT1 = "    ";
    /**
        Indent
    **/
    public final static String INDENT2 = "        ";
    /**
        Indent
    **/
    public final static String INDENT3 = "            ";
    /**
        Indent
    **/
    public final static String INDENT4 = "                ";

    //----------------------------------------------------------------------
    /**
     * Class constructor.
       @param uicfg a string containing the text of uicfg xml document.
    */
    public UICFGReformater(String uicfg)
    {
        EOL = System.getProperty("line.separator");
        String delimiters = " " + EOL;
        st = new StringTokenizer(uicfg, delimiters);
        buf = new StringBuffer(2 * uicfg.length());
    }

    //----------------------------------------------------------------------
    /**
     * Class constructor.
       @param uicfg a string containing the text of uicfg xml document.
    */
    public String getFormatedString()
    {
        formatFirstLines();

        while (st.hasMoreTokens()) 
        {
            String tag = st.nextToken();
            if (tag.equals("<OVERLAYSCREEN"))
            {
                formatOverlayscreen();
            }
            else
            if (tag.equals("</OVERLAYSCREEN>"))
            {
                buf.append(EOL);
                buf.append(INDENT1);
                buf.append("</OVERLAYSCREEN>");
                buf.append(EOL);
            }
            else
            if (tag.equals("<ASSIGNMENT"))
            {
                formatAssignment();
            }
            else
            if (tag.equals("</ASSIGNMENT>"))
            {
                buf.append(INDENT2);
                buf.append("</ASSIGNMENT>");
                buf.append(EOL);
            }
            else
            if (tag.equals("<BEANPROPERTY"))
            {
                formatProperty();
            }
            else
            if (tag.equals("<CONNECTION"))
            {
                formatConnection();
            }
            else
            if (tag.equals("<BEAN"))
            {
                formatBean();
            }
            else
            if (tag.equals("</BEAN>"))
            {
                buf.append(EOL);
                buf.append(INDENT1);
                buf.append("</BEAN>");
                buf.append(EOL);
            }
            else
            if (tag.equals("<BUTTON"))
            {
                formatButton();
            }
            else
            if (tag.equals("</SCREENS>"))
            {
                buf.append(EOL);
                buf.append("</SCREENS>");
            }
            else
            if (tag.equals("</BEANS>"))
            {
                buf.append(EOL);
                buf.append("</BEANS>");
            }
            else
            if (tag.equals("<SCREENS>"))
            {
                buf.append("<SCREENS>");
                buf.append(EOL);
            }
            else
            if (tag.equals("<BEANS>"))
            {
                buf.append("<BEANS>");
                buf.append(EOL);
            }
        }
        
        return buf.toString();
    }
    
    //----------------------------------------------------------------------
    /**
     * reasembles the xml statement and DOCTYPE of the document.
    */
    protected void formatFirstLines()
    {
        // The "xml" line
        buf.append(st.nextToken());
        buf.append(" ");
        buf.append(st.nextToken());
        buf.append(EOL);
        
        // The DOCTYPE line
        buf.append(st.nextToken());
        buf.append(" ");
        buf.append(st.nextToken());
        buf.append(" ");
        buf.append(st.nextToken());
        buf.append(" ");
        buf.append(st.nextToken());
        buf.append(EOL);
    }

    //----------------------------------------------------------------------
    /**
     * reasembles an OVERLAYSCREEN tag group.
    */
    protected void formatOverlayscreen()
    {
        buf.append(EOL);
        buf.append(INDENT1);
        buf.append("<OVERLAYSCREEN");
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(st.nextToken());
        buf.append(EOL);
    }
    
    //----------------------------------------------------------------------
    /**
     * reasembles an ASSIGNMENT tag group.
    */
    protected void formatAssignment()            
    {
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append("<ASSIGNMENT");
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        
        String line = st.nextToken();
        if (line.endsWith("/>"))
        {
            line = replace("/>", ">", line);
            buf.append(INDENT3);
            buf.append(line);
            buf.append(EOL);
            buf.append(INDENT2);
            buf.append("</ASSIGNMENT>");
            buf.append(EOL);
        }
        else
        {
            buf.append(INDENT3);
            buf.append(line);
            buf.append(EOL);
        }
    }

    //----------------------------------------------------------------------
    /**
     * reasembles an BEANPROPERTY tag group.
    */
    protected void formatProperty()
    {
        buf.append(INDENT3);
        buf.append("<BEANPROPERTY");
        buf.append(EOL);
        buf.append(INDENT4);
        buf.append(st.nextToken());
        buf.append(" ");
        
        boolean doNext = true;
        while(doNext)
        {
            String line = st.nextToken();
            buf.append(line);
            if (line.endsWith("/>"))
            {
                doNext = false;
                buf.append(EOL);
            }
            else
            {
                buf.append(" ");
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * reasembles an CONNECTION tag group.
    */
    protected void formatConnection()
    {
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append("<CONNECTION");
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
    }
    
    //----------------------------------------------------------------------
    /**
     * reasembles an BEAN tag group.
    */
    protected void formatBean()
    {
        buf.append(EOL);
        buf.append(INDENT1);
        buf.append("<BEAN");
        
        String beanClassName            = st.nextToken();
        String beanPackage              = st.nextToken();
        String configuratorClassName    = st.nextToken();
        String configuratorPackage      = st.nextToken();
        String specName                 = st.nextToken();
        boolean appendTag               = false;
        
        if (specName.endsWith("/>"))
        {
            specName  = replace("/>", "", specName);
            appendTag = true;
        }
        else
        {
            specName  = replace(">", "", specName);
        }
        
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(specName);
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(configuratorPackage);
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(configuratorClassName);
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(beanPackage);
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append(beanClassName);
        buf.append(">");
        
        if (appendTag)
        {
            buf.append(EOL);
            buf.append(INDENT1);
            buf.append("</BEAN>");
            buf.append(EOL);
        }
        else
        {
            buf.append(EOL);
        }
    }
    //----------------------------------------------------------------------
    /**
     * reasembles an BEAN tag group.
    */
    protected void formatButton()
    {
        buf.append(EOL);
        buf.append(INDENT2);
        buf.append("<BUTTON");
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT3);
        buf.append(st.nextToken());
        buf.append(EOL);
        buf.append(INDENT3);

        boolean doNext = true;
        while(doNext)
        {
            String line = st.nextToken();
            buf.append(line);
            if (line.endsWith("/>"))
            {
                doNext = false;
                buf.append(EOL);
            }
            else
            {
                buf.append(" ");
            }
        }
    }
    //--------------------------------------------------------------------------
    /**
        Replaces the first parameter with the second in the thrid parameter.
        
        @param current  String to be replaced
        @param replacement  replacement String to be replaced
        @param string  contians characters to be replaced.
        @return replacement 
        
    **/
    //--------------------------------------------------------------------------
    protected String replace(String current, String replacement, String string)
    {
        // Set up variables used in the loops.
        int startIndex = 0;
        int index = 0;
        int found = 0;
        boolean firstLoop = true;
        StringBuffer ret = new StringBuffer();
        ArrayList list = new ArrayList();
        
        // Create an list of all the segment
        while(index >= 0) 
        {
            index = string.indexOf(current, startIndex);
            if (index == -1)
            {
                index = string.length();
            }
            else
            {
                found++;
            }

            // Get this part of the string.
            String segment = string.substring(startIndex, index);
            list.add(segment);
                
            // Increment the start index
            startIndex = index + current.length();
                
            // If start index is past the end of the string,
            // quit.
            if (startIndex > string.length() - 1)
            {
                index = -1;
            }
        }
        
        // Put the string back together with the replacement.
        for(int i = 0; i < list.size(); i++)
        {
            ret.append((String)(list.get(i)));
            if (found > 0)
            {
                ret.append(replacement);
                found--;
            }
        }
    
        return ret.toString();
    }
}
