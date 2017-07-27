# Linux 基础知识

## 从文件说起

在Linux当中，**万物皆文件**。

- 文件（包括可执行文、媒体文件、一般文件等）
- 文件夹
- 设备

### 文件的基本属性

#### 文件属性分类

文件属性分四类，如下：


|文件类型|所有者权限|所属组权限|其他人权限|
|------|---------|--------|--------|
|d     |rwx      |rwx     |rwx     |

当然，除了这些属性之外还有一些不常用的属性，详见 #TODO

##### 文件类型

有这些：

- \-
- d
- l
- b
- c

分别对应：

- file(文件)
- directory(目录)
- link(链接)
- block device(块设备，如磁盘，详见 操作系统课程)
- character device(字符设备，如鼠标，同上)

##### 三个rwx

所有者(owner) 所属组(group)和其他人(others)权限都是分三位，分别代表 `rwx` ，如果三位都为1，就是 `777` ，所以一言不和就 `chmod 777 .` 是非常危险的。    
最常用的文件属性是 `644` (所有者读写，其他只读)。    
有的对安全性要求比较高的文件(比如私钥)甚至要求 `600`。

##### 更改文件属性

见常用命令。

### 文件的基本操作

见常用命令。

## 常用命令

注： `[]` 为可选参数， `<>` 为必选参数。

### ls

这应该是Linux中使用次数最多的命令，没有之一。

常见参数：

- 不使用参数，列出当前目录下非隐藏文件
- `-a, --all` 列出所有文件
- `-l` 详情
- `-h, --human-readable` 以合适的大小单位显示，而不是默认的字节

注：bash有个默认加载，在 `~/.bashrc` ，如果是Ubuntu，会有一个 `alias ll="ls -alF"` ，我一般会给它加个 `-h` 。

啥，你问我查看文件夹怎么办？

### man

- **Read the fucking manual plz.**
- **有问题问男人**
- 其实它叫手册，比 `<command> --help` 更加详细。
- 所以本文中未列出的参数自己查手册或者help吧

### cat

将文件显示到标准输出流。

用法(Usage)： `cat <file>`  

### mv 

移动文件。

用法(Usage)： `mv [options] <source file> <destination file>`  

~~所以 `rename` 存在的意义是什么？~~

### cp

复制文件。

用法(Usage)： `cp [options] <source file> <destination file>`

常见参数：

- `-r` 递归复制，复制文件夹时必须加此参数

### ln

创建链接。这比 ~~辣鸡~~ Widoge里的快捷方式强大多了。

用法(Usage)： `ln [options] <target> <link name>`

常见参数：

- `-f, --force` 覆盖前不询问
- `-s, --symbolic` 创建软链接

所以一般使用 `ln -sf <target> <link name>`

软链接和硬链接的区别：软链接相当于Widoge里的快捷方式，储存了目标的位置等信息，不过比快捷方式强大。硬链接是创建了另一个索引节点。只有当所有的索引节点都被删除时，文件才会被标记为失效，可能被写入操作覆盖。

### grep

一个正则匹配的强大命令。

用法：自己找男人。

常与管道符 `|` 连用。如： `ll | grep test` ，筛选出当前目录下含有test的文件。

有个蛋疼的问题，grep会显示

### ps

查看所有进程。

一般 `ps -aux`

和grep连用。 `ps -aux | grep init` 。会发现grep程序也在结果里面，可以用 `grep -v grep` 去掉。

### tar

打包。

仅打包： `tar -cvf <filename>.tar <files>`    
打包且压缩： `tar -zcvf <filename>.tar.gz <files>`   
解压：将上述的c(compress)换成x(exact)。
其中：v是显示详情，f是覆盖，具体请 `man tar`

### tee

将标准输入复制到每个指定文件，并显示到标准输出。

看个例子： `echo test | tee test1 test2 test3`

以上代码会显示 `test` ，并在

### chmod

修改文件保护位，在文件部分提到了，分三个部分。用三个8进制数字代替。


用法(Usage)： `chmod [OPTION] mode <file>`

一般 `chmod 644 <file>` 。目录一般 `755` ， `r` 只能保证可以列目录， `x` 才能保证可以 `cd` 进去(From haruue)。可以加 `-r` 递归。

`~/.ssh/` 下的key必须设置成 `600` ，否则会提示无法使用。

### chown

修改文件所有者。

用法(Usage)： `chown [OPTION] <OWNER>[:<GROUP>] <file>`

一般 `chown pinkd:pinkd <file>` 。可以加 `-r` 递归。

### mount

挂载设备。使用 `umount` 卸载。

`mount [options] <device> <dir>`

挂载一般的磁盘： `mount /dev/sdb1 /mnt`
挂载iso文件： `mount -t iso9660 /mnt/archlinux.iso /cdrom`

### curl

神器，需要安装，最常用的就是查看http请求的详情。比如： `curl -v dl.google.com` ，结果就不贴了，太长。详见 `man curl` 。

### nc 

神器，用来判断某个端口是否打开，还可以传输文件 ~~，甚至可以反弹shell。~~

`nc -vv www.baidu.com 80`

### rm

删除文件， 目录需要 `-r` ， 有些文件需要 `-f` 。 ~~所以就有了 `rm -rf /` 。~~

### dd

`Copy a file, converting and formatting according to the operands.`

核武器，可以用来备份、恢复，可以用来复制文件 ~~，还可以用来全盘填0。~~   
`dd if=/dev/zero of=/dev/sda` ~~全盘填0~~    
`dd if=/dev/sda2 of=/dev/sdb2 bs=4096 conv=noerror` 克隆分区    
`dd if=/dev/fd0 of=MBRboot.img bs=512 count=2` 备份MBR

其他用法和参数自行 `man` 

### mkfs

格式化。一般 `mkfs.ext4 /dev/sdx#` 。

### alias

设置alias。比如： `alias ll="ls -lah"`   
你甚至可以折腾git： `alias gta="git add"

### 其他命令

- `whoami` 如果你在别人的电脑上执行这个输出是 `root` 就很棒了
- `id` 查看用户和组的id。
- `which` 查找可执行文件完整路径
- `file` 查看文件类型，很强大
- `ping`
- `traceroute`
- `nslookup` 查域名
- `awk` 比grep更强大，Haruue会用，然而我并不会
- `sort`
- `echo`
- `top` 查看系统状态，相当于 `taskmgr` 。
- `htop` 上一个的升级版。吃我一记安利（其实我是被49安利的）
- `df` 查看文件系统状态。 `-h, --human-readable`
- `du` 统计当前目录大小。 常用 `-h, --human-readable` , `-s, --summarize`
- `lsblk` 查看块设备，加 `-f` 可看到 `UUID` 
- `useradd`
- `groupadd` 
- `pwd` 显示目前的目录
- `touch` 创建一个文件，默认 `644`
- `swapon/swapoff` 设置交换分区/文件
- `date` 看看时间
- `kill <pid>` 杀死某个进程
- `pkill <process name>` 杀死某个名字的进程


## 包管理

### apt

debian系下的包管理器。常见用法：

- `apt-get update` 更新仓库数据
- `apt-cache search <regex>` 搜索目标包名
- `apt-get upgrade` 更新系统(日常滚)
- `apt-get install <package name>` 安装指定包

### pacman

ArchLinux下的包管理器，不仅仅是包管理器。常见用法：

- `pacman -Sy` 更新仓库数据
- `pacman -Syu` 日常滚
- `pacman -Ss <package name>` 查找指定包
- `pacman -S <package name>` 安装指定包

### yum

不用CentOS，自己查。[这儿](http://www.runoob.com/linux/linux-yum.html)有个参考。

## vim

### 常用操作

- `i` insert 在当前位置切换到插入模式
- `a` append 在当前位置后一位切换到插入模式
- `esc` 编辑模式下退出编辑模式
- `x` 删除光标所在位置的字符
- `dd` 删除光标所在行
- `u` undo
- `ctrl+r` redo
- `/` 搜索模式
- `n` 搜索模式下下一个匹配项
- `v` 选择
- `y`  复制
- `yy`  复制当行
- `p` 粘贴
- *以上某些操作加上数字n* 执行n次操作，如： `dd100` 删除100行。

### 冒号操作

- `:w` write
- `:q` quit
- `:wq` write and quit
- `:q!` quit without save

### Tips & Tricks for vim

忘了 `sudo vim <file>` ，但想直接保存： `:w !sudo tee %` 。(From Haruue)    
解释一下： `:w` 在后面跟了参数的情况下，会将文件内容写入 `stdout` ，通过管道传输给后面的命令。原型是这样的 `:w !<command>`

批量替换： `:%s,<source>,<destination>,g` 其中，分隔符逗号可以换成其他的某些符号，我习惯逗号。 `g` 是替换所有。

~~啥，你问我emacs?我是vim党。~~

## 一些符号和小技巧

### `>`

`echo 123 > test` ，将 `echo` 输出到 `stdout` 的 `"123"` 重定向到文件 `test` 。

### `>>`

`>` 为覆盖文件内容， `>>` 为追加文件内容。

注意：0 是标准输入（STDIN），1 是标准输出（STDOUT），2 是标准错误输出（STDERR）。

所以有这种写法： `<command> 1 > stdout.log 2 > stderr.log` 

### `<`

将输入重定向到文件。

### `|`

管道符，将符号左边的 `stdout` 作为右边的 `stdin` 。比如：   
`ps -aux | grep docker` 

### `&`

此行命令后台运行。例： `ping www.baidu.com &` 。    
这样的话后台就会一直 `ping` 。

### `&&`

相当于两个命令一起执行。例： `apt-get update && apt-get upgrade`

### `~`

用户目录。

比如root就是 `/root` ,一般用户(username)就是 `/home/username` 。

### `!!`

上一条命令。

### `!<command>`

带上一次的参数再次执行那个命令执行。

`pacman -Syu` ，忘了 `sudo` ，就可以 `sudo !!` 。

### `$<num>`

`<num>` 为数字，代表第num个命令行参数，在bash脚本中经常使用。

### `$?`

上一条命令的返回值。

## 扩展

### 换个shell玩儿

haruue推荐了zsh，快捷配置可以用 [oh-my-zsh](https://github.com/robbyrussell/oh-my-zsh/) ，具体github上有安装过程。

### lxc

去 [这儿](http://blog.pinkd.online/sre/2016/11/27/ABC-of-lxc) 看看吧，之前写的，可能还有错的地方。

### dcoker

过几天再写。。。
