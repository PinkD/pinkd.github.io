##引言
我们为什么需要规范命名？首先，容我举个栗子：
>有这样一个求最大值的函数：
```c
//C
int max(int a, int b);
int zuidazhi(int a, int b);
```
读第一行代码的过程：单词max->最大值
读第二行代码的过程：拼音zuidazhi->从拼音匹配对应汉字->最大值
可能你现在还觉得第二种命名也不是那么难理解。那么再看看下面的：
```c
//C
int checkUsername(char *username);
int jianchayonghuming(char *yonghuming);
```
如果你还觉得第二种命名简单，不错，你的小学语文一定是满分，可是你写的程序其他人不一定能看懂，或者会花很多时间去看懂。然后，国外的人一定都看不懂（黑人问号）。

##为什么要谈命名的艺术
- 首先，每个文件，函数等都需要一个标签，为了我们能更快找到它们，命名就显得比较重要了。
- 我们写的代码不光是给自己看的，你需要让你的老师，队友，甚至是上司都能看懂你的代码。一个人只能耍耍小聪明，写点小程序，而那些大型程序几乎没人能独自完成。团队合作时，命名就是看你是否坑队友的标准之一。
- 其实命名规则也是一种习惯，我们都遵循这个的规则，世界才能更加和平（x）。

##通用的命名规则
- 不要用拼音！！！
- 不要作死去用关键字/保留字。
- 常量大写+下划线命名法。

例子：
```java 
//Java
public class class {//这行直接炸
    public static final int EXIT_SUCCESS = 0;//下面应该会有一个exit(EXIT_SUCCESS);吧，手动滑稽
    private int zonghe;//可以，这很拼音
}
```

##Java命名规则
- 据说Java支持中文变量，但是中文不要来。(据说有个叫e4a的就是这么干的)
- 类名/文件名用[帕斯卡命名法](http://baike.baidu.com/view/1276459.htm)。<b>注：文件名必须和最外层public类类名相同</b>
- 方法名/成员变量名/形参等用[驼峰命名法](http://baike.baidu.com/view/1165629.htm)。

例子：
文件名为TestClass.java
```java 
//Java
public class TestClass {//类名必须和文件名相同，帕斯卡命名法
    private int sum;
    public int getSum(){//驼峰
        return this.sum;
    }
}
```

##更高级的姿势
命名要有意义
- for循环里的i,j,k什么的就不要说了
- 类名：要能代表这个类的功能和类型（ChatClient）
- 方法名：同样要能表示方法的功能，还要简单易懂（getServerIP）
- 属性1：一般是类名变驼峰命名，也可以将类名适当缩写（chatClient或client）
- 属性2：java源码中经常以m开头来命名成员变量，虽然我不喜欢这么写（mChatClient或mClient）
- 尽量不要使用重复命名，这样容易导致各种奇怪的问题。


##其它命名规则（有点杂，就不列举多了）
- 下划线命名法。（脚本语言使用较多）
- 特殊前后缀标记（php的$，python的__）

```python
#python
class Test:
    def __init__(self):
        self.name = None
def is_empty(content)://下划线
    if content == "":
        return False
    else:
        return True
```

##最后，再次强调：不要用拼音！！！
最后的最后，贴上[朱大的地址](http://www.jianshu.com/users/d73c34b951ef)，快去膜一波。