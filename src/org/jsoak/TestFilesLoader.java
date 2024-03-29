package org.jsoak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFilesLoader
{
  private final String testDirectoryName;
  private final String fileType;
  
  public TestFilesLoader(final String testDirectoryName, final String fileType)
      throws FileNotFoundException
  {
    System.out.println(testDirectoryName);
    this.testDirectoryName = testDirectoryName;
    this.fileType = fileType;
  }

  public List<String> getFiles() throws FileNotFoundException
  {
    final List<String> testFileNames = new ArrayList<String>();
    final File testDirectory = new File(this.testDirectoryName);
    addAllFirst(testFileNames, testDirectory);
    return testFileNames;
  }
  
  private void addAllFirst(final List<String> testFileNames,
      final File testDirectory) throws FileNotFoundException
  {
    if (testDirectory.exists() && testDirectory.isDirectory())
    {
      recurseDirectories(testFileNames, testDirectory, false);
      addAllFiles(testFileNames, testDirectory, false);
    }
    else
    {
      throw new FileNotFoundException("Test directory does not exist: "
          + testDirectory.getAbsolutePath());
    }
  }
  
  private void addAll(final List<String> testFileNames,
      final File testDirectory) throws FileNotFoundException
  {
    if (testDirectory.exists() && testDirectory.isDirectory())
    {
      addAllFiles(testFileNames, testDirectory, true);
      recurseDirectories(testFileNames, testDirectory, true);
    }
    else
    {
      throw new FileNotFoundException("Test directory does not exist: "
          + testDirectory.getAbsolutePath());
    }
  }

  private void addAllFiles(final List<String> testFileNames,
      final File testDirectory, final boolean prepend) throws FileNotFoundException
  {
    final String[] testFiles = testDirectory.list(new FilenameFilter()
    {
      @Override
      public boolean accept(final File dir, final String name)
      {
        return name.endsWith(fileType);
      }
    });
    TestFilesLoader.ensureOrder(testFiles, testDirectory);
    if(prepend) 
    {
      TestFilesLoader.prependDirectory(testFiles, testDirectory.getName());
    }
    testFileNames.addAll(Arrays.asList(testFiles));
  }

  private interface NameGetter<T>
  {
    public abstract String getName(T object);
  }

  private static void ensureOrder(final String[] testFiles,
      final File testDirectory) throws FileNotFoundException
  {
    final String orderFileName = "order.txt";

    TestFilesLoader.ensureOrderImpl(orderFileName, testFiles, testDirectory,
        new NameGetter<String>()
        {
          @Override
          public String getName(final String object)
          {
            return object;
          }
        });

  }

  private static void ensureDirectoryOrder(final File[] directories,
      final File testDirectory) throws FileNotFoundException
  {
    final String orderFileName = "dirOrder.txt";

    TestFilesLoader.ensureOrderImpl(orderFileName, directories, testDirectory,
        new NameGetter<File>()
        {
          @Override
          public String getName(final File file)
          {
            return file.getName();
          }
        });
  }

  private static <T> void ensureOrderImpl(final String orderFileName,
      final T[] testFiles, final File testDirectory,
      final NameGetter<T> nameGetter) throws FileNotFoundException
  {
    final File[] orderFile = testDirectory.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(final File file)
      {
        return file.getName().equals(orderFileName);
      }
    });
    if (orderFile.length == 1)
    {
      final BufferedReader r = new BufferedReader(new FileReader(orderFile[0]));
      String fileName;
      int counter = 0;
      try
      {
        while ((fileName = r.readLine()) != null)
        {
          fileName = fileName.trim();
          for (int i = 0; i < testFiles.length; i++)
          {
            if (nameGetter.getName(testFiles[i]).equals(fileName))
            {
              final T tmp = testFiles[counter];
              testFiles[counter] = testFiles[i];
              testFiles[i] = tmp;
              counter++;
            }
          }
        }
      }
      catch (final IOException e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  private void recurseDirectories(final List<String> testFileNames,
      final File testDirectory, final boolean prepend) throws FileNotFoundException
  {
    final File[] directories = testDirectory.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(final File path)
      {
        return path.isDirectory();
      }
    });

    TestFilesLoader.ensureDirectoryOrder(directories, testDirectory);

    final List<String> directoryFileNames = new ArrayList<String>();
    for (final File d : directories)
    {
      addAll(directoryFileNames, d);
    }
    if(prepend) {
      TestFilesLoader.prependDirectory(directoryFileNames, testDirectory
          .getName());
    }
    testFileNames.addAll(directoryFileNames);
  }

  static String[] prependDirectory(final String[] necessaryFiles,
      final String prefix)
  {
    for (int i = 0; i < necessaryFiles.length; i++)
    {
      necessaryFiles[i] = prefix + "/" + necessaryFiles[i];
    }
    return necessaryFiles;
  }

  static List<String> prependDirectory(
      final List<String> necessaryFiles, final String prefix)
  {
    for (int i = 0; i < necessaryFiles.size(); i++)
    {
      necessaryFiles.set(i, prefix + "/" + necessaryFiles.get(i));
    }
    return necessaryFiles;
  }

}
