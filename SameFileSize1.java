/*
  Same File Size #1 - Find All Files With Same File Size
  Written by: Keith Fenske, http://kwfenske.github.io/
  Tuesday, 8 September 2020
  Java class name: SameFileSize1
  Copyright (c) 2020 by Keith Fenske.  Apache License or GNU GPL.

  This is a Java 1.4 console application to find files that have the same size
  as another file, which is often the first step in finding duplicate files.

  Operating systems such as Microsoft Windows may modify photo files as you
  copy them from a digital camera, by putting the current date and time inside
  each file (called "Exif" data for JPEG images).  If you copy the same photo
  twice from the same camera, you can have two files with slightly different
  contents and the same file size.  This prevents duplicate file finders from
  recognizing that both files are of the same photo.  Gallery software designed
  for photos knows to ignore the Exif data; general software does not.

  To run this program, put a list of file or folder names on the command line.
  For example, if your photos are in a folder called "MyPhotos", then use the
  following command line:

      java  SameFileSize1  MyPhotos

  See the "-?" option for a help summary:

      java  SameFileSize1  -?

  There is no graphical interface (GUI) for this program; it must be run from a
  command prompt, command shell, or terminal window.  Run time will depend upon
  how many files and folders are searched.  Most operating systems protect some
  folders, and this program treats those folders as empty.

  Apache License or GNU General Public License
  --------------------------------------------
  SameFileSize1 is free software and has been released under the terms and
  conditions of the Apache License (version 2.0 or later) and/or the GNU
  General Public License (GPL, version 2 or later).  This program is
  distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE.  See the license(s) for more details.  You should have
  received a copy of the licenses along with this program.  If not, see the
  http://www.apache.org/licenses/ and http://www.gnu.org/licenses/ web pages.
*/

import java.io.*;                 // standard I/O
import java.text.*;               // number formatting
import java.util.*;               // calendars, dates, lists, maps, vectors

public class SameFileSize1
{
  /* constants */

  static final String COPYRIGHT_NOTICE =
    "Copyright (c) 2020 by Keith Fenske.  Apache License or GNU GPL.";
  static final int EXIT_FAILURE = -1; // incorrect request or errors found
  static final int EXIT_SUCCESS = 1; // request completed successfully
  static final int EXIT_UNKNOWN = 0; // don't know or nothing really done
  static final String PROGRAM_TITLE =
    "Find All Files With Same File Size - by: Keith Fenske";

  /* class variables */

  static NumberFormat formatComma; // formats with commas (digit grouping)
  static boolean mswinFlag;       // true if running on Microsoft Windows
  static boolean recurseFlag;     // true if we search folders and subfolders
  static TreeMap sizeMap;         // mapping from file sizes to list of files
  static int totalFiles;          // total number of files found
  static int totalFolders;        // total number of folders or subfolders
  static int totalSame;           // total number of files with same size

/*
  main() method

  We run as a console application.  There is no graphical interface.
*/
  public static void main(String[] args)
  {
    int fileCount;                // number of files with same file size
    Iterator fileIterate;         // for iterating over elements in <fileList>
    TreeSet fileList;             // list of file path names, same file size
    int i;                        // index variable
    Iterator sizeIterate;         // for iterating over elements in <sizeMap>
    Long sizeKey;                 // one index key from <sizeMap>
    String word;                  // one parameter from command line

    /* Initialize global and local variables. */

    formatComma = NumberFormat.getInstance(); // current locale
    formatComma.setGroupingUsed(true); // use commas or digit groups
    mswinFlag = System.getProperty("os.name").startsWith("Windows");
    recurseFlag = true;           // by default, search folders and subfolders
    sizeMap = new TreeMap();      // empty mapping from sizes to list of files
    totalFiles = totalFolders = totalSame = 0; // no files found yet

    /* Check command-line parameters for options. */

    for (i = 0; i < args.length; i ++)
    {
      word = args[i].toLowerCase(); // easier to process if consistent case
      if (word.length() == 0)
      {
        /* Ignore empty parameters, which are more common than you might think,
        when programs are being run from inside scripts (command files). */
      }

      else if (word.equals("?") || word.equals("-?") || word.equals("/?")
        || word.equals("-h") || (mswinFlag && word.equals("/h"))
        || word.equals("-help") || (mswinFlag && word.equals("/help")))
      {
        showHelp();               // show help summary
        System.exit(EXIT_UNKNOWN); // exit application after printing help
      }

      else if (word.equals("-s") || (mswinFlag && word.equals("/s"))
        || word.equals("-s1") || (mswinFlag && word.equals("/s1")))
      {
        recurseFlag = true;       // start doing subfolders
      }
      else if (word.equals("-s0") || (mswinFlag && word.equals("/s0")))
        recurseFlag = false;      // stop doing subfolders

      else if (word.startsWith("-") || (mswinFlag && word.startsWith("/")))
      {
        System.err.println("Option not recognized: " + args[i]);
        showHelp();               // show help summary
        System.exit(EXIT_FAILURE); // exit application after printing help
      }

      else
      {
        /* Parameter does not look like an option.  Assume this is a file or
        folder name. */

        processFileOrFolder(new File(args[i]));
      }
    }

    /* Go through the map by file size looking for sizes that have more than
    one file.  Sizes are in ascending order.  File or path names are sorted,
    using strict Unicode character order (i.e., as Java UTF-16 strings). */

    sizeIterate = sizeMap.keySet().iterator(); // iterator for file sizes
    while (sizeIterate.hasNext()) // any more file sizes?
    {
      sizeKey = (Long) sizeIterate.next(); // get one file size as an object
      fileList = (TreeSet) sizeMap.get(sizeKey); // list of file path names
      fileCount = fileList.size(); // get number of files for this size
      if (fileCount > 1)          // only want multiple files, same size
      {
        totalSame += fileCount;   // add to number of files with same size
        System.out.println();     // blank line
        System.out.println("Size " + formatComma.format(sizeKey.longValue())
          + " bytes has " + formatComma.format(fileCount) + " files:");
        fileIterate = fileList.iterator(); // iterator for file path names
        while (fileIterate.hasNext()) // any more file path names?
          System.out.println("  " + (String) fileIterate.next());
      }
    }

    /* Print a summary of what we found.  Set the exit status from this
    program. */

    System.out.println();         // blank line
    System.out.println("Found " + formatComma.format(totalSame)
      + " files with same size from " + formatComma.format(totalFiles)
      + " files in " + formatComma.format(totalFolders) + " folders.");

    if ((totalFiles > 0) || (totalFolders > 0)) // any files or folders?
      System.exit(EXIT_SUCCESS);
    else                          // no files or folders given by user
    {
      showHelp();                 // show help summary
      System.exit(EXIT_UNKNOWN);  // exit application after printing help
    }

  } // end of main() method


/*
  processFileOrFolder() method

  The caller gives us a Java File object that may be a file, a folder, or just
  random garbage.  Search all files.  Get folder contents and process each file
  found, doing subfolders only if the <recurseFlag> is true.
*/
  static void processFileOrFolder(File givenFile)
  {
    File[] contents;              // contents if <givenFile> is a folder
    String filePath;              // full directory path name for <givenFile>
    int i;                        // index variable
    File next;                    // next File object from <contents>
    Long sizeKey;                 // one index key from <sizeMap>

    if (givenFile.isDirectory())  // if this is a folder
    {
      totalFolders ++;            // one more folder found
      System.err.println("Scanning folder: " + givenFile.getPath());
      contents = givenFile.listFiles(); // unsorted, no filter
      if (contents == null)       // happens for protected system folders
      {
        System.err.println("Protected folder: " + givenFile.getPath());
        contents = new File[0];   // replace with an empty array
      }
      for (i = 0; i < contents.length; i ++) // for each file in order
      {
        next = contents[i];       // get next File object from <contents>
        if (next.isDirectory())   // is this a subfolder (in the folder)?
        {
          if (recurseFlag)        // should we look at subfolders?
            processFileOrFolder(next); // yes, search this subfolder
          else
            System.err.println("Ignoring subfolder: " + next.getPath());
        }
        else if (next.isFile())   // is this a file (in the folder)?
        {
          processFileOrFolder(next); // call ourself to process this file
        }
        else
        {
          /* File or folder does not exist.  Ignore without comment. */
        }
      }
    }
    else if (givenFile.isFile())  // if this is a file
    {
      totalFiles ++;              // one more file found
      try { filePath = givenFile.getCanonicalPath(); }
                                  // get full directory path name, if possible
      catch (IOException ioe) { filePath = givenFile.getPath(); }
                                  // or accept abstract path name otherwise
      sizeKey = new Long(givenFile.length()); // get file size as an object
      if (sizeMap.containsKey(sizeKey) == false) // create new mapping key?
        sizeMap.put(sizeKey, new TreeSet()); // yes, start with empty list
      ((TreeSet) sizeMap.get(sizeKey)).add(filePath); // add to list by size
    }
    else
      System.err.println("Not a file or folder: " + givenFile.getPath());

  } // end of processFileOrFolder() method


/*
  showHelp() method

  Show the help summary.  This is a UNIX standard and is expected for all
  console applications, even very simple ones.
*/
  static void showHelp()
  {
    System.err.println();
    System.err.println(PROGRAM_TITLE);
    System.err.println();
    System.err.println("  java  SameFileSize1  [options]  fileOrFolderNames");
    System.err.println();
    System.err.println("Options:");
    System.err.println("  -? = -help = show summary of command-line syntax");
    System.err.println("  -s0 = do only given files or folders, no subfolders");
    System.err.println("  -s1 = -s = process files, folders, and subfolders (default)");
    System.err.println();
    System.err.println("Output may be redirected with the \">\" operator.");
    System.err.println();
    System.err.println(COPYRIGHT_NOTICE);
//  System.err.println();

  } // end of showHelp() method

} // end of SameFileSize1 class

/* Copyright (c) 2020 by Keith Fenske.  Apache License or GNU GPL. */
