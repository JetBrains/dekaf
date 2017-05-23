package org.jetbrains.dekaf.util;

/**
 * Kind of the java primitive type.
 */
public enum JavaPrimitiveKind {

    JAVA_BOOLEAN(boolean.class),
    JAVA_BYTE   (byte.class),
    JAVA_SHORT  (short.class),
    JAVA_INT    (int.class),
    JAVA_LONG   (long.class),
    JAVA_FLOAT  (float.class),
    JAVA_DOUBLE (double.class);

    /**
     * The primitive class.
     */
    public final Class primitiveClass;


    JavaPrimitiveKind(final Class primitiveClass) {this.primitiveClass = primitiveClass;}

}
