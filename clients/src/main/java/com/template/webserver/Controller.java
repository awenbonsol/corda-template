package com.template.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.flows.CreateCarFlow;
import com.template.flows.TransferOwnershipFlow;
import com.template.states.CarState;
import net.corda.client.jackson.JacksonSupport;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
public class Controller {
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);
    private final CordaRPCOps proxy;
    private final CordaX500Name me;

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    public String toDisplayString(X500Name name) {
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isMe(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo) {
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }

    @Configuration
    class Plugin {
        @Bean
        public ObjectMapper registerModule() {
            return JacksonSupport.createNonRpcMapper();
        }
    }

    @GetMapping(value = "/notaries", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<Object> notaries() {
        try {
            HashMap<String, String> myMap = new HashMap<>();
            myMap.put("notary", proxy.notaryIdentities().toString());
            return new ResponseEntity<>(APIResponse.success(myMap), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/myIdentity", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<Object> myIdentity() {

        try {
            HashMap<String, String> myMap = new HashMap<>();
            myMap.put("myIdentity", me.toString());
            return new ResponseEntity<>(APIResponse.success(myMap),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-car")
    public ResponseEntity<Object> registerFarmer(@RequestBody Forms.Car car) {
        try {
            SignedTransaction result = proxy.startTrackedFlowDynamic(CreateCarFlow.CreateCarFlowInitiator.class, car.getBrand(),
                    car.getModel(), car.getYear(), car.getColor()).getReturnValue().get();

            logger.info("end: calling /create-car");
            return new ResponseEntity<>(APIResponse.success("Transaction id: " + result.getId()),HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transferOwnership(@RequestBody Forms.Transfer i) {
        try {
            if (i.getOwner().toLowerCase().contains("customer")) {
                i.setOwner("O=Customer,L=New York,C=US");
            }

            String party = i.getOwner();
            CordaX500Name partyX500Name = CordaX500Name.parse(party);
            Party customer = proxy.wellKnownPartyFromX500Name(partyX500Name);

            SignedTransaction result = proxy.startTrackedFlowDynamic(TransferOwnershipFlow.TransferOwnershipFlowInitiator.class, customer)
                    .getReturnValue().get();

            logger.info("end: calling /transfer");
            return new ResponseEntity<>(APIResponse.success("Transaction id: " + result.getId()),HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-car")
    public ResponseEntity<Object> getCars() {
        try {
            logger.info("start: calling /get-car");
            List<StateAndRef<CarState>> carStateandRef = proxy.vaultQuery(CarState.class).getStates();

            List<CarState> carStates = new ArrayList<>();
            for (StateAndRef<CarState> carState : carStateandRef) {
                carStates.add(carState.getState().getData());
            }

            logger.info("end: calling /get-car");
            return new ResponseEntity<>(APIResponse.success(carStates),HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(APIResponse.error(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

}