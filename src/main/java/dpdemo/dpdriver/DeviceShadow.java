package dpdemo.dpdriver;

import lombok.Data;
import tuya.tedge.driver.sdk.base.model.DeviceStatus;
import tuya.tedge.driver.sdk.dpmodel.DpDriverApi;
import tuya.tedge.driver.sdk.dpmodel.model.CommandRequest;
import tuya.tedge.driver.sdk.dpmodel.model.DataType;
import tuya.tedge.driver.sdk.dpmodel.model.DpData;
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Data
public class DeviceShadow {
    private String cid;
    private String name;
    private DpDriverApi sdk;
    private Timer timer;

    public DeviceShadow(String cid, String name, DpDriverApi sdk) {
        this.cid = cid;
        this.name = name;
        this.sdk = sdk;
        this.timer = new Timer();
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //定时检测并更新设备状态
            checkDevStatus(true);

            //使用示例：上报设备DP点当前值
            reportDPS();
        }
    };

    public void running() {
        timer.schedule(timerTask, 1000, 60 * 1000);
    }

    public void stop() {
        timer.cancel();
    }

    public void checkDevStatus(Boolean status) {
        //TODO: implement me...
        //检测真实设备在线状态, 设备状态由驱动负责维护;
        //1.调用设备提供的接口；2.或者通过其它方式判断设备状态; 3.驱动定时向Tedge上报设备状态
        Boolean isOnline = status;

        DeviceStatus devStatus = new DeviceStatus();
        if (isOnline) {
            final ArrayList<String> onlineList = new ArrayList<>();
            onlineList.add(this.cid);
            devStatus.setOnline(onlineList);
            log.info("reportDeviceStatus online cids:{}", onlineList);
        } else {
            final ArrayList<String> offlineList = new ArrayList<>();
            offlineList.add(this.cid);
            devStatus.setOffline(offlineList);
            log.info("reportDeviceStatus offline cids:{}", offlineList);
        }
        this.sdk.reportDeviceStatus(devStatus);
    }

    public void reportDPS() {
        reportDp16(true);
        reportDp31("This is a test warning!");
        reportDp46();
        reportDp65("online");
    }

    //处理云端或TEdge下发的DP消息
    public void processDpMessage(CommandRequest cmdRequest) {
        Map<String, Object> data = cmdRequest.getData();
        Object dps = data.get("dps");

        String dpss = JSON.toJSONString(dps);
        log.info("processDpMessage cid:{}, dpss:{}", cid, dpss);

        //1.调用设备提供的接口，将云端下发的指令发送到设备
        //TODO: implement me
        //......
    }

    private void reportDp16(Boolean status) {
        //TODO: implement me...
        //获取设备dpId真实的数据

        String dpId = "16";
        final ArrayList<DpData> dpData = new ArrayList<>();
        DpData boolDp = new DpData(dpId, DataType.TypeBool, status);
        dpData.add(boolDp);
        this.sdk.reportWithDPData(cid, dpData);
        log.info("report bool dpId:{}, cid:{}, value:{}", dpId, cid, status);
    }

    private void reportDp46() {
        //TODO: implement me...
        //获取设备dpId真实的数据

        String dpId = "46";
        final ArrayList<DpData> dpData = new ArrayList<>();
        Long lValue = 100L;
        DpData valueDp = new DpData(dpId, DataType.TypeValue, lValue);
        dpData.add(valueDp);
        this.sdk.reportWithDPData(cid, dpData);
        log.info("report value dpId:{}, cid:{}, value:{}", dpId, cid, lValue);
    }

    private void reportDp31(String sValue) {
        //TODO: implement me...
        //获取设备dpId真实的数据

        String dpId = "31";
        final ArrayList<DpData> dpData = new ArrayList<>();
        DpData stringDp = new DpData(dpId, DataType.TypeString, sValue);
        dpData.add(stringDp);
        this.sdk.reportWithDPData(cid, dpData);
        log.info("report string dpId:{}, cid:{}, value:{}", dpId, cid, sValue);
    }

    private void reportDp65(String eValue) {
        //TODO: implement me...
        //获取设备dpId真实的数据

        String dpId = "65";
        final ArrayList<DpData> dpData = new ArrayList<>();
        DpData enumDp = new DpData(dpId, DataType.TypeEnum, eValue);
        dpData.add(enumDp);
        this.sdk.reportWithDPData(cid, dpData);
        log.info("report enum dpId:{}, cid:{}, value:{}", dpId, cid, eValue);
    }
}
