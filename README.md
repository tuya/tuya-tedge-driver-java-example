[English](README.md) | [中文版](README_CN.md)

# TEdge Southbound Driver Demo for Java

This demo describes how to use the SDK for Java named `tuya-tedge-driver-sdk-java` provided by Tuya IoT Edge Gateway (TEdge) to develop a southbound driver and access third-party smart devices.

## Compiler

IntelliJ IDEA

## Release image

1. Prepare the environment: install docker
2. Package and release image: `./script/dockerbuild.sh v1.0.0`

## Demo introduction

TEdge supports two running modes: DP model and things data model (TuyaLink)
* The drivers for both the DP model and TuyaLink model are described in the following samples.
* Unless otherwise specified, TEdge runs with the DP model.
* The following driver features are supported in the samples:
    - Standard driver programming
    - Implementation of the driver service interface
    - Sub-device activation
    - Sub-device status update
    - Sub-device DP reporting
    - Sub-device command processing

### DP model

* Sample: `dpdemo/dpdriver`

### Integrate with MQTT devices

* Sample: `dpdemo/mqttdriver`

## Technical support

- [Tuya Developer Platform](https://developer.tuya.com/)
- [Help Center](https://support.tuya.com/en/help)
- [Service & Support](https://service.console.tuya.com/)