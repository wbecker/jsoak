package org.jsoak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    return TestFilesLoader.prependDirectory(this.getAllNecessaryIncludes(),
        this.getProperty(WEB_DIRECTORY));
  }

  public String[] getAllNecessaryIncludes() throws FileNotFoundException
  {
    final List<String> allNecessaryFileNames = new ArrayList<String>();
    loadNecessaryFiles(allNecessaryFileNames);
    allNecessaryFileNames.addAll(Arrays.asList(getAllTestFiles()));
    return allNecessaryFileNames.toArray(new String[]{});
  }

  public String[] getAllTestFiles() throws FileNotFoundException
  {
    TestFilesLoader loader = new TestFilesLoader(    this.getProperty(WEB_DIRECTORY)
        + File.separator + this.getProperty(TEST_DIRECTORY));
    return loader.getFiles().toArray(new String[] {});
  }

  private void loadNecessaryFiles(final List<String> testFileNames)
  {
    final String[] necessaryFiles = this.getProperty(NECESSARY_FILES)
        .split(" ");
    testFileNames.addAll(Arrays.asList(necessaryFiles));
  }

  public String[] getBrowsers()
  {
    return this.getProperty(BROWSERS).split(" ");
  }

  public String getBrowserExecutable(String browser)
  {
    return this.getProperty(browser);
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
