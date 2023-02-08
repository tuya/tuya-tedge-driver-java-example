package dpdemo.mqttdriver;

import tuya.tedge.driver.sdk.base.mqttitf.MqttDriver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttDriverImpl implements MqttDriver {
    @Override
    public Boolean auth(String clientId, String username, String password) {
        log.info("mqtt auth clientId:{},username:{},password{}", clientId, username, password);
        //TODO: implement me
        return true;
    }

    @Override
    public Boolean subscribe(String clientId, String username, String topic, Byte qos) {
        log.info("mqtt Subscribe clientId:{},,username:{},topic:{},qos:{}", clientId, username, topic, qos);
        //TODO: implement me
        return true;
    }

    @Override
    public Boolean publish(String clientId, String username, String topic, Byte qos, Boolean retained) {
        log.info("mqtt publish clientId:{},,username:{},topic:{},qos:{},retained:{}", clientId, username, topic, qos, retained);
        //TODO: implement me
        return true;
    }

    @Override
    public void unSubscribe(String clientId, String username, String[] topics) {
        log.info("mqtt unSubscribe clientId:{},,username:{},topics:{}", clientId, username, topics);
        //TODO: implement me
    }

    @Override
    public void connected(String clientId, String username) {
        log.info("mqtt connected clientId:{},,username:{}", clientId, username);
        //TODO: implement me
    }

    @Override
    public void closed(String clientId, String username) {
        log.info("mqtt closed clientId:{},,username:{}", clientId, username);
        //TODO: implement me
    }
}
