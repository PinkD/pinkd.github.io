---
layout: post
title: 记录一次ISCC逆向
description: 从我的简书上面搬运过来的
tags:
- Reverse
- ctf
categories: ctf
---

题目：我方截获了敌方的一款加密程序和一段密文(密文为：F2F5D39F18D762B7)，尝试通过对此程序进行分析实现对密文的解密，解密后明文的32位小写MD5值作为flag提交。

1.先运行一发，运行截图：

![图片1.png](http://upload-images.jianshu.io/upload_images/2093711-453d6fa95ce2d155.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2.扔进OD，搜索字符串，发现，咦，内有乾坤。

![图片2.png](http://upload-images.jianshu.io/upload_images/2093711-f81255214621f632.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3.经过成吨的分析（本人鶸一只），发现题目给出的密文应该是经过打开程序时的默认加密方式加密的，那么，现在就要找到默认加密方式。

4.直接肛，强行跳到加密的地方

![图片3.png](http://upload-images.jianshu.io/upload_images/2093711-6b79efb86136df5a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

至于怎么跳，随便找个地方跳过去，我选择的改输入选项，把退出系统改成跳到自定义加密。
//反正我是来逆向的，程序boom了也没事。（雾

![图片4.png](http://upload-images.jianshu.io/upload_images/2093711-5049d2eb612c3bbc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在这后面下断点，看它怎么跳的。

![图片5.png](http://upload-images.jianshu.io/upload_images/2093711-48eddc1bf2715e8e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果输入为2，push返回值0，call exit。
把参数干掉，call自定义加密解密：

![图片6.png](http://upload-images.jianshu.io/upload_images/2093711-ed411c499254558d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![图片7.png](http://upload-images.jianshu.io/upload_images/2093711-a517cb9012faf0ba.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![图片8.png](http://upload-images.jianshu.io/upload_images/2093711-83a79787b9510af1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

跳过去了，然后下断点找到自定义加密是的函数。
Case 1 ，call了这个函数，跟过去。

![图片9.png](http://upload-images.jianshu.io/upload_images/2093711-b51ef958d84dc990.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

加密函数就在这中间，在最后一次输入（输入密钥时）后面下断点仔细找找，找到了就跟进去。

![图片10.png](http://upload-images.jianshu.io/upload_images/2093711-28ff883fae492b88.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这密密麻麻的运算，看来就是它了。

![图片11.png](http://upload-images.jianshu.io/upload_images/2093711-e975c01cfd2e9d5b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

找到return的地方，往前下断点。

![图片12.png](http://upload-images.jianshu.io/upload_images/2093711-02ba2b17f139e8ec.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

就是这儿，下断点，然后重新运行选择默认加密，随便加密一个就能看到密码是啥了。

![图片13.png](http://upload-images.jianshu.io/upload_images/2093711-f6ebe677ae7b2d2a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这就是密码了。

![图片14.png](http://upload-images.jianshu.io/upload_images/2093711-44cb1096bad4e52c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

再选择自定义解密。

![图片15.png](http://upload-images.jianshu.io/upload_images/2093711-549d08ecf5f73bfb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

直接在这儿看，或者看程序运行结果都可以。

![图片16.png](http://upload-images.jianshu.io/upload_images/2093711-3cc530f320558c74.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

然后拿去md5大法。就可以提交flag了。
