---
layout: post
title: 配置一个兼容arm的x86的Android虚拟机
description: 配置一个兼容arm的x86的Android虚拟机
tags:
- Android
- VM
categories: Android
---

## 需求

一个效率高的Android虚拟机，还支持arm

为啥不用avd呢？

那货速度虽然还不错，但是太耗内存和CPU，再加上AS和Chrome，直接内存爆炸

## 需要的东西

- [Virtual Box][1]
- [Android x86][2]
  - x86版的Android，而且可以直接硬盘安装，而不是刷入镜像
- [houdini][3]
  - 动态解释ARM指令到x86的库，用来兼容armabi，在内置脚本里也有提供下载，所以并不会在这儿下载

## 安装Android

在官网下一个镜像，在VBox中安装。没错，就像安装Linux那样安装就行了，记得把system设置成rw，安装过程中会提示，重启过后就可以进入系统了

## 配置ARM兼容

- `adb` 连上，或者直接在内置终端里 `su` ，然后 `which enable_nativebridge` 
- 然后把这个文件复制到 `/data/local/tmp/` 下，然后 `vi` 它，在 `wget $url` 那句前添加 `echo $v  $url && exit` ，运行它（ `/data/local/tmp` 目录下），记下版本和地址，然后这个复制过来的脚本就可以删了。把文件手动从地址下载下来， `adb push` 进去，然后 `cp /data/local/tmp/houdini.sfs /system/etc/houdini$v.sfs` ，其中 `$v` 就是前面的版本，然后运行原脚本，就可以挂载兼容库。检查方法是看目录 `/system/lib/arm` 是否存在
  - 如果你做了透明代理，应该可以直接跑脚本，会自动下载和部署
- 去 设置->应用兼容性 中开启兼容模式
- 装个QQ试试？

## 让屏幕横过来

用了一段时间，发现默认是横向布局，非常蛋疼，于是又想办法让它竖过来

- 设置VBox额外分辨率，并在grub中配置，有点蛋疼，参考 [这个][5]
- 设置Android的分辨率和DPI，参考 [这个][6] 和 [这个][7]
- 推荐的配置是VBox分辨率高度为你的屏幕高度，宽度按 `16:9` 算吧，然后设备内就 `1080x1920` 就好

## 实际效果

- 效率很不错，至少各项占用比avd低不少
- 操作有点蛋疼，传文件只能用adb，操作只能鼠标，不能像avd那样xjb点
- 也许可以用来玩儿游戏？（没试过

## 注意事项

- 如果发现鼠标操作有问题，请关闭鼠标捕获
- `houdini` 库只做了 `armabi` 的兼容， `v7a` 和 `v8a` 可能会炸
- 权限问题，请将涉及到的文件都 `chown 0:0`

## 参考链接

- [Virtual Box][1]
- [Android x86][2]
- [houdini][3]
- [Android-X86集成houdini][4]
- [How can I get VirtualBox to run at 1366x768?][5]
- [How to Change the Screen Resolution + DPI on Android x86][6]
- [How to Change Screen Resolution on Android][7]

   [1]:   https://www.virtualbox.org/wiki/Downloads
   [2]:   http://www.android-x86.org/download
   [3]:   https://github.com/Rprop/libhoudini
   [4]:   https://www.jianshu.com/p/73198c3bfbb1
   [5]:   https://superuser.com/questions/443445/how-can-i-get-virtualbox-to-run-at-1366x768
   [6]:   https://helloworldhelp.wordpress.com/2018/08/22/how-to-change-the-screen-resolution-dpi-on-android-x86/
   [7]:   https://joyofandroid.com/change-screen-resolution/ 