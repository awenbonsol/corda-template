package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.CarStateContract;
import com.template.states.CarState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateCarFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class CreateCarFlowInitiator extends FlowLogic<SignedTransaction>{

        private String brand;
        private String model;
        private int year;
        private String color;

        public CreateCarFlowInitiator(String brand, String model, int year, String color) {
            this.brand = brand;
            this.model = model;
            this.year = year;
            this.color = color;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            CarState carState = new CarState(brand, model,year,color,new UniqueIdentifier(),this.getOurIdentity(),this.getOurIdentity());

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(carState)
                    .addCommand(new CarStateContract.Commands.CreateCar(),
                            Arrays.asList(this.getOurIdentity().getOwningKey()));

            txBuilder.verify(getServiceHub());

            SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            return subFlow(new FinalityFlow(signedTx, Collections.emptyList()));
        }
    }

}