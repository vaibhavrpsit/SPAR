/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/POSLogWriterException.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:18 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:30:48  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.2  2004/02/12 17:13:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jan 22 2003 10:04:58   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;
// foundation imports
import oracle.retail.stores.foundation.utility.BaseException;

//--------------------------------------------------------------------------
/**
    This class is used for handling exceptions during POSLogWriter operations.
    This class identifies the type of file which had the exception. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class POSLogWriterException
extends BaseException
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5851689554690544147L;

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        file type code constant
    **/
    public static final int FILE_TYPE_CODE_XML = 0;
    /**
        file type string constant
    **/
    public static final String FILE_TYPE_STRING_XML = "XML";
    /**
        file type code
    **/
    protected int fileTypeCode = FILE_TYPE_CODE_XML;
    /**
        file type string
    **/
    protected String fileType = FILE_TYPE_STRING_XML;

    //---------------------------------------------------------------------
    /**
       Constructs exception object.
    **/
    //---------------------------------------------------------------------
    public POSLogWriterException()
    {
    }

    //---------------------------------------------------------------------
    /**
       Constructs object with message as parameter.
       @param msg message
    **/
    //---------------------------------------------------------------------
    public POSLogWriterException(String msg)
    {
        super(msg);
    }

    //---------------------------------------------------------------------
    /**
       Constructs object with nested exception as parameter.
       @param nestedException nested exception
    **/
    //---------------------------------------------------------------------
    public POSLogWriterException(Throwable nestedException)
    {                                   // begin POSLogWriterException()
        super(nestedException);
    }                                   // end POSLogWriterException()

    //---------------------------------------------------------------------
    /**
       Constructs object with message andnested exception as parameters.
       @param msg message
       @param nestedException nested exception
    **/
    //---------------------------------------------------------------------
    public POSLogWriterException(String msg, Throwable nestedException)
    {
        this(msg);
        setNestedException(nestedException);
    }

    //---------------------------------------------------------------------
    /**
        Sets file type code.
        @param value file type code
    **/
    //---------------------------------------------------------------------
    public void setFileTypeCode(int value)
    {                                   // begin setFileTypeCode()
        fileTypeCode = value;
    }                                   // end setFileTypeCode()

    //---------------------------------------------------------------------
    /**
        Returns file type code.
        @return file type code
    **/
    //---------------------------------------------------------------------
    public int getFileTypeCode()
    {                                   // begin getFileTypeCode()
        return(fileTypeCode);
    }                                   // end getFileTypeCode()

    //---------------------------------------------------------------------
    /**
        Sets file type string.
        @param value file type string
    **/
    //---------------------------------------------------------------------
    public void setFileType(String value)
    {                                   // begin setFileType()
        fileType = value;
    }                                   // end setFileType()

    //---------------------------------------------------------------------
    /**
        Returns file type string.
        @return file type string
    **/
    //---------------------------------------------------------------------
    public String getFileType()
    {                                   // begin getFileType()
        return(fileType);
    }                                   // end getFileType()

}
