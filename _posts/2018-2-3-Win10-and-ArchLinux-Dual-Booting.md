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

## 前言

本机装着 `ArchLinux` ，突然想装双系统，玩儿游戏还是得用 `Windoge`

## 前期准备

- 一个 `Win10` 启动盘
- 一个 `Linux` 启动盘

## 配置过程

### 分区

一定要有一个启动分区，剩下的自己看着整就好。

<del>想装双系统的都会装单个系统->会装系统都知道怎么分区->看这篇博客的都知道怎么分区</del>

如果你已经安装了某个系统，并且在分区的时候将整个硬盘都分了（我就是这样），那就需要调整分区大小了。 `Win10` 用系统自带的存储管理中的压缩卷功能就好， `Linux` 的方法看 [如何在Linux中调整分区大小]()

### 装系统

[ArchLinux Installition](https://wiki.archlinux.org/index.php/Installation_guide)

`Win10` 就一路点就好，注意分区选择

先安装 `ArchLinux` 的情况下， `Win10` 会自动把启动文件（ `bootmgfw.efi` ）放到启动分区去，具体路径是 `/boot/EFI/Microsoft/Boot/` ，而 `grub` 在 `/boot/EFI/grub/` ，这些东西下一步会用到

### 配启动

如果你是开机时直接在 `BIOS` 选启动项的话，可以直接关掉这篇 `blog` 了

然而我做了 [Secure Boot]() ，开了 `BIOS` 锁，每次进 `BIOS` 都需要输密码，很麻烦，干脆加到 `grub` ，统一管理

首先，在 `/etc/grub.d/` 中添加（或修改）一个配置文件，让 `grub-mkconfig` 能将启动项写到 `/boot/grub/grub.cfg` 中。其实直接修改 `40_custom` 就好，自己添加需要注意文件名格式和 `+x`

添加的配置文件内容会被 `grub-mkconfig` 执行，把输出写到 `/boot/grub/grub.cfg` （没错，就是输出 `stdout` ）。所以内容要可执行（然后 [ArchLnux Wiki](https://wiki.archlinux.org/index.php/GRUB#Windows_installed_in_UEFI-GPT_Mode_menu_entry) 上写的那个似乎就不可执行）。例如我新建的配置文件（ `11_win10` ）的内容：

```bash
# Win10
cat << EOF
menuentry 'Windows 10' {
    echo 'Loading Windows 10...'
    insmod part_gpt
    insmod fat
    insmod search_fs_uuid
    insmod chain
    search --fs-uuid --set=root --hint-bios=hd1,gpt1 --hint-efi=hd1,gpt1 --hint-baremetal=ahci1,gpt1 90C0-DEF4
    chainloader /EFI/Microsoft/Boot/bootmgfw.efi
}
EOF
```

其中， `search` 行中的参数需要通过 `grub-probe` 获取，指令分别为：

```bash
grub-probe --target=fs_uuid /boot/EFI/Microsoft/Boot/bootmgfw.efi
grub-probe --target=hints_string /boot/EFI/Microsoft/Boot/bootmgfw.efi
```

就会得到 `90C0-DEF4` 和 `--hint-bios=hd1,gpt1 --hint-efi=hd1,gpt1 --hint-baremetal=ahci1,gpt1 ` 

你可以把命令到脚本里，每次都会自动获取最新的参数，然而我直接写死了，反正一般来说不会变。 

然后， `grub-mkconfig -o /boot/grub/grub.cfg` 。完了过后最好手动检查一下 `/boot/grub/grub.cfg` 中的内容是不是有你写的启动项

最后，重启，看看是不是成功了，没成功就找问题和解决问题吧

## 参考

[ArchLinux Installition](https://wiki.archlinux.org/index.php/Installation_guide)
- [Windows installed in UEFI-GPT Mode menu entry](https://wiki.archlinux.org/index.php/GRUB#Windows_installed_in_UEFI-GPT_Mode_menu_entry)
