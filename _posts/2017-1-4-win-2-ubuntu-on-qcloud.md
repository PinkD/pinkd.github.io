---
layout: post
title: 腾讯云中win转ubuntu
description: 其它linux操作类似
tags:
- Linux
- Others
categories: Others
---

### 腾讯云50G系统盘Linux

其实就是硬盘安装linux，只是有一些需要注意的地方

**如果你没有安装过linux，请先用搜索引擎了解一下整个过程**

#### 备份

- 因为腾讯云不是使用的DHCP，所以重转前请备份好ip，`ipconfig /all`即可。（如果多次重装，ip可能会变，最好每次都备份。虽然在腾讯云的控制台能找到IP。~~然而我重装完了才发现~~）
- 还有就是你服务器上的业务，数据等（如果你是刚从linux提交工单换的win，当我没说）

#### 需要的东西

- 镜像
- EasyBCD
- 系统自带磁盘管理器

#### 重装前的操作

- 下载镜像和EasyBCD（镜像我用的[ubuntu 16.04](https://mirrors.tuna.tsinghua.edu.cn/ubuntu-releases/16.04/ubuntu-16.04-server-amd64.iso)）
- 将C盘压缩出一个大于镜像的分区，**格式化为FAT32**，将下载的镜像丢进去
- 打开EasyBCD->添加新条目->ISO->选择D盘的iso文件->添加完成
- 重启，快速进入控制台，最好在35s以内（重启5s，启动等待30s），从你添加的引导进入安装界面

#### 开始安装Linux

- 选择English（为啥不用中文？[原因在这儿](http://www.linuxdiyf.com/linux/20025.html)。~~没错，我被坑过~~）
- 按照安装linux的正常流程安装，GG在搜索CDROM界面，我们需要手动挂载镜像
- ctrl+alt+fx进入第x个tty（当前为第一个）


#### 挂载镜像

```bash
mkdir /iso && mount /dev/vda3 /iso
mount -t iso9660 /iso/ubuntu-16.04-server-amd64.iso /cdrom
```

文件夹名和文件名自己替换

然后回到tty1，取消自动搜索CDROM，回到安装总流程，选择下一项

PS:这一步有些玄学问题，cdrom的挂载会莫名其妙消失，自己多搞几次就行了。~~不用像我那样傻傻的去重装~~

#### 手动配置IP

自动DHCP失败，将之前备份的ip信息依次填入

#### 分区

先把1、2分区删了，（镜像所在的）分区3先不删，然后新建1个（/）或者2个分区(/、swap)，也可以在重启后把镜像所在的分区当swap。镜像那个分区我（格了10G）拿来做单独的数据分区了

#### 还有问题

似乎会卡在安装软件那儿，先装grub，back，再装那堆基本软件就可以了（不知道是不是个例，~~又是一次重装~~）

#### 完结撒花

重启，enjoy    
换成[大清的镜像源](https://mirrors.tuna.tsinghua.edu.cn/help/ubuntu/)，效果更佳



