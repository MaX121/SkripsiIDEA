package report;

import core.*;

public class KeyRelayedReport extends Report implements MessageListener {

    private int nrofMessageRelayed;

    public KeyRelayedReport() {
        super.init();
        this.nrofMessageRelayed = 0;
    }

    @Override public void newMessage(Message m) { }
    @Override public void messageTransferStarted(Message m, DTNHost from, DTNHost to) { }
    @Override public void messageDeleted(Message m, DTNHost where, boolean dropped) { }
    @Override public void messageTransferAborted(Message m, DTNHost from, DTNHost to) { }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        for (Message message : to.getMessageCollection()) {
            double creationTime = message.getCreationTime();
            if (from.getTombstoneList().containsKey(message.getFrom().toString())) {
                if (from.getTombstoneList().get(message.getFrom().toString()) > creationTime) {
                    this.nrofMessageRelayed++; // increment if the key is newer
                }
            }
        }
    }

    @Override
    public void done() {
        write("Number of message relayed : " + this.nrofMessageRelayed);
        super.done();
    }

}
