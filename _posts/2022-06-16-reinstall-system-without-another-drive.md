---
layout: post
title: 不使用其他盘的情况下原地重装 Linux
description: 不使用其他盘的情况下原地重装 Linux
tags:
- Linux
categories: Linux
---

# 思路

在没有其他盘的情况下，无法通过启动 live cd 来对现有系统的盘进行缩容和分区
因此我们需要想办法在不挂载盘的情况下对系统盘进行操作，缩容和分区完成后，就能重新分区，然后就和正常安装系统的流程一样了

# 不挂载盘的情况下进入系统

## initramfs

不知道你在启动系统时是否遇到过挂载 root 分区失败，这时候你就会进入一个只读的文件系统，如果你在这个只读文件系统中成功挂载了 root 并退出，就能继续启动系统

这个只读的文件系统只有非常有限的一部分功能，那么它是从哪里来的呢

它的名字叫 [initramfs][1] ，你可以通过自定义 `initramfs` 往里面添加你想要的东西，这样就能在挂盘之前做一些操作

## 开工

既然我们找到了方法，那就开干

archlinux 中是用 [mkinitcpio][2] 来管理 `initramfs` 的，它的 `HOOK` 功能能自动在创建 `initramfs` 的时候加入你想要的二进制文件

比如我这里就需要这些命令：

 `cat /etc/initcpio/install/resizefs`

```bash
#!/usr/bin/ash

build() {
    add_binary e2fsck
    add_binary resize2fs
    add_binary fdisk
    add_binary lsblk
}

# vim: set ft=sh ts=4 sw=4 et:
```

然后执行：

```bash
# dry run
mkinitcpio -A resizefs
# generate img
mkinitcpio -A resizefs -g /boot/initramfs-linux-resizefs.img
```

生成一个专用的 `initramfs` ，然后改长 grub 超时，重启系统，进入 grub 界面，选择一个启动项， `e` 进去编辑，删掉 `vmlinuz` 的 root 参数，让内核停在 `initramfs` ，进去后系统就会在 `initramfs` 中，盘没有被挂载，直接操作缩容盘就可以了，具体可以参考我之前写的 [无损调整EXT4分区大小][3]

大致命令是：

```bash
fsck.ext4 -f /dev/sda1
resize2fs /dev/sda1 10G
fsck.ext4 /dev/sda1
```

缩容完成后挂载该分区到 `/new_root` 然后 `exit` 就能进系统了

```bash
mount /dev/sda1 /new_root
# then ctrl+d
```

在系统里，格式化你需要的分区，然后挂上，然后向里面装系统即可

如果是原地重装系统可以直接使用如下命令：

```bash
# perform a full system backup
rsync -zzaAXHSvP --exclude={"/dev/*","/proc/*","/sys/*","/tmp/*","/run/*","/mnt/*","/media/*","/lost+found"} / /mnt/
```

然后 `chroot` 进去配启动， `grub-install` `grub-mkconfig` `mkinitcpio -P` 一把梭，然后记得改 `/etc/fstab` ，重启，你就应该在新的系统里了

接下来就需要把分区扩回去， `ext4` 应该能用和缩容类似的方法扩回去，需要自己搜索一下，我这里是用的 `btrfs` ，无法直接扩容分区，但是可以直接将新分区加到现有的文件系统里，连手动扩容的功夫都省了

# 另一种可能的方案

下载 archiso ，然后 `dd if=arch.iso of=/dev/sda` ，重启，进服务器远程，然后像正常装系统一样装。但是这种方案会直接抹掉盘上的数据

另外，还需要注意的是， **盘上 archiso 所在的部分是暂时不能动的** ，所以分区需要建到后半块盘上，装完扩容回去即可

# 参考链接

- [initramfs][1]
- [mkinitcpio][2]
- [无损调整EXT4分区大小][3]

[1]: https://en.wikipedia.org/wiki/Initial_ramdisk
[2]: https://wiki.archlinux.org/title/Mkinitcpio
[3]: https://blog.pinkd.moe/linux/2018/01/31/resize-a-ext4-partiton-safely
