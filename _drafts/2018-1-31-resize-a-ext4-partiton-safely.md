---
layout: post
title: 无损调整EXT4分区大小
description: 无损调整EXT4分区大小
tags:
- Linux
categories: Linux
---

# 无损调整EXT4分区大小

## 前言 <del>（废话）</del>

因为突然想装双系统，本来把 `Win10` 装在机械硬盘的，实在是忍不了常年磁盘 `100%` 准备迁到 `SSD` 里（用的 `DiskGenius` 的分区备份还原功能，很棒）。然后现在 `SSD` 里已经装了 `ArchLinux` ，并且分区也是把空间分完了的，把分区整理好过后就准备开始调整分区大小。因为分区操作，所以有点方，就查了一堆资料，还在虚拟机做了实验，所以记录一下方法。不出意外下一个是双系统的安装和 `grub` 的配置

## 前期准备

- 一个 `Linux` 的启动盘（需要包含 `e2fsck` ， `fdisk` ， `resize2fs` 等命令）：已经挂载的分区没办法操作，所以需要在 `LiveCD` 里动手
- 一块不大不小的 <del>木板（划掉）</del> 硬盘
- 把分区里该清理的东西清理一下，尽量腾出空间（其实没必要，只是顺便）

## 开始操作

- 重启到 `LiveCD`
- `lsblk` 看看分区
- `e2fsck -f /dev/sda1` 检查分区
- `resize2fs /dev/sda1 100G` 调整分区文件系统到 `100G` ，需要配合下一步才能生效
- `fdisk /dev/sda` ，进去删掉( `d` ) `sda1` ，然后再新建（ `n` ），除了结束大小，其他全部默认就好，结束大小应该写 `+100G` ，保留 `EXT4` 签名那个我选的 `N` ，两个都试过，似乎没什么影响
- 再 `e2fsck -f /dev/sda1` 检查一下，没有错误就说明没问题了
- 如果有错误，可以删掉分区，重新创建一个跟原来的分区大小一样的分区，一般来说都不会翻车，可以像我一样在虚拟机里先试试

然后，你就可以在腾出来的空间里装 `Windoge` 了

## 参考

- [How to Shrink an ext2/3/4 File system with resize2fs](https://access.redhat.com/articles/1196333)

