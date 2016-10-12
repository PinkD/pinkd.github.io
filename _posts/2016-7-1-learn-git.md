---
layout: post
title: Git笔记
description: 从我的简书上面搬运过来的
tags:
- Coding
categories: Other
---


**tips:<\*> means required,[\*] means optional**

## 基本操作
创建仓库

```bash
git init
```

添加文件

```bash
git add <filename>
```

提交更改

```bash
git commit -m '<message>'
```

查看状态

```bash
git status
```

查看修改内容

```bash
git diff
```

查看日志

```bash
git log
```

回退到目标版本

**注：**

- HEAD^为上一个版本，HEAD^^为上上个版本，依此类推
- commit id类似3628164

```bash
git reset --hard <version>
```

撤销更改

- 缓存区

```bash
git reset HEAD <filename>
```

- 文件

```bash
git checkout -- <filename>
```

删除文件

```bash
git rm <filename>
```

## 远程仓库
添加远程仓库

```bash
git remote add origin <*.git>
```

推送到远程仓库

```bash
git push [-u] origin <repository-name>
```

克隆仓库

```bash
git clone <*.git>
```

## 分支

创建分支

```bash
git branch <branch-name>
```

切换分支

```bash
git checkout <branch-name>
```

合并某分支到当前分支

```bash
git checkout -b <branch-name>
```

创建+切换分支

```bash
git checkout -b <branch-name>
```

查看分支

```bash
git branch -a
```

删除分支

```bash
git branch -d <branch-name>
```

强行删除未合并分支

```bash
git branch -D <branch-name>
```
创建远程分支到本地

```bash
git checkout -b <branch-name> origin/<branch-name>
```

删除远程分支

```bash
git branch -r -d origin/<branch-name>
git push origin :<branch-name>
```

建立本地分支和远程分支的关联

```bash
git branch --set-upstream <branch-name> origin/<branch-name>
```

## 临时储存工作进度
储存

```bash
git stash
```
查看

```bash
git stash list
```

恢复

```bash
git stash apply [stash@{*}]
```

删除

```bash
git stash drop [stash@{*}]
```

恢复并删除

```bash
git stash pop [stash@{*}]
```

## 标签

创建标签

```bash
git tab <tag-name> [-m 'comment'] [commit-id]
 #eg.
git tag v0.9 6224937
```

删除标签

```bash
git tag -d <tag-name>
```

推送标签

```bash
git push origin <tag-name>
```

删除远程标签

先删除本地，然后再

```bash
git push origin :refs/tags/<tag-name>
```

## 忽略文件
创建：<code>.gitignore</code>，在里面写入过滤规则

```
*.class
test/
```

强行添加忽略的文件

```bash
git add -f <filename>
```

被忽略的文件的忽略规则

```bash
git check-ignore -v <filename>
```

## 自定义命令
(个人认为可以理解为C语言中的宏定义)

```bash
git config --global alias.<abbr> 'command'
 #e.g.
git config --global alias.fuck 'push origin'
git config --global alias.logd "log --color --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit"
```

## 不小心push了密码到github上
没错，简直蠢哭了

[BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)
用法：

```bash
java -jar bfg.jar --replace-text replacements.txt my-repo.git
```

replacements.txt 这样写：

```bash
test@qq.com==>xxxx@qq.com
```

------
[原文看这里](http://www.liaoxuefeng.com/wiki/0013739516305929606dd18361248578c67b8067c8c017b000 )
