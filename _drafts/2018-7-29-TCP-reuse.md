---
layout: post
title: 解决进程结束后端口仍然被占用的问题
description: 解决进程结束后端口仍然被占用的问题
tags:
- Linux
- Network
- Others
categories: Others
---

## socket分配

一个进程向操作系统申请一个 `scoket` ，但是当进程退出后，还未关闭的连接不会立即关闭，会留给操作系统处理。操作系统会尝试关闭这个连接。但是如果关闭时出现问题，这个连接就会一直处于 `TIME_WAIT` 的状态，而这是相应的端口还处于占用状态，如果这个时候再重新启动这个程序，就会出现地址被占用的情况


## 例子

测试代码：


```python
import socket

s = socket.socket()
s.bind(('0.0.0.0', 12345))
s.listen()
(client, addr) = s.accept()
print(client)
print(addr)
```

使用 `nc` 进行连接：

```bash
nc 127.0.0.1 12345
```

服务端会打印 `client` 和 `addr` ，然后正常退出，但是此时使用 `netstat -anop | grep 12345` 查看，发现对应连接并没有被立即释放

```
tcp        0      0 127.0.0.1:12345         127.0.0.1:59408         TIME_WAIT   -                    timewait (28.18/0/0)
```

此时再次启动服务端，发现报错了：

```
Traceback (most recent call last):
  File "server.py", line 5, in <module>
    s.bind(('0.0.0.0', 12345))
OSError: [Errno 98] Address already in use
```

## 解决方案

使用 `setsockopt` ：

```
import socket

s = socket.socket()
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind(('0.0.0.0', 12345))
s.listen()
(client, addr) = s.accept()
print(client)
print(addr)
```

此时就不会出现地址被占用的提示了

在 `c` 中也有一样的方法，只是方法声明不同， `c` 版的用法为

```c
struct sockaddr_in addr;

addr.sin_family = AF_INET;
addr.sin_port = htons(12345);
addr.sin_addr.s_addr = htonl(INADDR_ANY);

int s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
int reuse = 1;
setsockopt(s, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));
bind(s, (struct sockaddr *) &addr, sizeof(addr))
listen(s, )

struct sockaddr_in in_addr;
int len = sizeof(in_addr);
int client = accept(socket, (struct sockaddr *) in_addr, &len);
//handle client
//...
```

## 其他

发现除了 `SO_REUSEADDR` 之外还有一个 `SO_REUSEPORT` 的选项，查询后得知是 `BSD` 独有的， `Linux` 并不能用
