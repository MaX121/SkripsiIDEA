package report;

import core.*;
import java.util.*;

public class MessageTransactionReport extends Report implements MessageListener {
    private int nrofMessageSent;
    private Set <DTNHost> allHostsOnNetwork;
    private Set <Message> allMessagesOnNetwork;

    public MessageTransactionReport() {
        super();
        this.nrofMessageSent = 0;
        this.allHostsOnNetwork = new TreeSet<>();
        this.allMessagesOnNetwork = new TreeSet<>();
    }

    @Override public void newMessage(Message m) { }
    @Override public void messageTransferStarted(Message m, DTNHost from, DTNHost to) { }
    @Override public void messageDeleted(Message m, DTNHost where, boolean dropped) { }
    @Override public void messageTransferAborted(Message m, DTNHost from, DTNHost to) { }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        allHostsOnNetwork.add(to);
        allHostsOnNetwork.add(from);
        allMessagesOnNetwork.add(m);
        nrofMessageSent++;
    }

    @Override
    public void done() {

        write("Number of message created : " + nrofMessageSent);

        for (DTNHost host : allHostsOnNetwork) {
            write(host + " : " + host.getTombstoneList());
        } write("\n");

        for (DTNHost host : allHostsOnNetwork) {
            write(host + " : " + host.getMessageCollection());
        } write("\n");

        for (DTNHost host : allHostsOnNetwork) {
            String temp = ""; // initial value to the temp
            for (Message message : host.getMessageCollection()) temp += message.getFrom().toString() + " ";
            write(host + " : " + temp); // print all message's sender and its host
        } write("\n");

        for (DTNHost host : allHostsOnNetwork) {
            String temp = ""; // initial value to the temp
            for (Message message : host.getMessageCollection()) temp += message.getFrom() + "_" + message.getCreationTime() + " ";
            write(host + " : " + temp); // print all message's sender and its host
        } write("\n");

        for (DTNHost host : allHostsOnNetwork) {
            write(host + " stored " + host.getMessageCollection().size() + " messages");
        } write("\n");

        super.done();
    }

}
