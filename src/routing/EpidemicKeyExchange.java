package routing;

import core.*;
import java.util.*;

public class EpidemicKeyExchange implements RoutingDecisionEngine {

    public EpidemicKeyExchange(Settings s) { }

    @Override
    public void connectionDown(DTNHost myHost, DTNHost peer) {

        // FYI : We created a list to store an old message to prevent ConcurrentModification
        // there are two ways to prevent ConcurrentModification, using Iterator and using
        // a temp-like list to store the object and delete it later. I stick with the second
        // one because Since the computational cost isn't an issue here, the second one is

        List <Message> myDeletedMessage = new ArrayList<>();
        for (Message m : myHost.getMessageCollection()) {
            if (myHost.getTombstoneList().containsKey(m.getFrom().toString())) {
                if (myHost.getTombstoneList().get(m.getFrom().toString()) > m.getCreationTime()) myDeletedMessage.add(m);
                else myHost.getTombstoneList().put(m.getFrom().toString(), m.getCreationTime());
            }
        }

        // delete old messages, preventing ConcurrentModificationException
        for (Message m : myDeletedMessage) {
            myHost.getMessageCollection().remove(m);
            System.out.println("Deleting " + m);
        }

    }

    public EpidemicKeyExchange(EpidemicKeyExchange proto) { }

    @Override public void connectionUp(DTNHost myHost, DTNHost peer) { }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {

        DTNHost myHost = con.getOtherNode(peer); // get this host
        EpidemicKeyExchange partnerRouter = this.getAnotherRouter(peer);

        for (Message message : peer.getMessageCollection()) {
            DTNHost sender = message.getFrom();
            myHost.getTombstoneList().put(sender.toString(), message.getCreationTime());
            if (myHost.getTombstoneList().containsKey(sender.toString()) && myHost.getTombstoneList().get(sender.toString()) < message.getCreationTime()) {
                myHost.getTombstoneList().put(sender.toString(), message.getCreationTime());
                partnerRouter.shouldSendMessageToHost(message, peer);
                this.shouldSaveReceivedMessage(message, peer);
            }
        }

    }

    @Override public boolean newMessage(Message m) { return true; }
    @Override public boolean isFinalDest(Message m, DTNHost aHost) { return true; }
    @Override public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) { return true;}
    @Override public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) { return false; }
    @Override public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) { return true; }
    @Override public RoutingDecisionEngine replicate() { return new EpidemicKeyExchange(this); }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost myHost) {
        if (myHost.getTombstoneList().containsKey(m.getFrom().toString())) {
            if (myHost.getTombstoneList().get(m.getFrom().toString()) > m.getCreationTime()) return false;
            else return true;
        } else return true;
    }

    private EpidemicKeyExchange getAnotherRouter(DTNHost peer) {
        MessageRouter otherRouter = peer.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works with other routers of same type";
        return (EpidemicKeyExchange) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

}