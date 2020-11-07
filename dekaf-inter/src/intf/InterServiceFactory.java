package org.jetbrains.dekaf.inter.intf;


import org.jetbrains.annotations.NotNull;



/**
 * Creates InterFacade instances for specific RDBMS.
 */
public interface InterServiceFactory {

    /**
     * Creates an instance of InterFacade.
     * @return just created instance.
     */
    @NotNull
    InterFacade createFacade();

}
