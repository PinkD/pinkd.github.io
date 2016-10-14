1---
layout: post
title: openkeeper配置
description: 一番魔改
tags:
- Linux
categories: Linux
---

## 引言
据说[有人在github上喷openkeeper](https://github.com/purpleroc/OpenKeeper/issues/1)，让我们来看看openkeeper到底有多难配置。

## 0x00 下载

```bash
git clone https://github.com/purpleroc/OpenKeeper.git
```

或者直接去下吧。。。
据说还有[GUI版本](http://pan.baidu.com/s/1dDAUFa5)，我没用过，出问题不要找我= =


## 0x01 解压make
如果你是压缩包：

```bash
tar -zxvf openkeeper-cli-1.0.tar.gz
```

如果你直接就是文件夹就不用了。
然后，

```bash
cd openkeeper-cli-1.0
 # 如果你的文件夹里有64/32，按照自己的系统架构cd进去
sudo chmod +x *.sh
sudo ./install.sh
```

## 0x02 配置&拨号

```bash
sudo ok-config
```

按提示走就行了，网卡可以在<code>ifconfig</code>里面看，一般是ethx或者enp9sx

然后，拨号：

```bash
sudo ok
```

## 0x03 魔改

如果你自定义了DNS地址，需要在<code>/etc/ppp/pppoe.conf</code>中将PEERDNS设置为no。
这可能需要用到vim，用法[在这里](https://www.baidu.com/)

嘛，其实以前因为没找到这个选项，直接把pppoe-connect里面有关更改DNS的代码给删掉了。。。（大雾）

## 0x04 注意事项

因为openkeeper将加密后的帐号写进了配置文件，如果加密结果中出现了引号会炸掉，请等待+1s+1s+1s+1s+1s后重试。

## 0x05 卸载问题

请阅读install.sh，找出创建的文件，手动<code>rm -f</code>，这算一个前辈们没有考虑到的地方吧，有空去pull request一下。
