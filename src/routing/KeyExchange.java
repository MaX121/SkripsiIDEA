package routing;

import java.util.*;
import core.*;

public class KeyExchange implements RoutingDecisionEngine {

    private Set<String> sumVectorList;

    public KeyExchange(Settings s) { }
    public KeyExchange(KeyExchange proto) {
        this.sumVectorList = new HashSet<>();
    }

    public void connectionUp(DTNHost thisHost, DTNHost peer) { }
    public void connectionDown(DTNHost thisHost, DTNHost peer) { }
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost myHost = con.getOtherNode(peer);
        Collection <Message> thisPeerMessage = peer.getMessageCollection();
        Collection <Message> partnerMessage = peer.getMessageCollection();

        // if the hash code different with one and another, it means
        // at least one message not exist in the router. so we need
        // to do exchange summary vector

        if (thisPeerMessage.hashCode() != partnerMessage.hashCode()) {
            sumVectorCheck(peer, myHost);

            Iterator iterator = sumVectorList.iterator();
            while (iterator.hasNext()) {
                Message message = (Message) iterator.next();
                this.shouldSendMessageToHost(message, myHost);
                this.shouldSaveReceivedMessage(message, myHost);
            }
        }
    }

    private void sumVectorCheck(DTNHost peer, DTNHost myHost) {
        Collection <Message> thisPeerMessage = myHost.getMessageCollection();
        Collection <Message> partnerMessage = peer.getMessageCollection();

        for (Message message : partnerMessage) {
            if (!thisPeerMessage.contains(message)) {
                sumVectorList.add(message.getId()); // add into exchange list
                // the list will used later to exchange the summary vector
            }
        }
    }

    public boolean newMessage(Message m) { return true; }
    public boolean isFinalDest(Message m, DTNHost aHost) { return true;  }
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) { return true; }
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) { return true;  }
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) { return false; }

    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
        DTNHost sender = m.getFrom();
        DTNHost receiver = m.getTo();
        double creationTime = m.getCreationTime();

        for (Message message : hostReportingOld.getMessageCollection()) {
            if (message.getFrom().equals(sender) && message.getTo().equals(receiver) &&
                    message.getCreationTime() > creationTime) { return true;
                // delete the message if the stored key is newer than the
                // receive message. the freshness of the message/key depends on the
                // creation time. if the creation time is newer, receive message
            }
        }

        return false; // delete the message because the received message is older
        // than the existing message with the same source and same destination.
    }

    public RoutingDecisionEngine replicate() {
        return new KeyExchange(this);
    }
}
