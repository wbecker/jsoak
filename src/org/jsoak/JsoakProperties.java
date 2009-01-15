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

  private static final long serialVersionUID = 1L;

  private static final String BROWSERS = "browsers";

  private static final String NECESSARY_FILES = "necessaryFiles";

  private static final String WEB_DIRECTORY = "webDirectory";
  private static final String CSS_DIRECTORY = "cssDirectory";
    
  private static final String KILL_BROWSER = "killBrowser";

  public String[] getAllNecessaryAbsoluteIncludes()
      throws FileNotFoundException
  {
    return TestFilesLoader.prependDirectory(this.getAllNecessaryIncludes(),
        this.getProperty(WEB_DIRECTORY));
  }

  public String[] getAllNecessaryIncludes() throws FileNotFoundException
  {
    final List<String> allNecessaryFileNames = new ArrayList<String>();
    allNecessaryFileNames.addAll(Arrays.asList(getAllTestFiles()));
    return allNecessaryFileNames.toArray(new String[]{});
  }

  public String[] getAllTestFiles() throws FileNotFoundException
  {
    TestFilesLoader loader = new TestFilesLoader(this.getProperty(WEB_DIRECTORY), ".js");
    List<String> files = loader.getFiles();
    return files.toArray(new String[] {});
  }

  public String[] getCssFiles() throws FileNotFoundException
  {
    TestFilesLoader loader = new TestFilesLoader(
        this.getProperty(WEB_DIRECTORY)+"/"+this.getProperty(CSS_DIRECTORY), ".css");
    List<String> files = loader.getFiles();
    TestFilesLoader.prependDirectory(files, this.getProperty(CSS_DIRECTORY));
    return files.toArray(new String[] {});
  }

  public String[] getBrowsers()
  {
    return this.getProperty(BROWSERS).split(" ");
  }

  public String getBrowserExecutable(String browser)
  {
    return this.getProperty(browser);
  }
  
  public boolean killBrowser() 
  {
    return Boolean.parseBoolean(this.getProperty(KILL_BROWSER));
  }

}
