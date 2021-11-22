---
layout: post
title: 解包加密的 UE4 游戏
description: 解包加密的 UE4 游戏
tags:
- Others
categories: Others
---

# 简介

UE4 的游戏一般会将所有资源打包成一个 pak 文件，可选加密。如果能直接从游戏里解包资源，那就能拿到很多不错的素材

> 注：自己玩玩就好，在取得原作者同意前，请勿将解包的资源公开使用和发布

# 步骤

## 获取用于加密游戏资源的 AES Key

由于游戏在运行时能够解密资源，所以对于单机游戏，这个 key 一定是在游戏的可执行文件中
使用 [UEAESKeyFinder][1] 即可 dump 出位于游戏引擎二进制文件中的 AES Key

直接运行该程序，会提示你用哪种方案获取：

```text
Please select from where you want to get the AES Key
0: Memory
1: File
2: Dump File
3. LibUE4.so File
4. APK File
Use:
```

直接选择 1 ，然后输入 .exe 文件的目录，或者直接将 exe 拖入然后回车即可

然后应该就能找到 Key:

```text
Engine Version: ++UE4+

Found 1 AES Key in 399ms
0x114514 (EUUU) at 140695136861675
```

前面的 `0x114514` 就是 hex ，括号内的就是 base64 后的字符串，只需要 base64 后的内容，复制一下

## 解密和解包资源

UE4 官方提供了解包的工具，如果你装了 Epic 并下载了 UE4 的源码，就能直接找到 `UnrealPak.exe` ，如果没装，可以直接去 github 下载 [UnrealPakTool][2]

然后创建一个 `Crypto.json` 文件，将之前复制的 key 填进去：

```json
{
    "EncryptionKey": {
        "$type": "2",
        "Name": "null",
        "Guid": "null",
        "Key": "EUUU"
    }
}
```

然后使用 UnrealPack 工具来解密：

```cmd
UnrealPak.exe xxx.pak -Extract OutputDir -cryptokeys=Crypto.json
```

稍等一会，然后你就能在 `OutputDir` 这个目录下找到解包后的内容

> 解包出来的项目文件应该可以直接用 UE4 打开，我没配完整 UE4 的环境，没试过到底能不能用，有兴趣可以试试

## 浏览和导出资源

可以使用 [UEViewer][3] 来浏览和导出项目中的资源。关联资源会自动导出，比如导出模型会同时导出材质

然后就可以把模型扔到 blender 或者其他 3D 软件里玩耍了

# 参考链接

- [UEAESKeyFinder][1]
- [UnrealPakTool][2]
- [UEViewer][3]

[1]: https://github.com/EZFNDEV/UEAESKeyFinder
[2]: https://github.com/allcoolthingsatoneplace/UnrealPakTool
[3]: https://github.com/gildor2/UEViewer
