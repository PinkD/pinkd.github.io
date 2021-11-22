---
layout: post
title: 配置一个DFS
description: 配置一个分布式文件系统
tags:
- Others
categories: Others
---

## 需求

一个自带冗余备份的分布式文件系统

## 常见的DFS

见 [ArchLinux Wiki][1] ，还有一个很常见的 [Hadoop的DHFS][2]

## MooseFS

大概浏览了一下几个 `DFS` ，挑了一个文档比较全，源码也容易找到， ~~（更重要的是，Arch源里打包了的）~~ ， [MooseFS][3]

## 结构

`MooseFS` 分几个部分，分别是：
- `master` ：负责管理所有节点，可以有多个。需要较大内存
- `metalogger` ：负责备份 `master` 的数据，在 `master` 挂掉的时候可以暂时代替其工作（如果你只部署了一个 `master` ，最好部署一个 `metalogger` ）。尽量达到 `master` 节点配置需求
- `chunk` ：数据保存服务器，数据就存在这儿。需要较大硬盘
- `cgi` ：可以通过 `web` 查看整个集群的状态

## 安装

### ArchLinux

```bash
pacman -Sy moosefs
```

### Debian

参考 [安装指南][4] 

### 源码编译

```bash
git clone https://github.com/moosefs/moosefs
cd moosefs
./linux_build.sh
make install
```

注意：我直接从 `pacman` 安装的，源码安装只在容器里编译安装过，并没有实际运行

## 配置

参考官方的 [安装指南][4] 和 [用户指南][5]


首先，配置 `mfsmaster` ：

在 `/etc/hosts` 中添加：

```
YOUR_MASTER_IP_1 mfsmaster
YOUR_MASTER_IP_2 mfsmaster
```

然后，将 `/etc/mfs` 下的所有 `*.cfg.sample` 改为 `*.cfg` ，大概是：

```bash
# 一行：
for f in /etc/mfs/*.sample; do mv -- "$f" "${f%.sample}" ;done
# 或者分开写：
for f in /etc/mfs/*.sample
    do mv -- "$f" "${f%.sample}" 
done
```

几个服务和对应的配置文件：

- `master` : `mfsmaster.cfg` 和 `mfsexports.cfg`
- `chunk` : `mfschunkserver.cfg` 和 `mfshdd.cfg`
- `metalogger` : `mfsmetalogger.cfg`
- 其他: `mfsmount.cfg` 、 `mfstopology.cfg`

配置文件里全都有注释，如果使用默认，需要配的只有：
- `mfshdd.cfg`

在其中添加对应（虚拟）硬盘位置，没有挂载过的目录其实也可以，但是需要指定大小。详细配置请看 [安装向导][5] 的 `Chunkservers Installation` 部分。格式如下：

```
/mnt/mfschunks1 20GiB
```


在启动 `master` 之前需要先手动初始化一个文件：

```bash
cp /var/lib/mfs/metadata.mfs.empty /var/lib/mfs/metadata.mfs
```

启动所有节点后就可以在客户端进行 `dfs` 的挂载了：

```bash
mount -t moosefs ip:port:/ /mnt
```

向里面丢东西时速度会比较慢（取决于网速，如果你是千兆以上的网，当我没说）

## 一些心得

- 使用 `mfsxxx -f start` 可以让服务前台运行，就可以更清晰看到日志。 `mfsmaster` 还可以加 `-xx` 以查看详细日志，这对调试非常有帮助
- 在你安装过程中，频繁更改 `master` 和 `chunk` ，会出现 [这个问题][6] ，按回答的方法删掉相关文件就好
- 有一个 `goal` 参数可以设置文件备份的次数，使用 `mfssetgoal` 和 `mfsgetgoal` 来查看和更改，也可以在 `mfsexports.cfg` 里添加挂载时参数 `goal=x`

## 我的搭建方案

采用 [docker][7] ，每个模块编写一个 [Dockerfile][8] ，直接到指定机器上部署即可。其中 ~~（最简单的）~~ 一个 `Dockerfile` 如下：

```Dockerfile
FROM archlinux/base
MAINTAINER PinkD

# set mirrorlist and install moosefs
ADD mirrorlist /etc/pacman.d/mirrorlist
RUN pacman -Sy --noconfirm moosefs && rm -r /var/cache/ && rm -rf /var/lib/pacman/sync

EXPOSE 9425/tcp

#start service
CMD ["/usr/bin/mfscgiserv", "-f", "start"]
```

## 参考链接

- [ArchLinux Wiki DFS][1]
- [ArchLinux Wiki Hadoop][2]
- [MooseFS][3]
- [MooseFS Installation Document][4]
- [MooseFS User Manual Document][5]
- [MooseFS Github Issue][6]
- [Docker][7]
- [Dockerfile][8]

   [1]:   https://wiki.archlinux.org/index.php/File_systems#Clustered_file_systems
   [2]:   https://wiki.archlinux.org/index.php/Hadoop
   [3]:   https://moosefs.com/
   [4]:   https://moosefs.com/Content/Downloads/moosefs-installation.pdf
   [5]:   https://moosefs.com/Content/Downloads/moosefs-3-0-users-manual.pdf
   [6]:   https://github.com/moosefs/moosefs/issues/74
   [7]:   https://www.docker.com/
   [8]:   https://docs.docker.com/engine/reference/builder/


