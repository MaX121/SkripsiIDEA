package report;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.DTNHost;
import core.Message;
import core.MessageListener;

public class ReportTransferMessage extends Report implements MessageListener{
	
	private Map<DTNHost,Integer> receivedCounts;
	
	public ReportTransferMessage(){
		init();
	}
	
	@Override
	public void init(){
		super.init();
		this.receivedCounts=new HashMap<>();
	}
	@Override
	public void newMessage(Message m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean firstDelivery) {
		// TODO Auto-generated method stub
		List<DTNHost> hopNodes=m.getHops();
		if(hopNodes.get(hopNodes.size()-1)!= m.getTo()){
			DTNHost receivedNode=hopNodes.get(hopNodes.size()-1);
			if(receivedCounts.containsKey(receivedNode)){
				receivedCounts.put(receivedNode,receivedCounts.get(receivedNode)+1);
			}else{
				receivedCounts.put(receivedNode, 1);
			}
		}
		
	}

	@Override
	public void done() {
		write("Message Transfer for scenario " + getScenarioName() + 
				"\nsim_time: " + format(getSimTime())+"\n");
		
		
		String statsText = "";
		for(DTNHost key:receivedCounts.keySet()){
			write(key.toString()+"\t"+receivedCounts.get(key));
		}
		
		
		write(statsText);
		super.done();
	}

}
