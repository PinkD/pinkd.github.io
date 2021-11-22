---
layout: post
title: 如何在Android程序FC前抢救一番
description: 在Android程序FC前应该如何进行错误收集以及给用户一个友好的提示，甚至忽略这个错误
tags:
- Coding
- Java
- Android
categories: Android
---

# 如何在Android程序FC前抢救一番

**注：此文章中线程不特殊声明，均为Android主线程。**

## 什么是FC，程序为什么会FC

`Force Close` ，就是程序发生了未被捕捉的异常，也称 `FATAL EXCEPTION` ，导致程序崩溃，并弹出 ~~令人愉快的~~ `Unfortunately` ~~（雾）~~ 。

## 程序中出现未被捕捉的异常后发生了什么

- 抛出异常
- 交给设置的 `UncaughtExceptionHandler` 处理
- 线程结束
- 程序退出

## 如何处理

### 保存或上传崩溃日志

在 `UncaughtExceptionHandler` 中写好就可以了。

### 在崩溃后重启APP

因为在主线程崩溃后，Android的消息机制已经炸了，默认的 `UncaughtExceptionHandler` 就是并关闭程序弹出 ~~令人愉快的~~ `Unfortunately`。可以选择在处理完成后重新启动App。例：

```java
Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //LogUtils.save(e);
                //具体方法网上一大堆，还可以打印机型、系统版本等信息
                Log.e(TAG, "uncaughtException on " + t.toString() + ": ", e);
                //重启App
                context.startActivity(new Intent(context, BootActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                System.exit(-1);
            }
        });
```

### 防止崩溃

这个问题是我没想到的，因为崩溃都发生了，一般的想法就是安排后事，而不是继续 `续` 。

在此感谢 [AndroidDevCN](https://t.me/AndroidDevCn) 中的 [Aesean](https://t.me/Aesean)


#### 原理解答

在主线程崩溃后，Android的消息机制已经炸了，但是我们有没有办法 `续` 一发呢？

了解Android消息机制的都知道（不了解的网上搜一搜吧，[这儿](https://android-notes.github.io/2016/12/03/5%E5%88%86%E9%92%9F%E5%AE%8C%E5%85%A8%E7%90%86%E8%A7%A3android-handler/) 也有一篇blog），Android的主线程就执行了几行代码，大概就是：

```java
public static void main(String[] args) {
    Looper.prepare();
    initMessageQueue();
    Looper.loop();
    throw new RuntimeException("Main thread loop unexpectedly exited");
    //加一句测试代码
    System.out.println("喵喵喵？");
}
```

而 `UncaughtExceptionHandler` 就是在当前线程异常发生时跳转到其中的代码继续执行，然后就退出线程。所以， `System.out.println("喵喵喵？");` 并不会执行。 **注意，在执行完 `UncaughtExceptionHandler` 之前线程并没有死。第一段代码中的 `t.toString()` 的 `t` 就是主线程。** 

那么，问题来了，该如何防止崩溃呢？

#### 原理解答

既然线程发生异常，消息队列被破坏，那我们让消息队列继续运行不就可以实现 `续` 的目的了？所以，可以这样写：

```java
Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("TAG", "uncaughtException: ", e);
                Looper.loop();
            }
        });
```

但是问题又来了，这样只能防止第一次崩溃，第二次依旧会导致程序出现问题。

这就是 [Cockroach](https://github.com/android-notes/Cockroach) 这个库所做的。思路很巧妙。

主要源码就是 [Cockroach.java](https://github.com/android-notes/Cockroach/blob/master/app/src/main/java/com/wanjian/cockroach/Cockroach.java) 。

大概分析一下：

```java
Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("APP", "uncaughtException: ", e);
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Exception e1) {
                        Log.e("APP", "uncaughtException: ", e1);
                    }
                }
            }
        });
```

就是向 `UncaughtExceptionHandler` 添加一个死循环，这个死循环又调用 `Looper.looper()` 来获取 `MessageQueue` 中的 `Message` 进行处理，发生异常就处理，然后继续执行 `Looper.looper()` 。这样消息循环就不会被破坏，线程也不会退出，就可以做到 `无限续` 的作用。

在 [Cockroach](https://github.com/android-notes/Cockroach) 中还做了一些优化处理，详见源码。

然而有些异常依旧会导致 `FC` ，比如JNI的异常。所以这个方法也不是万能的，只是一种抢救和优化的方案。

关于 [Cockroach](https://github.com/android-notes/Cockroach) ，本身就带了一份 [原理说明](https://github.com/android-notes/Cockroach/blob/master/%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90.md)

~~对了，今天似乎是虵的生日~~
