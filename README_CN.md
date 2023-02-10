[English](README.md) | [中文版](README_CN.md)

# 涂鸦边缘网关南向驱动程序 Java Demo

本次 Demo 演示通过涂鸦边缘网关（Tedge）提供南向驱动开发 Java SDK（tuya-tedge-driver-sdk-java），实现 Tedge 南向驱动程序开发，最终对接第三方智能设备。

## 编译工具

IntelliJ IDEA

## 发布镜像
* 环境准备：安装 docker
* 打包并发布：`./script/dockerbuild.sh v1.0.0`

## Demo 介绍

Tedge 有两种运行模式：DP 模型、物模型（TyLink）
* 该驱动程序 Demo 同时演示了物模型、DP 模型两种驱动
* 除非特别说明，Tedge 默认情况都运行在 DP 模型下
* 驱动程序功能介绍：
    - 标准的驱动程序开发范式
    - 驱动服务接口实现示例
    - 子设备激活示例
    - 子设备状态更新示例
    - 子设备 DP 上报示例
    - 子设备指令处理示例

### DP 模型

* 代码实现示例：`dpdemo/dpdriver`

### 接入 MQTT 设备

* 代码实现示例：`dpdemo/mqttdriver`

## 技术支持

- [涂鸦开发者](https://developer.tuya.com/)
- [涂鸦帮助中心](https://support.tuya.com/cn/help)
- [涂鸦技术工单](https://service.console.tuya.com/)