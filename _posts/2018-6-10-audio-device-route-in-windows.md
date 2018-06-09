---
layout: post
title: Windows下音频设备路由的实现
description: Windows下音频设备路由的实现（内录）
tags:
- Music
- Others
categories: Others
---

# Windows下音频设备路由的实现

## 软件

[Voice Meeter](https://www.vb-audio.com/Voicemeeter/index.htm)

我使用的是高级版的 [banana](https://www.vb-audio.com/Voicemeeter/banana.htm) ，多了几个接口，功能更强大

## 目的

- 麦克风能正常录音，且不为监听状态
- 能将某些软件内录，比如 `foobar2000` 和 `PotPlayer` ，且能输出到外设
- 全局声音不内录，但是能输出到外设

## 实现步骤

### Voice Meeter

- 1.修改默认输入设备为软件提供的虚拟声卡（ `Voice Meeter` 的话只有一个，其实是软件的虚拟输出）

    ![](/images/default_record_device.jpg)

- 2.配置 `Voice Meeter`

    引用官方的一张图：
    ![](/images/VoicemeeterAudioMixer.jpg)
  - 将 `HARDWARE INPUT 1` 设为麦克风，将其输出的 `A` 取消， `B` 选中（输出到虚拟设备）
  - 将 `HARDWARE INPUT 2` 设为立体声混音器，将其输出的 `A` 选中， `B` 取消（输出到实体设备）
  - 将 `VIRTUAL INPUT 1` 输出的 `A` `B` 均选中（同时输出到虚拟和实体设备）
  - 将 `HARDWARE OUTPUT A1` 设为 `MME` 模式的扬声器（ `WDM` 会冲突）

- 3.配置 `PotPlayer` 或者 `foobar`

  - 将他们的输出设备改为软件提供的虚拟声卡（其实是软件的虚拟输入）

### Voice Meeter Banana

- 1.同上

- 2.修改默认输出设备为软件提供的虚拟声卡（软件的输入，注意不要与第3步中虚拟设备相同）

    ![](/images/default_play_device.jpg)

- 3.配置 `Voice Meeter Banana`
    
    ![](/images/config_voice_meeter.jpg)
  - 如图
  - 将 `HARDWARE INPUT 1` 设为麦克风，将其输出的 `B1` 选中（输出到虚拟设备1）
  - 将 `VIRTUAL INPUT 1` 输出的 `A1` `B1` 均选中（同时输出到虚拟和实体设备1）
  - 将 `VIRTUAL INPUT 2` 输出的将其输出的 `A1` 选中（输出到实体设备1）
  - 将 `HARDWARE OUTPUT A1` 设为扬声器，不用担心冲突

- 4.同上3

## 如何录音

按理说因为设置了默认录音设备，所以直接使用即可，如果不能正常使用，请手动选择。有的软件不支持选择，又无法正常工作的（如 `Telegram` ），请重启软件再尝试

## 效果

![](/images/test_record_with_au.jpg)

后面那几下就是我拍了几下麦的录进去的声音，前面是 `foobar` 播放的音乐

## 一些问题

- 注意设备独占问题
- 音频经过多次输入输出是否有损（位置）

## 参考

- [Voice Meeter官网](https://www.vb-audio.com/)
- [WASAPI和设备独占](https://baike.baidu.com/item/WASAPI)
