---
layout: post
title: Java中的异常处理机制
description: 其实是总结+半个教案
tags:
- Coding
- Java
- Android
categories: Java
---

## Java中的异常处理机制

### 0x00异常

异常分为Error和Exception，我们通常说的处理异常，其实是处理Exception。而Error已经不是异常了，而是错误。一般是因为代码错误导致jvm崩溃。

用图说话
![异常大类](/images/Throwable.png)

#### 0x01 Exception类和它的子类

从上面的图可以看到，Exception这个类下面有很多子类，他们都继承自Exception，我们也可以自己写一个异常类。例如：

``` java
public class MyException extends Exception {
}
```

然后就可以使用了，例如：

``` java
public static void throwException() throws MyException {
    throw new MyException();
}
```

原理会在下文讲到

这个继承于Exceptiion的类啥都没写，所以类里面的东西和Exception里面的是一样的。让我们来看看Exception里面有些什么。好吧。。。全是super.method，我们还是看它的父类Throwable吧。

``` java
private transient Object backtrace;
private String detailMessage;
private Throwable cause = this;
```

主要就是这三个。第一个，是在抛出Exception后的栈跟踪。第二个，是异常详情。第三个就是异常原因，就是异常本身。

异常的构造方法：

``` java
public Exception();
public Exception(String message);
public Exception(String message, Throwable cause);
public Exception(Throwable cause);
```

我就不具体写了。跟成员变量对应。


jvm通过栈来将异常一层一层往上抛(与一层一层的函数调用相反)，直到他被处理(`catch`)，否则，程序停止工作，jvm向用户报告错误。异常的抛出（栈）路径可以通过`Exception.printStackTrace()```方法查看    
而异常被处理后，程序会回到抛出异常的地方继续执行。


#### 0x02 如何抛出异常和进行异常处理

- 抛出异常

    很简单，直接使用`throw`关键字，但是如果抛出的异常未在当前方法处理，需要在方法后面声明。需要在它所在的方法处声明`throws MyException`。意思是此方法将抛出错误。

- 处理异常

    使用`try catch`语句。用法：    

``` java    
public static void main(String[] args) {
    try {
        throwException();
    } catch (MyException e) {
        e.printStackTrace();
        //Do sth...
         //throw e;
    }
}
```

    `try`可能会抛出异常的语句块，`catch (MyException e)`捕获异常MyException声明为引用e.一般来说都会跟上一个`e.printStackTrace()`，打印错误详情，方便debug。    
    当然你也可以再次将异常抛出，交给上层继续处理。    
    有人会发现，`e.printStackTrace()`输出的怎么是红的，因为你看看它的源码：    

``` java    
public void printStackTrace() {
    printStackTrace(System.err);
}
```

    它默认用的输出流是`System.err`，而不是sout用的`System.out`哦。

- 多重异常处理

    用于将不同类型的异常分开处理。    

``` java    
try {
    throwException();
    throw new NullPointerException();
} catch (MyException e) {
    e.printStackTrace();
} catch (NullPointerException e) {
    e.printStackTrace();
}
```

> 对了，如果程序因异常而退出，它的返回值就不为0，在IDEA中可以明显看到：`Process finished with exit code xxx`

#### 0x03 finally关键字

你可能会遇到无论是否出现异常都需要进行某种操作的情况，这时候，你就需要用到`finally`了。比如：

``` java
try {
    throwException();
    throw new NullPointerException();
} catch (MyException e) {
    e.printStackTrace();
} catch (NullPointerException e) {
    e.printStackTrace();
}finally {
    System.out.println("An error occurred!");
}
```

这种方法经常用来在异常发生后关闭流。    
<del>其实可以把finally后面的语句看成擦屁股的</del>

#### 0x04 异常丢失

这是一种特殊情况，虽然不经常出现，但是还是提一下比较好。

最简单的触发方法：

``` java
public static void main(String[] args) {
    try {
        throwException();
        throw new NullPointerException();
    } catch (MyException e) {
        e.printStackTrace();
    } catch (NullPointerException e) {
        e.printStackTrace();
    }finally {
        System.out.println("An error occurred!");
        return;
    }
}
```

也就是在finally里面执行return。    
IDEA里面会提示：![MissingException](/images/MissingException.png)    
其实原理就是在finally里面的语句会在异常处理完成之前执行。如果在finnally里面return，就会发生异常丢失。

#### 0x05 异常实例

``` java
private int take(int index, int last) {//取豆子
    int count;
    try {
        count = mPrisoners.get(index).take(index, last);
    } catch (Exception e) {//这些神经病抓出来单独死
        count = -1;
    }
    if (count > last || count < 0){//如果返回无效个数，和上面的神经病一样死
        count = -1;
    }
    System.out.println(mPrisoners.get(index).getName() + "取了" + count + "个");
    //保存每个人取的豆子数
    mTempHold.replace(mPrisoners.get(index), count);
    return count;
}
```

取豆子游戏，[源码在这儿](https://github.com/Jude95/lifeline)，Manager类。

这是异常处理的用法之一，你永远不知道熊孩子们会搞出什么异常炸掉你的程序，所以熊孩子必须抓出来单独死。

异常在Java中使用频率极高，多次使用对异常自然就熟悉了。
