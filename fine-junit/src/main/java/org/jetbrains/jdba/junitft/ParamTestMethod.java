package org.jetbrains.jdba.junitft;

import org.junit.runner.Description;

import java.lang.reflect.Method;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ParamTestMethod extends TestMethod {

  final Object paramData;
  final boolean isArray;

  public ParamTestMethod(Class<?> javaClass, Method javaMethod, Object paramData) {
    super(javaClass, javaMethod);
    this.paramData = paramData;
    this.isArray = paramData != null && paramData.getClass().isArray();
  }


  @Override
  Description getDescription() {
    StringBuilder b = new StringBuilder();
    b.append(javaMethod.getName());
    b.append('[');
    if (isArray) {
      Object[] realParams = (Object[]) paramData;
      for (int i = 0, n = realParams.length; i < n; i++) {
        if (i > 0) b.append(", ");
        Object v = realParams[i];
        String s = valueDescription(v);
        s = s.replace('\r', ' ').replace('\n', ' ');
        b.append(s);
      }
    }
    else {
      b.append(paramData);
    }
    b.append(']');
    return Description.createTestDescription(javaClass, b.toString());
  }

  private String valueDescription(final Object v) {
    if (v == null) return "null";
    if (v instanceof Class) {
      final Class clazz = (Class) v;
      return clazz.getCanonicalName();
    }
    return v.toString();
  }


  @Override
  public Object invokeExplosively(Object target, Object... params) throws Throwable {
    Object[] realParams;
    if (isArray) {
      realParams = (Object[]) paramData;
    }
    else {
      realParams = new Object[] { paramData };
    }
    return super.invokeExplosively(target, realParams);
  }
}
