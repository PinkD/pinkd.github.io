---
layout: post
title: openkeeper配置
description: 一番魔改
tags:
- Linux
categories: Linux
---

## 引言
据说[有人在github上喷openkeeper](https://github.com/purpleroc/OpenKeeper/issues/1)，让我们来看看openkeeper到底有多难配置。

## 0x00 openkeeper的历史

openkeeper是一个linux平台上的，基于终端的，用于解决重庆等地高校电信宽带PPPoE上网的程序集合。我们一般使用的校园宽带账户，并不是可以直接接入Internet的PPPoE账户，所以要想接入Internet，必须通过其他软件（或程序）将一般意义上的校园宽带账户，翻译成真正的PPPoE账户，再将数据传递到PPPoE程序，完成拨号。

开发者：重邮Linux协会

没错，以上借鉴的百度百科

## 0x01 下载

```bash
git clone https://github.com/purpleroc/OpenKeeper.git
```

或者直接去下吧。。。
据说还有[GUI版本](http://pan.baidu.com/s/1dDAUFa5)，我没用过，出问题不要找我= =


## 0x02 解压make
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

## 0x03 配置&拨号

```bash
sudo ok-config
```

按提示走就行了，网卡可以在<code>ifconfig</code>里面看，一般是ethx或者enp9sx

然后，拨号：

```bash
sudo ok
```

## 0x04 魔改

如果你自定义了DNS地址，需要在<code>/etc/ppp/pppoe.conf</code>中将PEERDNS设置为no。
这可能需要用到vim，用法[在这里](https://www.baidu.com/)

嘛，其实以前因为没找到这个选项，直接把pppoe-connect里面有关更改DNS的代码给删掉了。。。（大雾）

## 0x05 注意事项

openkeeper加密账户时用到了时间，所以请确认你的电脑的时间正确（误差5s内）。

因为openkeeper将加密后的帐号写进了配置文件，如果加密结果中出现了引号会炸掉，请等待+1s+1s+1s+1s+1s后重试。

## 0x06 卸载问题

请阅读install.sh，找出创建的文件，手动<code>rm -f</code>，这算一个前辈们没有考虑到的地方吧，有空去pull request一下。
