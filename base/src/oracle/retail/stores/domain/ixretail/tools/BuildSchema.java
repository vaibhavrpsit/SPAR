/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/tools/BuildSchema.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:33  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:59:40   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:54   msg
 * Initial revision.
 * 
 *    Rev 1.2   May 06 2002 19:37:58   mpm
 * Tweaked XML-creation code.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   May 02 2002 08:55:50   mpm
 * Added capability to create code fragment for XML-to-SQL java code.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 01 2002 18:12:34   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.tools;
// java imports
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This class takes the non-static attributes of an object and writes out the schema
    element entries, constants values entries and element-creation Java code. <P>
    To use this class, invoke the class followed by the domain object name.
    For example, <code>java oracle.retail.stores.domain.ixretail.tools.BuildSchema oracle.retail.stores.domain.financial.FinancialTotals</code><P>
    The schema fragment is contained in <code>schema.txt</code>, the
    IXRetailConstantsIfc constants fragment is contained in <code>constants.txt</code>,
    the XML creation code fragment is contained in <code>create.txt</code>, and
    the XML-to-SQL code fragment is contained in <code>tosql.txt</code>.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class BuildSchema
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        schema file name
    **/
    public static final String SCHEMA_FILE_NAME = "schema.txt";
    /**
        constants file name
    **/
    public static final String CONSTANTS_FILE_NAME = "constants.txt";
    /**
        file name for java fragment which contains XML creation code
    **/
    public static final String CREATE_XML_FILE_NAME = "create.txt";
    /**
        file name for java fragment which contains XML-to-SQL code
    **/
    public static final String TO_SQL_FILE_NAME = "tosql.txt";
    /**
        class name
    **/
    protected String className = "";
    /**
        members
    **/
    protected ArrayList members = new ArrayList();
    /**
        output writer
    **/
    protected BufferedWriter writer = null;
    /**
        spaces buffer
    **/
    public static final String SPACES = "                                                                      ";
    /**
        offset for constants definition
    **/
    public static final int CONSTANTS_OFFSET = 69;

    //----------------------------------------------------------------------------
    /**
        Constructs BuildSchema object. <P>
        @param classNameParameter class name parameter
    **/
    //----------------------------------------------------------------------------
    public BuildSchema(String classNameParameter)
    {                                   // begin BuildSchema()
        className = classNameParameter;
    }                                   // end BuildSchema()

    //---------------------------------------------------------------------
    /**
       Parses attributes of input class. <P>
    **/
    //---------------------------------------------------------------------
    public void parseAttributes()
    {                                   // begin parseAttributes()
        try
        {
            Class useClass = Class.forName(className);
            Field[] fields = useClass.getDeclaredFields();
            MemberHolder mh = null;
            StringBuffer varName = null;
            for (int i = 0; i < fields.length; i++)
            {
                // exclude static members
                if (!Modifier.isStatic(fields[i].getModifiers()))
                {
                    mh = new MemberHolder();
                    mh.dataType = fields[i].getType().getName();
                    mh.variableName = fields[i].getName();
                    mh.attributeName = makeAttributeName(mh.variableName);
                    members.add(mh);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(Util.throwableToString(e));
            System.exit(0);
        }
    }                                   // end parseAttributes()

    //---------------------------------------------------------------------
    /**
       Builds schema entries list. <P>
    **/
    //---------------------------------------------------------------------
    public void writeSchemaEntries()
    {                                   // begin writeSchemaEntries()
        boolean bOk = openOutputFile(SCHEMA_FILE_NAME);
        if (bOk)
        {
            Iterator iter = members.iterator();
            MemberHolder mh = null;
            while (iter.hasNext())
            {
                mh = (MemberHolder) iter.next();
                String attributeType = "";
                if (mh.dataType.endsWith("CurrencyIfc") ||
                    mh.dataType.endsWith("BigDecimal"))
                {
                    mh.attributeType = "decimal";
                }
                else if (mh.dataType.endsWith("EYSDate"))
                {
                    mh.attributeType = "dateTime";
                }
                else if (mh.dataType.equals("int"))
                {
                    mh.attributeType = "integer";
                }
                else if (mh.dataType.equals("boolean"))
                {
                    mh.attributeType = "boolean";
                }
                // bypass arrays
                else if (mh.dataType.endsWith("[]") ||
                         mh.variableName.equalsIgnoreCase("RevisionNumber") ||
                         mh.dataType.startsWith("Array"))
                {
                    mh.noAttribute = true;
                }
                // default to string
                else
                {
                    mh.attributeType = "string";
                }

                if (mh.noAttribute)
                {
                    System.out.println("No entries generated for " +
                                       mh.variableName + " (" +
                                       mh.dataType + ").");
                }
                else
                {
                    write(getElementString(mh));
                }
            }
        }
    }                                   // end writeSchemaEntries()

    //---------------------------------------------------------------------
    /**
       Writes out constant names for attributes.
    **/
    //---------------------------------------------------------------------
    public void writeConstants()
    {                                   // begin writeConstants()
        boolean bOk = openOutputFile(CONSTANTS_FILE_NAME);
        if (bOk)
        {
            Iterator iter = members.iterator();
            MemberHolder mh = null;
            while (iter.hasNext())
            {
                mh = (MemberHolder) iter.next();
                if (!mh.noAttribute)
                {
                    write(getConstantsString(mh));
                }
            }
        }

    }                                   // end writeConstants()

    //---------------------------------------------------------------------
    /**
       Writes out java code to populate the attributes. <P>
    **/
    //---------------------------------------------------------------------
    public void writeTextNodeElementCode()
    {                                   // begin writeTextNodeElementCode()
        boolean bOk = openOutputFile(CREATE_XML_FILE_NAME);
        if (bOk)
        {
            Iterator iter = members.iterator();
            MemberHolder mh = null;
            while (iter.hasNext())
            {
                mh = (MemberHolder) iter.next();
                if (!mh.noAttribute)
                {
                    write(getTextNodeElementCode(mh));
                }
            }
        }

    }                                   // end writeTextNodeElementCode()

    //---------------------------------------------------------------------
    /**
       Writes out java code for conversion back to SQL. <P>
    **/
    //---------------------------------------------------------------------
    public void writeToSQLCode()
    {                                   // begin writeToSQLCode()
        boolean bOk = openOutputFile(TO_SQL_FILE_NAME);
        if (bOk)
        {
            Iterator iter = members.iterator();
            MemberHolder mh = null;
            while (iter.hasNext())
            {
                mh = (MemberHolder) iter.next();
                if (!mh.noAttribute)
                {
                    write(getToSQLCode(mh));
                }
            }
        }

    }                                   // end writeToSQLCode()

    //---------------------------------------------------------------------
    /**
       Returns schema element name string for specified member.
       @param mh MemberHolder object
       @return schema element entry
    **/
    //---------------------------------------------------------------------
    protected String getElementString(MemberHolder mh)
    {                                   // begin getElementString()
        StringBuffer buf =
          new StringBuffer("            <xs:element name=\"")
            .append(mh.attributeName)
            .append("\" type =\"xs:")
            .append(mh.attributeType)
            .append("\" minOccurs=\"0\"/>")
            .append(Util.EOL);
        return(buf.toString());
    }                                   // end getElementString()


    //---------------------------------------------------------------------
    /**
       Returns constants string for specified member.
       @param mh MemberHolder object
       @return constants string
    **/
    //---------------------------------------------------------------------
    protected String getConstantsString(MemberHolder mh)
    {                                   // begin getConstantsString()
        StringBuffer buffer =
          new StringBuffer("    public static final String ELEMENT_")
            .append(convertToAllCapsUnderbarDelimited(mh.variableName));
        int spacesToAdd = CONSTANTS_OFFSET - 2 - buffer.length();
        if (spacesToAdd > 0)
        {
            buffer.append(SPACES.substring(0, spacesToAdd));
        }
        buffer.append(" = \"")
            .append(mh.attributeName)
            .append("\";")
            .append(Util.EOL);
        return(buffer.toString());
    }                                   // end getConstantsString()

    //---------------------------------------------------------------------
    /**
       Converts an attribute name to all caps, with underbars between
       words. <P>
       @param attributeName attribute name
       @return allCapsName all-caps name
    **/
    //---------------------------------------------------------------------
    protected String convertToAllCapsUnderbarDelimited(String attributeName)
    {                                   // begin convertToAllCapsUnderbarDelimited()
        StringBuffer allCapsName = new StringBuffer();
        char useCharacter;
        for (int i = 0; i < attributeName.length(); i++)
        {
            useCharacter = attributeName.charAt(i);
            if (Character.isUpperCase(useCharacter) &&
                i > 0)
            {
                allCapsName.append("_")
                           .append(useCharacter);
            }
            else
            {
                allCapsName.append(Character.toUpperCase(useCharacter));
            }
        }

        return(allCapsName.toString());
    }                                   // end convertToAllCapsUnderbarDelimited()

    //---------------------------------------------------------------------
    /**
       Returns java code string for specified attribute.
       @param mh MemberHolder object
       @return java code string
    **/
    //---------------------------------------------------------------------
    protected String getTextNodeElementCode(MemberHolder mh)
    {                                   // begin getTextNodeElementCode()
        String functionName = "createTextNodeElement";
        if (mh.dataType.startsWith("date"))
        {
            functionName = "createDateTextNodeElement";
        }
        StringBuffer codeString =
          new StringBuffer("        ")
            .append(functionName)
            .append(Util.EOL)
            .append("          (IXRetailConstantsIfc.ELEMENT_")
            .append(convertToAllCapsUnderbarDelimited(mh.attributeName))
            .append(",")
            .append(Util.EOL)
            .append("           className.get")
            .append(mh.attributeName)
            .append("(),")
            .append(Util.EOL)
            .append("           elementName);")
            .append(Util.EOL).append(Util.EOL);

        return(codeString.toString());
    }                                   // end getTextNodeElementCode()

    //---------------------------------------------------------------------
    /**
       Returns to-SQL java code string for specified attribute.
       @param mh MemberHolder object
       @return java code string
    **/
    //---------------------------------------------------------------------
    protected String getToSQLCode(MemberHolder mh)
    {                                   // begin getToSQLCode()
        String functionName = "getValueFromParent";
        if (mh.dataType.endsWith("String"))
        {
            functionName = "getQuotedValueFromParent";
        }
        StringBuffer elementName =
          new StringBuffer("ELEMENT_")
            .append(convertToAllCapsUnderbarDelimited(mh.attributeName));
        StringBuffer codeString =
          new StringBuffer("    //---------------------------------------------------------------------")
            .append(Util.EOL)
            .append("    /**")
            .append(Util.EOL)
            .append("       Returns the value of the ")
            .append(elementName)
            .append(" element")
            .append(Util.EOL)
            .append("       @param el parent element")
            .append(Util.EOL)
            .append("       @return value of the element")
            .append(Util.EOL)
            .append("    **/")
            .append(Util.EOL)
            .append("    //---------------------------------------------------------------------")
            .append(Util.EOL)
            .append("    protected String get")
            .append(mh.attributeName)
            .append("(Element el)")
            .append(Util.EOL)
            .append("    {")
            .append(Util.EOL)
            .append("        return(")
            .append(functionName)
            .append("(el, ")
            .append(elementName)
            .append("));")
            .append(Util.EOL)
            .append("    }")
            .append(Util.EOL)
            .append(Util.EOL);
        return(codeString.toString());
    }                                   // end getToSQLCode()

    //---------------------------------------------------------------------
    /**
       Makes attribute name from variable name (by replacing first character
       with uppercase letter).
       @param variableName variable name string
       @return attributeName attribute name string
    **/
    //---------------------------------------------------------------------
    public String makeAttributeName(String variableName)
    {                                   // begin makeAttributeName()
        StringBuffer variableBuffer = new StringBuffer(variableName);
        if (variableBuffer.length() > 0)
        {
            variableBuffer.setCharAt
              (0,
               Character.toUpperCase(variableBuffer.charAt(0)));
        }
        return(variableBuffer.toString());
    }                                   // end makeAttributeName()

    //---------------------------------------------------------------------
    /**
        Opens output file. <P>
        @param fileName name of output file
        @return flag indicating if file was opened
    **/
    //---------------------------------------------------------------------
    public boolean openOutputFile(String fileName)
    {                                   // begin openOutputFile()
        boolean bOk = false;
        try
        {                               // begin file-open try block
            if (writer != null)
            {
                writer.close();
            }
            // open output stream
            writer = new BufferedWriter
              (new OutputStreamWriter(new FileOutputStream(fileName)));
            bOk = true;
        }                               // end file-open try block
        catch (Exception e)
        {
            System.err.println("Failed to open output file: " + fileName);
            System.err.println(e);
            e.printStackTrace();
            bOk = false;
        }
        return(bOk);
    }                                   // end openOutputFile()

     //---------------------------------------------------------------------
    /**
        Writes a line to the output file. <P>
        @param buff string to be written to output stream
        @return flag indicating write succeeded
    **/
    //---------------------------------------------------------------------
    protected boolean write(String buff)
    {                                   // begin write()
        boolean bOk = false;
        // write line to output and flush
        try
        {
            writer.write(buff, 0, buff.length());
            writer.flush();
            bOk = true;
        }
        catch(Exception e)
        {
            System.out.println("Failed To Write: " + buff );
            System.err.println(e);
            e.printStackTrace();
            bOk = false;
        }
        return(bOk);
    }                                   // end write()

    //---------------------------------------------------------------------
    /**
        Closes input, output streams. <P>
    **/
    //---------------------------------------------------------------------
    protected void close()
    {                                   // begin close()
        try
        {
            writer.close();
        }
        catch(Exception e)
        {
            // ignore any exceptions
        }
    }                                   // end close()

   //----------------------------------------------------------------------
    /**
        protected inner class for data member
    **/
    //----------------------------------------------------------------------
    protected class MemberHolder
    {                                   // begin class MemberHolder
        /**
            data type
        **/
        public String dataType = "";
        /**
            variable name
        **/
        public String variableName = "";
        /**
            attribute name
        **/
        public String attributeName = "";
        /**
            attribute type
        **/
        public String attributeType = "";
        /**
            attribute not provided (for types from which attributes cannot be made
        **/
        public boolean noAttribute = false;
    }                                   // end class MemberHolder

    //----------------------------------------------------------------------
    /**
       Main method. <P>
       @param args[]  command-line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        if (args.length < 1)
        {
            System.out.println("Class name parameter is required.");
        }
        else
        {
            BuildSchema c = new BuildSchema(args[0]);
            c.parseAttributes();
            boolean bOk = c.openOutputFile(SCHEMA_FILE_NAME);
            if (bOk)
            {
                c.writeSchemaEntries();
                c.writeConstants();
                c.writeTextNodeElementCode();
                c.writeToSQLCode();
            }

        }
    }                                   // end main()

}
