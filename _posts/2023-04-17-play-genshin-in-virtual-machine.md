---
layout: post
title: 显卡虚拟化实现在虚拟机内玩原神
description: Win11 下显卡虚拟化实现在虚拟机内玩原神
tags:
- Other
categories: Other
---

# 简介

记得好久之前看到过 n 卡开放了 vGPU 功能，但是一直没时间去折腾，虽然前段时间在主力电脑上尝试折腾了一下，但是发现 Win10 的 Hyper-V 不支持嵌套虚拟化，没法完美搞定，就一直搁置到最近。然后因为有一台空闲的笔记本，就给它升到了 Win11 ，然后终于搞定了

~~话说 Win11 确实挺漂亮的，有点想升了怎么办，但是主力机还是不是特别想升级，能用就不升，万一挂了就蛋疼了~~

# 预先准备

在 Windows 功能里启用 Win11 的 Hyper-V ，这个是基操，具体方法自己搜一下

然后准备一个 Win10 LSTC 的镜像，安装到虚拟机中，然后记得 [开启嵌套虚拟化][1] 。注意，这个功能需要新的 Hyper-V 版本 **仅 Win11 可用** ， Win10 的 Insider 好像也可用，我没有符合条件的机器

```powershell
Set-VMProcessor -VMName <VMName> -ExposeVirtualizationExtensions $true
```

# 配置 vGPU

其实很简单，网上有不少教程了，直接抄个脚本，然后运行脚本就行了。记得把名字改成你的虚拟机的名字。参考 [Hyper-v 虚拟机 Game 尝试][2]

```powershell
$vm = "VMName"
Add-VMGpuPartitionAdapter -VMName $vm
Set-VMGpuPartitionAdapter -VMName $vm -MinPartitionVRAM 80000000 -MaxPartitionVRAM 100000000 -OptimalPartitionVRAM 100000000 -MinPartitionEncode 80000000 -MaxPartitionEncode 100000000 -OptimalPartitionEncode 100000000 -MinPartitionDecode 80000000 -MaxPartitionDecode 100000000 -OptimalPartitionDecode 100000000 -MinPartitionCompute 80000000 -MaxPartitionCompute 100000000 -OptimalPartitionCompute 100000000
Set-VM -GuestControlledCacheTypes $true -VMName $vm
Set-VM -LowMemoryMappedIoSpace 1GB -VMName $vm
Set-VM -HighMemoryMappedIoSpace 8GB -VMName $vm
```

然后还需要将 **本机的显卡驱动** 复制到 **虚拟机中** ，它们的 **位置并不一样** ：

- 主机: `C:\Windows\System32\DriverStore\FileRepository`
- 虚拟机: `:\Windows\System32\HostDriverStore\FileRepository`

要复制的驱动为 `nv` 开头，一般是第一个，你可以点进去看看，有一堆文件的就是，其他的驱动一般都只有几个文件

> 我这边 Win10 是叫 `nv_dispi` ， Win11 是叫 `nvamsi` ，名字是不一样的

到此为止，如果你是单显卡的机器，这时候其实就能正常使用了，但是如果你恰好跟我一样是双显卡/带核显的机器，需要特殊处理一下

# 适配双显卡

简单来说就是只虚拟化需要的显卡，参考 [双显卡笔记本 HyperV GPU虚拟化 踩坑记录][3] 。比如我这里就是要虚拟化独立显卡，先用以下命令查看可用的显卡，并记录下 `Name` 列，是以 `\\?\PCI#VEN_` 开头

```powershell
Get-VMPartitionableGpu
```

然后稍微修改一下脚本，只加载需要的显卡：

```powershell
$vm = "VMName"
$gpu_path = "\\?\PCI#VEN_XXX"

Add-VMGpuPartitionAdapter -VMName $vm -InstancePath $gpu_path
Set-VMGpuPartitionAdapter -VMName $vm -MinPartitionVRAM 80000000 -MaxPartitionVRAM 100000000 -OptimalPartitionVRAM 100000000 -MinPartitionEncode 80000000 -MaxPartitionEncode 100000000 -OptimalPartitionEncode 100000000 -MinPartitionDecode 80000000 -MaxPartitionDecode 100000000 -OptimalPartitionDecode 100000000 -MinPartitionCompute 80000000 -MaxPartitionCompute 100000000 -OptimalPartitionCompute 100000000
Set-VM -GuestControlledCacheTypes $true -VMName $vm
Set-VM -LowMemoryMappedIoSpace 1GB -VMName $vm
Set-VM -HighMemoryMappedIoSpace 8GB -VMName $vm
```

添加之前可以先查一下，如果你之前已经加过显卡了，记得先删掉，如果有多张，需要调用删除多次

```powershell
Get-VMGpuPartitionAdapter
Remove-VMGpuPartitionAdapter -VMName $vm
```

> 第一篇文章提到需要复制宿主机中的 `C:\Windows\System32\nvapi64.dll` 到虚拟机中的 `C:\Windows\System32` 下，我验证了一下，这个文件并不影响正常使用

# 过原神的虚拟机检测

像原神或者一些其他游戏和软件，附带了 VM 检测的功能，如果只是透传显卡，没有办法正常运行的，还需要过这个虚拟机检测。这个其实也很简单，还记得我们最开始开的嵌套虚拟化吗，开嵌套虚拟化的作用就是让我们可以在虚拟机内也开启 Hyper-V ，这样的话，就能过原神的虚拟机检测。你没听错，就这么简单

至于原理也很简单：开了 Hyper-V 的机器的行为会类似于虚拟机，如果把开了 Hyper-V 的机器全当成虚拟机，肯定不现实，所以许多虚拟机检测的软件在遇到开启 Hyper-V 的机器时直接开摆，不做检测了

引用来自 [操作系统能否知道自己处于虚拟机中?][4] 一条回答的一部分：

> 于是有很多网游反作弊系统，只要检测到开启了Hyper-V，就放弃检测虚拟机环境。于是在Linux界就有了一种神奇的操作，先用KVM开Windows虚拟机，然后在Windows中开启Hyper-V，这样就能愉快地玩各种3A巨作了。

# 串流方案

如果你家网络好，建议直接 [Steam Link][5] 。然而在我这边这玩意儿有 Bug ，只能显示 1/3 屏幕，剩下的 2/3 被切掉拼上去了，反正看起来很怪，没法正常使用。而且我用的时候还会动不动断连，应该是我这边到 Steam 的网络不够稳定

既然商业方案不太行，那我们就切换到开源拖拉机，搜了下， [MoonLight][6] 好像还行，安装上客户端就行了，服务端可以用 NVIDIA Experience 的串流功能。但是，这种虚拟化的情况下，其实是装不上 NE 的。我不确定有没有方法可以先装好 NE ，我当时懒得折腾了，直接换方案

客户端还是可以用 MoonLight ，服务端换一个，搜了一下又搜到了 [Sunshine][7] 。这名字，好嘛，一个日光，一个月光，月光是接收到日光反射出来的，很合逻辑！

部署好了过后，一顿配置，跑起来了，效果， hmm ，怎么说呢，好像不太行

# 效果和结论

从结果来说，确实是可以跑起来的，也是能玩的，但是串流的画质确实有损失，不确定是不是因为我参数没有拉满。而且，串流其实是有一定卡顿的，我看了下虚拟机的画面其实也有卡顿，说明可能是显卡或者 CPU 什么的瓶颈，毕竟我那就一个 3060 移动版，还只有 6G 显存，估计是有点带不动，如果用更好的显卡然后再把串流的参数拉满，说不定能有不错的效果

结论：如果有这方面的需求，这个方案还是比较可取的的，玩 3A 可能没有那么令人满意，但是用来玩点 Galgame 或者其他的比较轻量的游戏，肯定是绰绰有余，所以还是比较值得折腾的，毕竟折腾好了就可以躺在床上用手机玩电脑的游戏了

# 补充说明: Linux 实现思路

整套方案其实有两个要点：

- vGPU
- 过虚拟机检测

因此，如果想在 Linux 上复现该方案理论上也是可行的

首先是 vGPU ，可以参考 NVDIA 的官方文档 [Virtual GPU Software User Guide][8] ，主要方向有两个，一个是透传，另一个就是 vGPU 了，但是 vGPU 配置估计不太好搞，按需选择

另一个过虚拟机检测，因为这里是启用 Hyper-V 的嵌套虚拟化，然后在虚拟机里启用 Hyper-V 来过虚拟机检测，按理说只要在宿主机上的虚拟机软件上启用了嵌套虚拟化，然后再在虚拟机内部开启 Hyper-V 也能够过虚拟机检测。思路是这样的，但是没有验证过，如果有兴趣，可以验证一下


# 参考链接

- [开启嵌套虚拟化][1]
- [Hyper-v 虚拟机 Game 尝试][2]
- [双显卡笔记本 HyperV GPU虚拟化 踩坑记录][3]
- [操作系统能否知道自己处于虚拟机中?][4]
- [Steam Link][5]
- [MoonLight][6]
- [Sunshine][7]
- [Virtual GPU Software User Guide][8]


[1]: https://learn.microsoft.com/zh-cn/virtualization/hyper-v-on-windows/user-guide/nested-virtualization
[2]: https://jasper1024.com/jasper/ioubn7891wc/
[3]: https://blog.shigure.fun/laptop_gpu_hyperv/
[4]: https://www.zhihu.com/question/359121561
[5]: https://store.steampowered.com/app/353380/Steam_Link/
[6]: https://github.com/moonlight-stream/
[7]: https://github.com/LizardByte/Sunshine
[8]: https://docs.nvidia.com/grid/6.0/grid-vgpu-user-guide/index.html#installing-configuring-grid-vgpuinstalling-grid-vgpu-display-drivers
