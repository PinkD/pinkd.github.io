---
layout: post
title: RecyclerView中的图片乱序问题
description: RecyclerView中的网络请求图片乱序问题
tags:
- Android
- Java
categories: Android
---

### RecyclerView中的图片乱序问题

#### 0x01 原因分析

分析这个问题之前，我们应该先了解一下RecyclerView的工作原理。

[官方文档](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html)中是这样写的

- _Recycle (view)_: A view previously used to display data for a specific adapter position may be placed in a cache for later reuse to display the same type of data again later. This can drastically improve performance by skipping initial layout inflation or construction.
- _Scrap (view)_: A child view that has entered into a temporarily detached state during layout. Scrap views may be reused without becoming fully detached from the parent RecyclerView, either unmodified if no rebinding is required or modified by the adapter if the view was considered dirty.

用图片来说明一下大概原理：

![](https://www.processon.com/diagram_export?type=image&title=RecyclerView&chartId=58c750c6e4b0897e6c283b06)

原理解释：RecyclerView会创建比屏幕上可见数量多几个的ItemView用来显示Adapter中的数据。在用户上下滑动时，RecyclerView会自动回收处理那些不可见的ItemView，以方便重复使用，减小各种资源消耗。

那么，为什么会出现所谓的图片加载乱序问题呢？

网络加载图片的基本流程是这样的：

- 新建一个（子）线程，在子线程中进行网络请求
- 拿到`InputStream`，用`BitmapFactory.decodeStream`解析为`Bitmap`
- 用`ImageView.post`方法(或者其它方法)来更新UI

问题出在哪儿呢？

举一个最容易理解的例子：

假设ItemView1中的ImageView先进行网络请求，然后用户快速滑动，ItemView1被回收了，但是图片加载方法中还在继续加载图片。然后用户继续滑动，ItemView1重用了，再一次进行网络请求。如果此时第一次请求时网络卡了（或者其他的什么原因），造成第二次先请求完成，就会出现第二次请求先更改ImageView内容为Image2，然后第一次请求又将其更改为Image1的情况，这样就造成了图片乱序。

#### 0x02 解决方法

`ImageView`中有一个`Tag`属性。我们可以从这个属性入手，找到解决方法。

具体思路：

- 在请求开始前，先用`ImageView.setTag`方法将tag设置为图片来源（一般为url）
- 进行请求
- 在`ImageView.post`前附加判断`url.equals(imageView.getTag())`，如果不等，就说明已经有新的图片正在加载了，于是跳过更新图片操作

