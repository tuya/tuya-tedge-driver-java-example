package dpdemo;

import dpdemo.dpdriver.DpDriverImpl;
//import dpdemo.mqttdriver.MqttDriverImpl;
//import tuya.tedge.driver.sdk.base.mqttitf.MqttDriver;
import tuya.tedge.driver.sdk.base.model.GatewayModel;
import tuya.tedge.driver.sdk.base.utils.PrintException;
import tuya.tedge.driver.sdk.dpmodel.DPModelDriver;
import tuya.tedge.driver.sdk.dpmodel.DpDriverService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            //Step1: DP 模型，创建 DpDriverService
            DpDriverService driver = new DpDriverService(args);

            GatewayModel gatewayModel = driver.getTEdgeModel();
            log.info("main get gatewayModel:{}", gatewayModel);

            //Step2: 实现 DPModelDriver 接口，并调用 setDpDriverImpl
            DpDriverImpl driverImpl = new DpDriverImpl(driver.getDpDriverApi());
            DPModelDriver dpDriver = driverImpl;
            log.info("main set dpDriverImpl, start service!");

            //对接MQTT协议设备时，需另外实现MqttDriver接口，并调用相应接口设置回调
            //get mqtt username from customConfig
            //String username = driverImpl.getMqttUsername();
            //MqttDriver mqttDriver = new MqttDriverImpl();
            //driver.setMqttDriverImpl(mqttDriver, username);

            //Step3: 调用 start，异步阻塞
            driver.start(dpDriver);

            //Step4: 基本功能示例: 获取配置文件，获取子设备，新增产品、子设备、上报子设备状态、上报DP
            driverImpl.run();
        } catch (Exception e) {
            PrintException.printStack(e);
        }
    }
}
