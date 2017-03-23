package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Classes {

  public static <C> Constructor<C> defaultConstructorOf(@NotNull final Class<C> claß)
          throws IllegalArgumentException
  {
    try {
      return claß.getDeclaredConstructor();
    }
    catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(String.format("The class \"%s\" has no default constructor", claß.getName()));
    }
  }

  /**
   * Returns the list of declared non-private non-final non-transient fields.
   * @param clazz class which fields to return.
   * @return      the fields.
   */
  public static NameAndClass[] assignableFieldsOf(@NotNull final Class<?> clazz) {
    final ArrayList<NameAndClass> fields = new ArrayList<NameAndClass>(8);
    for (Field field : clazz.getDeclaredFields()) {
      int modifiers = field.getModifiers();
      if (Modifier.isPrivate(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) {
        continue;
      }
      NameAndClass nac = new NameAndClass(field.getName(), field.getType());
      fields.add(nac);
    }

    final int n = fields.size();
    return fields.toArray(new NameAndClass[n]);
  }

}
