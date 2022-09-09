
### Same File Size (Java)

by: Keith Fenske, https://kwfenske.github.io/

SameFileSize is a Java 1.4 console application to find files that have the same
size as another file, which is often the first step in finding duplicate files.

Operating systems such as Microsoft Windows may modify photo files as you copy
them from a digital camera, by putting the current date and time inside each
file (called "Exif" data for JPEG images). If you copy the same photo twice
from the same camera, you can have two files with slightly different contents
and the same file size. This prevents duplicate file finders from recognizing
that both files are of the same photo. Gallery software designed for photos
knows to ignore the Exif data; general software does not.

There is no graphical interface (GUI) for this program; it must be run from a
command prompt, command shell, or terminal window. Run time will depend upon
how many files and folders are searched. Most operating systems protect some
folders, and this program treats those folders as empty.

Download the ZIP file here: https://kwfenske.github.io/same-file-size-java.zip

Released under the terms and conditions of the Apache License (version 2.0 or
later) and/or the GNU General Public License (GPL, version 2 or later).
