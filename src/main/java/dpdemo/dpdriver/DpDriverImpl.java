package dpdemo.dpdriver;

import tuya.tedge.driver.sdk.base.model.*;
import tuya.tedge.driver.sdk.dpmodel.DPModelDriver;
import tuya.tedge.driver.sdk.dpmodel.DpDriverApi;
import tuya.tedge.driver.sdk.dpmodel.model.*;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DpDriverImpl implements DPModelDriver {
    private Map<String, DeviceShadow> deviceMap;
    private DpDriverApi sdk;
    private String newProductId;
    private JSONObject customConfig;

    public DpDriverImpl(DpDriverApi sdk) {
        this.sdk = sdk;
        this.deviceMap = new ConcurrentHashMap<>();
        this.newProductId = "en1agoqpbynmgb5l"; //演示产品pid：霍尼韦尔能耗系统

        //获取专家模式中的配置文件，获取自定义配置信息。
        this.getCustomConfig();
    }

    /**
     * 在TEdge控制台页面，新增、激活、更新子设备属性、删除子设备时，回调该接口
     * 如果接入的设备不需要在Tedge控制台页面手动新增子设备，则该接口实现为空即可
     * 注意：不要在该接口做阻塞性操作
     *
     * @param deviceNotifyType
     * @param device
     */
    @Override
    public void deviceNotify(DeviceNotifyType deviceNotifyType, DeviceInfo device) {
        log.info("deviceNotify type:{},device:{}", deviceNotifyType.getValue(), device.toString());
        String cid = device.getId();
        DeviceShadow oldDev = deviceMap.get(cid);

        switch (deviceNotifyType) {
            case DeviceAddNotify:
            case DeviceActiveNotify:
            case DeviceUpdateNotify:
                if (!device.getActiveStatus().equals(DeviceStatus.DeviceActiveStatusActivated)) {
                    log.warn("deviceNotify cid:{} not activated", cid);
                    return;
                }
                if (oldDev != null) {
                    deviceMap.remove(cid);
                    oldDev.stop();
                }

                DeviceShadow newDev = new DeviceShadow(cid, device.getName(), this.sdk);
                deviceMap.put(device.getId(), newDev);

                newDev.running();
                log.info("deviceNotify cid:{}, running...", cid);
            case DeviceDeleteNotify:
                if (oldDev != null) {
                    deviceMap.remove(cid);
                    oldDev.stop();
                }
        }
    }

    /**
     * ProductNotify 产品增删改通知,删除产品时product参数为空
     * 不要在该接口做阻塞性操作
     *
     * @param productNotifyType
     * @param productInfo
     */
    @Override
    public void productNotify(ProductNotifyType productNotifyType, ProductInfo productInfo) {
        log.info("productNotify type:{}, product:{}", productNotifyType.getValue(), productInfo.toString());
    }

    /**
     * 在TEdge页面，更新驱动实例，或停止驱动实例时，回调该接口，驱动程序进行资源清理
     */
    @Override
    public void stop() {
        log.info("stop in...");
        for (DeviceShadow dev : deviceMap.values()) {
            dev.stop();
        }
    }

    /**
     * TEdge/云端 下发的MQTT消息
     * 注意：不要在该接口做阻塞性操作
     *
     * @param cid
     * @param commandRequest
     * @param protocols
     * @param dpExtendMap
     */
    @Override
    public void handleCommands(String cid, CommandRequest commandRequest, Map<String, ProtocolProperties> protocols, Map<String, DpExtend> dpExtendMap) {
        log.info("handleCommands cid：{}, handleCommands:{}", cid, commandRequest);

        //TODO: 处理下发指令, 不要在该接口做阻塞性操作, 这里只是演示
        DeviceShadow devShadow = deviceMap.get(cid);
        if (devShadow != null) {
            devShadow.processDpMessage(commandRequest);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //启动后，先初始化已经存在的子设备列表
    public void run() {
        //1.获取专家模式中的配置项
        this.getMqttUsername();

        //2.新增一个产品
        this.addNewProduct();

        //3.添加并激活一个子设备
        this.addNewDevice();
        //更新子设备名
        this.modifyDeviceName();

        //4.获取所有已经激活的子设备，并运行子设备
        this.runAllActiveDevices();

        //5.测试告警接口
        this.testAlarmReport();

        //6.获取网关信息
        this.getGatewayInfo();

        //7.测试key/value相关接口
        String key1 = "TestKey1";
        String value1 = "TestValue1";
        String key2 = "TestKey2";
        String value2 = "TestValue2";
        this.testGetPutBackupKV(key1, value1.getBytes());
        this.testGetPutBackupKV(key2, value2.getBytes());
        this.testDeleteBackupKV(key2);

        Map<String, byte[]> extendData = new HashMap<>();
        extendData.put("TestKey3", "TestValue3".getBytes());
        extendData.put("TestKey4", "TestValue4".getBytes());
        this.testPutBackupKVs(extendData);

        //
        String[] getKeys = {"TestKey1", "TestKey3", "TestKey4"};
        this.testGetBackupKVs(getKeys);

        this.testGetBackupKVKeys("Test");
        this.testQueryBackupKV("Test");
    }

    private void getCustomConfig() {
        //获取专家模式中的配置文件，获取自定义配置信息。
        this.customConfig = this.sdk.getCustomConfig();
        log.info("get customConfig:{}", customConfig);
    }

    public String getMqttUsername() {
        if (customConfig == null || customConfig.isEmpty()) {
            log.error("getMqttUsername customConfig is null");
            return "";
        }

        final JSONObject mqttConfig = customConfig.getJSONObject("mqtt");
        final String username = mqttConfig.getString("username");
        log.info("get mqttConfig:{}, username:{}", mqttConfig, username);
        return username;
    }

    private void addNewProduct() {
        ProductMetadata productMetadata = new ProductMetadata(
                this.newProductId,
                "演示设备PID",
                "This a test product");
        this.sdk.addProduct(productMetadata);
        log.info("addProduct end, pid:{}", newProductId);
    }

    private void addNewDevice() {
        String newCid = "test_cid_x0001";    //演示子设备cid
        Map<String, Object> extendData = new HashMap<>();
        extendData.put("key1", "value1");
        extendData.put("key2", "value2");
        this.sdk.activeDevice(new DeviceMetadata(
                newCid,
                "测试设备1",
                this.newProductId,
                "安装位置",
                extendData
        ));
        log.info("activeDevice end, cid:{}", newCid);
    }

    private void modifyDeviceName() {
        String cid = "test_cid_x0001";    //演示子设备cid
        String newName = "cid_name_新";
        DeviceUpdateProperty updateProperty = new DeviceUpdateProperty(newName);
        this.sdk.setDeviceBaseAttr(cid, updateProperty);
        log.info("modifyDeviceName end, cid:{}， name:{}", cid, newName);
    }

    private void runAllActiveDevices() {
        Map<String, DeviceInfo> activeDevices = this.sdk.getActiveDevices();
        log.info("get activeDevices:{}", JSON.toJSONString(activeDevices));
        for (DeviceInfo dev : activeDevices.values()) {
            String devCid = dev.getId();
            DeviceShadow newDev = new DeviceShadow(devCid, dev.getName(), this.sdk);
            deviceMap.put(devCid, newDev);

            newDev.running();
            log.info("new device cid:{}, running...", devCid);
        }
    }

    private void testAlarmReport() {
        this.sdk.reportAlert(AlertLevel.Warn, "This is a test alarm!");
    }

    private void getGatewayInfo() {
        GatewayInfo gatewayInfo = this.sdk.getGatewayInfo();
        log.info("getGatewayInfo:{}", JSON.toJSONString(gatewayInfo));
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    private void testGetPutBackupKV(String key, byte[] value) {
        byte[] value1 = this.sdk.GetBackupKVOne(key);
        if (value1 == null) {
            Boolean ret = this.sdk.PutBackupKVOne(key, value);
            String sValue = new String(value, StandardCharsets.UTF_8);
            log.info("PutBackupKVOne key:{}, value:{}, ret:{}", key, sValue, ret);
        } else {
            String sValue1 = new String(value1, StandardCharsets.UTF_8);
            log.info("GetBackupKVOne key:{}, sValue1:{}", key, sValue1);
        }
    }

    private void testDeleteBackupKV(String key) {
        String[] keys = {key};
        Boolean ret = this.sdk.DelBackupKV(keys);
        if (!ret) {
            log.warn("DelBackupKV key:{} fail", keys);
        } else {
            log.info("DelBackupKV key:{} success", keys);
        }
    }

    private void testPutBackupKVs(Map<String, byte[]> kvs) {
        Boolean ret = this.sdk.PutBackupKV(kvs);
        //String sValue = new String(value, StandardCharsets.UTF_8);
        log.info("PutBackupKV kvs:{}, ret:{}", kvs, ret);
    }

    private void testGetBackupKVs(String[] keys) {
        Map<String, byte[]> result = this.sdk.GetBackupKV(keys);
        if (result != null) {
            for (String key : result.keySet()) {
                String sValue = new String(result.get(key), StandardCharsets.UTF_8);
                log.info("GetBackupKV key:{}, value:{}", key, sValue);
            }
        }
    }

    private void testGetBackupKVKeys(String prefix) {
        String[] result = this.sdk.GetBackupKVKeys(prefix);
        if (result != null) {
            for (String key : result) {
                log.info("GetBackupKVKeys prefix:{} key:{}", prefix, key);
            }
        }
    }

    private void testQueryBackupKV(String prefix) {
        Map<String, byte[]> result = this.sdk.QueryBackupKV(prefix);
        if (result != null) {
            for (String key : result.keySet()) {
                String sValue = new String(result.get(key), StandardCharsets.UTF_8);
                log.info("QueryBackupKV key:{}, value:{}", key, sValue);
            }
        } else {
            log.info("QueryBackupKV prefix:{}, result is null", prefix);
        }
    }
}
