package adaptme;


import java.net.MalformedURLException;

import java.net.URL;

import java.net.URLClassLoader;


public class DynamicExperimentationProgramProxyFactory {
  public static IDynamicExperimentationProgramProxy newInstance() {
    URLClassLoader tmp = new URLClassLoader(new URL[] {getClassPath()}) {
      @Override
      public
      synchronized Class<?> loadClass(String name)
      throws ClassNotFoundException {
        if ("adaptme.DynamicExperimentationProgramProxy".equals(name))
          return findClass(name);
        return super.loadClass(name);
      }
    };

    try {
      return (IDynamicExperimentationProgramProxy) tmp.loadClass("adaptme.DynamicExperimentationProgramProxy").newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException(e.getCause());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static URL getClassPath() {
    String resName = 
      DynamicExperimentationProgramProxyFactory.class.getName().replace('.', '/') + ".class";
    String loc = 
      DynamicExperimentationProgramProxyFactory.class.getClassLoader().getResource(resName)
      .toExternalForm();    
    URL cp;
    try {
      cp = new URL(loc.substring(0, loc.length() - resName.length()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return cp;
  }
}
