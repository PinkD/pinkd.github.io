---
layout: post
title: ArchLinux 和 Win10 双系统
description: ArchLinux 和 Win10 双系统安装和遇到的坑
tags:
- Linux
- grub
categories: Linux
---

# ArchLinux 和 Win10 双系统

## 前言 <del>（废话）</del>

本机装着 `ArchLinux` ，突然想装双系统

然后现在 `SSD` 里已经装了 `ArchLinux` ，并且分区也是把空间分完了的，所以需要先 [调整分区]() 

调整完过后就可以开始装 `Windoge` 了，这个跳过
 
本来之前是把 `Win10` 装在机械硬盘的，实在是忍不了常年磁盘 `100%` 迁到了 `SSD` 里（用的 `DiskGenius` 的分区备份还原功能，很棒）

装好了过后就开始配置统一的启动，我主系统是 `ArchLinux` ，所以用的 `grub`


## 前期准备

- 一个 `Win10` 启动盘，一个 `Linux` 启动盘
- 

## 参考

- [How to Shrink an ext2/3/4 File system with resize2fs](https://access.redhat.com/articles/1196333)

