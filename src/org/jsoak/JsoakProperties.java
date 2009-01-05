package org.jsoak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class JsoakProperties extends Properties
{
  private static final long serialVersionUID = 1L;

  private static final String BROWSERS = "browsers";

  private static final String TEST_DIRECTORY = "testDirectory";

  private static final String NECESSARY_FILES = "necessaryFiles";

  private static final String WEB_DIRECTORY = "webDirectory";

  public String[] getAllNecessaryAbsoluteIncludes()
      throws FileNotFoundException
  {
    return JsoakProperties.prependDirectory(this.getAllNecessaryIncludes(), 
        this.getProperty(WEB_DIRECTORY));
  }

  public String[] getAllNecessaryIncludes() throws FileNotFoundException
  {
    final List<String> allNecessaryFileNames = new ArrayList<String>();
    loadNecessaryFiles(allNecessaryFileNames);
    loadTestFiles(allNecessaryFileNames);
    return allNecessaryFileNames.toArray(new String[] {});
  }
  
  public String[] getAllTestFiles() throws FileNotFoundException {
    final List<String> testFileNames = new ArrayList<String>();
    this.loadTestFiles(testFileNames);
    return testFileNames.toArray(new String[] {});
  }

  private void loadNecessaryFiles(final List<String> testFileNames)
  {
    final String[] necessaryFiles = this.getSpaceSeperatedString(this
        .getProperty(NECESSARY_FILES));
    testFileNames.addAll(Arrays.asList(necessaryFiles));
  }

  private void loadTestFiles(final List<String> testFileNames)
      throws FileNotFoundException
  {
    final String testDirectoryName = this.getProperty(TEST_DIRECTORY);
    final File testDirectory = new File(testDirectoryName);
    if (testDirectory.exists() && testDirectory.isDirectory())
    {
      final String[] testFiles = testDirectory.list(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name)
        {
          return name.endsWith(".js");
        }
      });
      JsoakProperties.prependDirectory(testFiles, testDirectoryName);
      testFileNames.addAll(Arrays.asList(testFiles));
    }
    else
    {
      throw new FileNotFoundException("Test directory does not exist");
    }
  }

  private static String[] prependDirectory(final String[] necessaryFiles,
      String prefix)
  {
    for (int i = 0; i < necessaryFiles.length; i++)
    {
      necessaryFiles[i] = prefix + File.separator + necessaryFiles[i];
    }
    return necessaryFiles;
  }

  public String[] getBrowsers()
  {
    return this.getSpaceSeperatedString(this.getProperty(BROWSERS));
  }

  private String[] getSpaceSeperatedString(String string)
  {
    return string.split(" ");
  }

  private static final String DEFAULT_PROPERTIES_LOCATION = "jsoak.properties";

  public static JsoakProperties loadProperties(String propertiesFile)
  {
    JsoakProperties p = new JsoakProperties();
    try
    {
      p.load(new FileInputStream(propertiesFile));
    }
    catch (Exception e)
    {
      if (propertiesFile != DEFAULT_PROPERTIES_LOCATION)
      {
        p = loadProperties();
      }
    }
    return p;
  }

  public static JsoakProperties loadProperties()
  {
    return loadProperties(DEFAULT_PROPERTIES_LOCATION);
  }
}
