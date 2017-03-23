/**
 * Dekaf Intermediate Layer.
 *
 * <p>
 *   Contains interfaces and values-object classes that are needed to interact between client layer (client side) and RDBMS vendor provided API.
 * </p>
 *
 * <p>
 *   There are interfaces:
 *   <ul>
 *     <li><b>PrimeIntermediate*</b>: portable prime (lite) services that can be run remotely (in another java process)</li>
 *     <li><b>IntegralIntermediate*</b>: non-portable integral services that contain methods for improve performance when interacting directly (when the vendor API is in the same process)</li>
 *   </ul>
 * </p>
 *
 * <p>
 *   Also contains interface {@link org.jetbrains.dekaf.intermediate.DBExceptionRecognizer} for RDBMS-specific errors analysis.
 * </p>
 *
 *
 * @author Leonid Bushuev from JetBrains
 */
package org.jetbrains.dekaf.intermediate;