package org.jetbrains.dekaf.core;

/**
 * @author Leonid Bushuev from JetBrains
 **/
final class Tetra {

  int A;
  int B;
  int C;
  int D;

  public Tetra() {}

  public Tetra(final int a, final int b, final int c, final int d) {
    A = a;
    B = b;
    C = c;
    D = d;
  }

  @Override
  public String toString() {
    return A + "," + B + "," + C + "," + D;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tetra that = (Tetra) o;
    return A == that.A && B == that.B && C == that.C && D == that.D;
  }

  @Override
  public int hashCode() {
    return A + B + C + D;
  }
}
