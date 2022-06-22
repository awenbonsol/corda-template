package com.template.contracts;

import com.template.states.CarState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;


public class CarStateContract implements Contract {

    public static final String ID = "com.template.contracts.CarStateContract";

    @Override
    public void verify(LedgerTransaction tx) {

        final CommandData commandData = tx.getCommands().get(0).getValue();

        if (commandData instanceof Commands.CreateCar) {
            /**
             * validations left empty for simplicity
             */
        }
    }


    public interface Commands extends CommandData {
        class CreateCar implements Commands {}
    }
}