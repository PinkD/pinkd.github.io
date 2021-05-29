---
layout: post
title: 让 Linux 成为 rdp 服务器
description: 让 Linux 成为 rdp 服务器
tags:
- Linux
categories: Linux
---

# 前言

居然三年没更新 blog 了，水一篇

# 用到的软件

- [xrdp][1]
- [xorgxrdp][2]

# 安装

## ArchLinux

- [xrdp-git][3]
- [xorgxrdp-git][4]

## 其他

从源码编译安装

# 配置

关闭 channel `/etc/xrdp/xrdp.ini`

> 不关连上过后会卡住

```ini
allow_channels=false
```

为普通用户添加 xorg 登录权限 `/etc/X11/Xwrapper.config`

```ini
allowed_users=anybody
```

为用户添加启动脚本 `~/.xinitrc`

```bash
#!/bin/sh
unset DBUS_SESSION_BUS_ADDRESS
unset XDG_RUNTIME_DIR
. $HOME/.profile
export $(dbus-launch)
# replace with your de
exec startplasma-x11
```

> 如果你不是使用的 plasma ，请替换为你自己的启动器

# 启动和使用

启动和启用服务

```bash
systemctl start xrdp.service
# enable it if needed
systemctl enable xrdp.service
```

然后使用 `mstsc.exe` 或者 `freerdp`  进行连接即可

# 其他

## GPU support

参考 [xorgxrdp-git aur][4] 的评论

```
xorgxrdp with GPU support:
Intel and AMD: https://aur.archlinux.org/packages/xorgxrdp-glamor/
NVIDIA: https://aur.archlinux.org/packages/xorgxrdp-nvidia-git/
```

## audio support

参考 [官方文档][5] 里的链接
也就是安装 [pulseaudio-module-xrdp][6] 插件


## 参考链接

- [xrdp][1]
- [xorgxrdp][2]
- [xrdp-git(aur)][3]
- [xorgxrdp-git(aur)][4]
- [xrdp access-to-remote-resources][5]
- [pulseaudio-module-xrdp][6]

[1]: https://github.com/neutrinolabs/xrdp
[2]: https://github.com/neutrinolabs/xorgxrdp
[3]: https://aur.archlinux.org/packages/xrdp-git/
[4]: https://aur.archlinux.org/packages/xorgxrdp-git/
[5]: https://github.com/neutrinolabs/xrdp#access-to-remote-resources
[6]: https://github.com/neutrinolabs/pulseaudio-module-xrdp/wiki/README


