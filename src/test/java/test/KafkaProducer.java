package test;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class KafkaProducer 
{
    private final Producer<String, String> producer;
    public final static String TOPIC = "test21";

    private KafkaProducer(){
        Properties props = new Properties();
//        props.put("metadata.broker.list", "121.42.141.232:9092,115.28.156.126:9092,121.42.60.39:9092");
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        //request.required.acks
        //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
        //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
        //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
        props.put("request.required.acks","-1");

        producer = new Producer<String, String>(new ProducerConfig(props));
    }

    void produce() {
        int messageNo = 0;
        final int COUNT = 10000;

        while (messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String data = "hello kafka message " + key;
            producer.send(new KeyedMessage<String, String>(TOPIC, key ,data));
            System.out.println(data);
            System.out.println("生产"+messageNo ++);
        }
    }

    public static void main( String[] args )
    {
        new KafkaProducer().produce();
    }
}