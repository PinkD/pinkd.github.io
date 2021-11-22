---
layout: post
title: 在Win 2008 Server上安装openvpn
description: 在Windows 2008 Server 上安装 OpenVPN
tags:
- Win
- Network
- Others
categories: Others
---

## 下载

OpenVPN可以在 [官网](https://openvpn.net/index.php/open-source/downloads.html) 直接下载

## OpenVPN的安装

### 安装

直接跟着安装程序的指引安装

### 手动添加和删除接口

如果你要多开，或者要使用多个需要 `TAP` 设备的软件（比如 [TunSafe](https://tunsafe.com/) 和 [tinc](https://www.tinc-vpn.org/) ），就可以使用 `addtap.bat` 和 `deltapall.bat` 来管理，其实就是使用 `tapinstall.exe` 命令安装和卸载设备，命令格式如下：

```
tapinstall.exe install OemVista.inf tap0901
tapinstall.exe remove tap0901
```

## OpenVPN的安装配置

参考 [官方教程](https://community.openvpn.net/openvpn/wiki/Easy_Windows_Guide)

过程为：

- 生成证书
- 修改配置文件
- 将他们放到对应的位置

注：
- 可以修改 `TAP` 接口的名字，然后在配置文件中加上 `dev-node dev_name` 即可，这样指定接口可以防止多个使用 `TAP` 的程序互相冲突



## 启动OpenVPN

### 使用OpenVPN GUI

打开 `OpenVPN GUI` ，选择配置文件并启动即可

### 命令行启动

```
openvpn --config server.ovpn
```

也可以通过服务启动

## 您的OpenVPN出现了问题

到了这一步，OpenVPN启动完成了，也能连接上了，但是你会发现，连上了也上不了网，情况是包只能到服务器，但是不能从服务器出去，如果是在 `Linux` 上，直接 `iptables -t nat -s xxx -j MASQUERADE` 一把梭就搞定了，但是 `win` 不自带 `NAT` 配置，需要手动配置

## 配置Win 2008 Server 的NAT

### 安装服务

服务器管理(Server Manager)->添加角色(Add Role)->网络策略和访问服务(Network Policy and Access Services)->远程访问服务和路由(Remote Access Service and Routing)

### 配置NAT

如果没有服务启用，先启用

![启用服务](/images/win2008OpenVPN1.jpg)

选择 `NAT` ，如果没有接口就添加，添加的对象为你的外网接口（本地连接）和OpenVPN的接口（本地连接 2）

![查看路由](/images/win2008OpenVPN2.jpg)

配置外网接口为公共接口，配置OpenVPN的接口为专用接口

![配置接口](/images/win2008OpenVPN3.jpg)

接口名可以在网络连接中查找（TAP）那个即为OpenVPN的接口

![查看接口](/images/win2008OpenVPN4.jpg)


然后再在客户端试试，应该就能连网了

## 其他

`#垃圾windoge`


## 参考链接

- [OpenVPN Download](https://openvpn.net/index.php/open-source/downloads.html)
- [TunSafe](https://tunsafe.com/)
- [Tinc](https://www.tinc-vpn.org/)
- [OpenVPN Easy Windows Guide](https://community.openvpn.net/openvpn/wiki/Easy_Windows_Guide)
- [OpenVPN Wiki](https://community.openvpn.net/openvpn/wiki)
- [Setup a VPN Server in Windows Server 2008 R2](http://www.geekyprojects.com/networking/how-to-setup-a-vpn-server-in-windows-server-2008-r2/)
- [Windows Server 2008 NAT基本配置](http://blog.51cto.com/hostmaoo/1177630)
- [Enable NAT on Windows Server 2008 R2](http://www.itgeared.com/articles/1345-enable-nat-routing-windows-2008-r2-tutorial/)