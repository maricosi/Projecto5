import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class SendTopic {
	private ConnectionFactory cf;
	private Topic topic;
	

	public SendTopic() throws NamingException {
		this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
		topic = InitialContext.doLookup("jms/topic/testTopic");
	}

	public void send(String text) {
		try (JMSContext jcontext = cf.createContext("mr", "mr2015");) {
			JMSProducer mp = jcontext.createProducer();
			mp.send(topic, text);
		} catch (JMSRuntimeException re) {
			re.printStackTrace();
		}
	}

}
