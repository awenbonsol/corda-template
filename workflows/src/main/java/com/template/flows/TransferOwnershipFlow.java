package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.CarStateContract;
import com.template.states.CarState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;


public class TransferOwnershipFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class TransferOwnershipFlowInitiator extends FlowLogic<SignedTransaction> {

        private Party owner;

        public TransferOwnershipFlowInitiator(Party owner) {
            this.owner = owner;
        }

        @Override
        @Suspendable
        public SignedTransaction call() throws FlowException {
            StateAndRef carStateAndRef = getServiceHub().getVaultService().queryBy(CarState.class).getStates().get(0);

            CarState carState = (CarState) carStateAndRef.getState().getData();

            carState.setOwner(this.owner);

            TransactionBuilder txBuilder = new TransactionBuilder(carStateAndRef.getState().getNotary())
                    .addInputState(carStateAndRef)
                    .addOutputState(carState)
                    .addCommand(new CarStateContract.Commands.CreateCar(),
                            Arrays.asList(this.getOurIdentity().getOwningKey(), this.owner.getOwningKey()));

            txBuilder.verify(getServiceHub());

            SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            FlowSession session = initiateFlow(this.owner);

            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTx, Arrays.asList(session)));

            return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(session)));
        }
    }

    @InitiatedBy(TransferOwnershipFlowInitiator.class)
    public static class Responder extends FlowLogic<Void> {
        private FlowSession otherPartySession;

        public Responder(FlowSession otherPartySession) {
            this.otherPartySession = otherPartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(otherPartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {

                }
            });

            subFlow(new ReceiveFinalityFlow(otherPartySession, signedTransaction.getId()));
            return null;
        }
    }
}
