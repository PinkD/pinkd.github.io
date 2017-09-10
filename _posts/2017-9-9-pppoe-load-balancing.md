---
layout: post
title: 多个pppoe负载均衡
description: Linux下多个pppoe负载均衡方案
tags:
- Linux
- Network
categories: Linux
---

# Linux下pppoe负载均衡方案


## 前言

- 有多个pppoe账号，但是每个带宽都比较小，因此就想多拨，做个负载均衡
- iptables的方法没有成功，但是我依旧会写下思路
- 想看成功的解决方案请直接拉到最下面
- 如果有大佬能指出iptables方法的问题，不胜感激，联系方式blog上就有

## 设备和需求

#### 设备

- pppx      ---->   多个pppoe拨号后的接口
- tun0      ---->   openvpn的接口，网段为 `10.8.8.0/24`
- eth0

#### 需求

- 从openvpn来的流量全都从pppoe的接口出去（因为机器都在内网，内网的地址直接自己添加静态路由即可）
- 多个pppoe出口实现负载均衡

## 基于iptables和iproute的路由选择

主要参考 [海运的博客](https://www.haiyun.me/archives/iptables-nth-mark-route-load.html) 和 [qiuske的ChinaUnix博客](http://blog.chinaunix.net/uid-13423994-id-3212414.html) 。文中是针对两个接口，多个的话自己手动改一下即可

### iptables配置

首先， `MASQUERADE` openvpn的网段：

```bash
iptables -t nat -A POSTROUTING -s 10.8.8.0/24 -j MASQUERADE
```

这是iptables最基础的应用，详情请自己搜索关键词 `iptables MASQUERADE` 

然后，给每条连接打上 `CONNMARK` :

```bash
iptables -t mangle -A PREROUTING -s 10.8.8.0/24 -m state --state NEW -m statistic --mode nth --every 2 --packet 0 -j CONNMARK --set-mark 1
iptables -t mangle -A PREROUTING -s 10.8.8.0/24 -m state --state NEW -m statistic --mode nth --every 2 --packet 1 -j CONNMARK --set-mark 2
```

解释一下：

- `mangle` 表在 `nat` 表之前生效
- `-m state --state NEW` ----> 匹配新建的连接
- `-m statistic --mode nth --every n --packet x` ----> 统计，每n个包，对第x个进行匹配
- `-j CONNMARK --set-mark x` ----> 交给 `CONNMARK` 处理，打上标记 x
- `CONNMARK` 打上的标记是针对连接的，由iptables管理的，因此，还需要将标记打到每个包

将连接上的标记打到每个包上：

```bash
iptables -t mangle -A PREROUTING -m connmark --mark 1 -j CONNMARK --restore-mark
iptables -t mangle -A PREROUTING -m connmark --mark 2 -j CONNMARK --restore-mark
```

解释：

- `-m connmark --mark x` ----> 对于 `CONNMARK` 标记为x的
- `-j CONNMARK --restore-mark` ----> 将连接的标记打到包上
- 在这里并没有加源地址和状态的过滤条件，因为所有的包都应该被检测（？）


参考 [海运的博客](http://www.haiyun.me/archives/iptables-mark-connmark.html) 和 [iptables-extensions的文档](http://ipset.netfilter.org/iptables-extensions.man.html)

### iproute配置

```bash
ip route add 10.8.8.0/24 dev tun0 table p0
ip route add default dev ppp0 table p0
ip route add 10.8.8.0/24 dev tun0 table p1
ip route add default dev ppp1 table p1

ip rule add fwmark 1 table p0
ip rule add fwmark 2 table p1
```

说明：

- table后可以接数字或者在 `/etc/iproute2/rt_tables` 中指定的名字
- `fwmark` 即为之前打在包上的标记，按照这个标记来走路由，就能实现按连接的负载均衡

### 出现的问题

- `traceroute` 能到 `pppx` 的路由地址，但是接下来的包全都在服务器端，并没有发回 `openvpn` 的客户端（抓包发现的）
- 在一台外网服务器上 `nc -vv -l -p 23333` ，客户端 `nc -vv x.x.x.x 23333` ，外网服务器端通过 `netstat` 可以看到连接的状态是 `SYN_RECV` 。也就是说，三次握手，第一个包收到了，发出了第二个包，在等待第三个包，然而这第二个包看起来并没有被客户端收到
- 将 `MASQUERADE` 分解开来，改成 `SNAT` + `DNAT` 发现 `SNAT` 有包被匹配到，而 `DNAT` 几乎没有

### 附加说明

这种方法比较古老了，效率也不高，但是思路是没有问题的，因此依旧想将这个方法实现。如果有大佬发现问题出在哪里欢迎联系

## iproute直接ECMP

就一句话：

```bash
ip route add default nexthop dev ppp0 weight 1 nexthop dev ppp1 weight 1
```

说明：

- `nexhop` 指定下一跳
- `dev` 指定接口
- `weight` 指定权重，全1就好

当然，别忘了

```bash
iptables -t nat -A POSTROUTING -s 10.8.8.0/24 -j MASQUERADE
```

BTW，遇到已经存在默认路由的，删掉就好

说明：

- 这个方法来自 [49](http://49.gs/) 
- `pppoe` 下不用指定 `via` 参数，因为 `pppoe` 本身就是点对点，不像以太网那样靠广播，需要手动指定网关。这个知识点来自49师傅和另外一位不愿意透露联系方式的大佬（大雾）

## 参考

- [海运的博客1](https://www.haiyun.me/archives/iptables-nth-mark-route-load.html)
- [qiuske的ChinaUnix博客](http://blog.chinaunix.net/uid-13423994-id-3212414.html)
- [海运的博客2](http://www.haiyun.me/archives/iptables-mark-connmark.html) 
- [iptables-extensions的文档](http://ipset.netfilter.org/iptables-extensions.man.html)
