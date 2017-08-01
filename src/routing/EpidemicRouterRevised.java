package routing;

import core.*;

import java.util.*;

public class EpidemicRouterRevised implements RoutingDecisionEngine {

    public EpidemicRouterRevised(Settings s) { }
    public EpidemicRouterRevised(EpidemicRouterRevised proto) { }

    @Override public void connectionUp(DTNHost myHost, DTNHost peer) { }

    @Override
    public void connectionDown(DTNHost myHost, DTNHost peer) {
        List <Message> myDeletedMessage = new ArrayList<>();
        for (Message m : myHost.getMessageCollection()) {
            DTNHost sender = m.getFrom();
            DTNHost receiver = m.getTo();
            if (myHost.getTombstoneList().containsKey(sender + " - " + receiver)) {
                if (myHost.getTombstoneList().get(sender +  " - " + receiver) > m.getCreationTime()) myDeletedMessage.add(m);
                else myHost.getTombstoneList().put(sender + " - " + receiver, m.getCreationTime());
            }
        }

        // delete old messages, preventing ConcurrentModificationException
        for (Message message : myDeletedMessage) {
            myHost.getMessageCollection().remove(message);
            System.out.println("Deleting " + message);
        }
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {

        DTNHost myHost = con.getOtherNode(peer); // get this host s
        EpidemicRouterRevised partnerRouter = this.getAnotherRouter(peer);

        for (Message message : peer.getMessageCollection()) {
            String sender = message.getFrom().toString();
            String receiver = message.getTo().toString();
            myHost.getTombstoneList().put(sender + " - " + receiver, message.getCreationTime());
            if (myHost.getTombstoneList().containsKey(sender + " - " + receiver) && myHost.getTombstoneList().get(sender + " - " + receiver) < message.getCreationTime()) {
                myHost.getTombstoneList().put(sender + " - " + receiver, message.getCreationTime());
                partnerRouter.shouldSendMessageToHost(message, peer);
                this.shouldSaveReceivedMessage(message, peer);
            }
        }
    }

    private Message getMessageById(String messageId, Collection<Message> partnerMessage) {
        for (Message message : partnerMessage) { // linear search for msg collection
            if (message.getId().equals(messageId)) return message; // return message
        } return null; // if not exist, return null
    }

    @Override public boolean newMessage(Message m) { return true; }
    @Override public boolean isFinalDest(Message m, DTNHost aHost) {
        return true;
    }
    @Override public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) {
        return true;
    }
    @Override public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) { return false; }
    @Override public RoutingDecisionEngine replicate() {
        return new EpidemicRouterRevised(this);
    }
    @Override public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) { return true; }
    @Override public boolean shouldSaveReceivedMessage(Message m, DTNHost myHost) { return true; }

    private EpidemicRouterRevised getAnotherRouter(DTNHost peer) {
        MessageRouter otherRouter = peer.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works with other routers of same type";
        return (EpidemicRouterRevised) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

}
