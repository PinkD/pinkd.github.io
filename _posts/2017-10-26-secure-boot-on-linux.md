---
layout: post
title: Linux下的SecureBoot配置
description: Linux下的SecureBoot配置，以及注意事项
tags:
- Linux
- Security
categories: Linux
---

# Linux下的SecureBoot配置

算是划水+笔记吧

## 为什么要使用SecureBoot

[Microsoft](https://technet.microsoft.com/en-us/library/hh824987.aspx) 上是这样说的：

```
Secure Boot is a security standard developed by members of the PC industry to help make sure that your PC boots using only software that is trusted by the PC manufacturer.

When the PC starts, the firmware checks the signature of each piece of boot software, including firmware drivers (Option ROMs) and the operating system. If the signatures are good, the PC boots, and the firmware gives control to the operating system.
```

- 总结一下，大概就是：

`SecureBoot` 在你系统启动前会对内核等底层的东西进行签名验证。验证通过则继续启动，验证失败无法进去系统并弹出提示。

- 达到的效果就是：

当你的内核遭到修改（被植入后门什么的）时， `SecureBoot` 会阻止系统启动，这样就能防止那些图谋不轨的人进入系统。

49：配合全盘加密食用，味道更佳。

## 谈谈UEFI

`UEFI` (可扩展固件接口)负责加电自检、联系操作系统以及提供连接操作系统与硬件的接口 <sup>[wikipedia]</sup> 。说白了就是用来替代BIOS的。具体参考 [Wikipedia](https://zh.wikipedia.org/wiki/%E7%B5%B1%E4%B8%80%E5%8F%AF%E5%BB%B6%E4%BC%B8%E9%9F%8C%E9%AB%94%E4%BB%8B%E9%9D%A2)

## 杂谈

据说因为巨硬给 `EFI` 做出了不少的贡献，所以 `EFI` 的文件格式是 `PE` 而不是 `ELF` 。而且最开始的部分电脑只能使用厂商内置的key和微软的key进行签名，导致Linux下根本无法配置 `SecureBoot` 。[阮一峰有提到过](http://www.ruanyifeng.com/blog/2013/01/secure_boot.html)

## 为Linux配置SecureBoot

**！！请确保你的 `BIOS` 支持 `SecureBoot` 中的自定义密钥选项！！** ，不支持我也没办法。

### 参考 [Ubuntu wiki](https://wiki.ubuntu.com/UEFI/SecureBoot) 

没错，代码都是复制粘贴的

### 创建自己的key

```bash
openssl genrsa -out test-key.rsa 2048
openssl req -new -x509 -sha256 -subj '/CN=test-key' -key test-key.rsa -out test-cert.pem
openssl x509 -in test-cert.pem -inform PEM -out test-cert.der -outform DER
```

你也可以使用你自己的RSA私钥生成一个key。用rsa的key登录ssh的人应该都知道怎么生成吧（然而并不。不知道就搜呗，就像我）

### 给内核签名

```bash
sbsign --key test-key.rsa --cert test-cert.pem --output grubx64.efi /boot/efi/efi/ubuntu/grubx64.efi
cp /boot/efi/efi/ubuntu/grubx64.efi{,.bak}
cp grubx64.efi /boot/efi/efi/ubuntu/
```

目录什么的需要自己改成自己的。更改 `/boot/` 下的文件需要 `root` 权限，记得 `sudo` 。以上命令也可以对内核文件使用。

### 配置自动签名

每次更新内核都要手动签名会累死的。在 `ArchLinux` 里面直接可以添加 `hook` ，具体看 [这里](https://wiki.archlinux.org/index.php/Pacman_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)#Hooks) 。将写好的 `hook` 放到 `/etc/pacman.d/hooks/` 即可。 `Ubuntu` 下参考 `/etc/apt/apt.conf.d/` 目录下的配置（并没有具体尝试）。 `Pacman` 的配置如下：

```
[Trigger]
Type = Package
Target = linux-xxx # your kernel package name
Operation = Install
Operation = Upgrade

[Action]
Description = Secure Boot Sign
When = PostTransaction
Exec = # sign your vmlinuz here
Depends = sbsigntools
```

### 有个问题

签名操作不能对 `initramfs` 进行签名，因此需要将 `initramfs` 和内核文件 `vmlinuz` 打包在一起一并签名。然而我并没有做这一步，过程可以在 [这儿](https://bentley.link/secureboot/) 找到。这篇文章写得很详细（但是太TMD折腾了，所以搞到上一步就没搞了）。

## 参考

- [Microsoft对SecureBoot的解释](https://technet.microsoft.com/en-us/library/hh824987.aspx)
- [Wikipedia](https://zh.wikipedia.org/wiki/%E7%B5%B1%E4%B8%80%E5%8F%AF%E5%BB%B6%E4%BC%B8%E9%9F%8C%E9%AB%94%E4%BB%8B%E9%9D%A2)
- [Ubuntu Wiki - SecureBoot](https://wiki.ubuntu.com/UEFI/SecureBoot)
- [ArchLinux Wiki - Pacman](https://wiki.archlinux.org/index.php/Pacman_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87)#Hooks)
- [ArchLinuxSecureBoot](https://bentley.link/secureboot/)
- [阮一峰的网络日志](http://www.ruanyifeng.com/blog/2013/01/secure_boot.html)