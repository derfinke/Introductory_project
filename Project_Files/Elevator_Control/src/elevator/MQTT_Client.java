package Control;

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
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(60);
        qos = 0;
        retained = true;
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
}
