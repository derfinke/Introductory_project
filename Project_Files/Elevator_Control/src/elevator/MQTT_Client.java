package elevator;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class MQTT_Client {
    private final MqttClient client;
    private final MqttConnectOptions options;
    private final int qos;
    private final boolean retained;
    private final IMqttMessageListener eventHandler;

    public MQTT_Client(String name, String brokerAddress, IMqttMessageListener eventHandler) throws MqttException {
        this.eventHandler = eventHandler;
        client = new MqttClient(brokerAddress, name);
        options = new MqttConnectOptions();
        options.setAutomaticReconnect(false);
        options.setCleanSession(false);
        options.setConnectionTimeout(60);
        qos = 0;
        retained = false;
    }

    public void connect() throws MqttException {
        client.connect(options);
    }

    public void publish(String topic, String payload) throws MqttException {
        client.publish(topic, payload.getBytes(), qos, retained);
    }

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic, eventHandler);
    }
    
    public void disconnect() throws MqttException {
    	client.disconnect();
    }
    
    public void close() throws MqttException {
    	client.close();
    }
}
