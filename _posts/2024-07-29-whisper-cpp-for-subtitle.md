---
layout: post
title: 使用 Whisper 快速制作字幕
description: 使用 Whisper 快速制作字幕
tags:
- Other
categories: Other
---

# 简介

制作字幕需要花费非常多的时间，大致流程如下：

- 打轴
- 听写
- 校对
- 后期

如果是外语字幕，还会多一道翻译的流程，更加复杂

其中，打轴大概 1x ，听写 2x ，翻译 2x ，校对 1x ，后期 1x ，一共至少需要源视频的 5-10 倍时间才能制作一个完整的字幕。(这里不考虑剪辑视频的时间，纯给视频加字幕)

现在有越来越多的大模型能够做到各种各样的事情，其中就包括将语音转换为文字的 [Whisper] 和能够翻译的各种大模型，例如 [ChatGPT] 和 [llama] 。因此，现在制作字幕的过程中一些步骤的时间都可以大大缩减

这里介绍使用 [Whisper] 进行快速打轴和语音转文字的流程

# 依赖

两个必选依赖，一个可选依赖：
- [Whisper] : 大模型，将语音转文字，并提供简单的时间轴
- [ffmpeg] : 非常强大的格式转换软件，绝大部分视频音频播放器都有它的影子。能从视频提取音频，对音频进行简单处理
- [aegisub] : 字幕软件，基本功能齐全，没有花里胡哨的功能，可选依赖

## 依赖安装

Whisper 是大模型，默认是用 [python] 下的 pytorch 运行，但是有大佬开发了 [whisper.cpp] 让我们可以原生运行该模型。只需要从 [release 页面][whisper.cpp-release] 下载可执行文件和大模型，即可使用
模型可以直接用 `download-ggml-model.sh` 脚本下载，参考 [whisper 模型下载指南][whisper-model-download]

ffmpeg 可以直接从 [官网][ffmpeg] 进行下载，解压即可使用。 ffmpeg 和 whisper.cpp 都是命令行工具，不能直接双击打开，下文会给出具体的使用方法

[aegisub] 同样是从官网下载，可以下载安装版，也可以下载解压运行的版本

# 流程

- 首先，我们需要选一个视频或者音频作为源，在线视频可以通过 [yt-dlp] 等软件，将视频下载到本地
- 然后，使用 ffmpeg 对视频进行转码
  - 这里需要注意， whisper 只支持固定采样率的音频，因此参数中需要指定音频采样率为 16kHz
- 最后，使用 whisper 对转码后的音频进行识别

上述两个软件都是命令行工具，需要在命令行中运行。在 windows 中，需要在对应目录中 shift+鼠标右键，然后选择「在此处打开命令行」或者 powershell ，然后输入对应的 exe 和参数运行

简单示例：

```bash
./ffmpeg -i /path/to/source.mp4 -vn -ar 16k /path/to/output.wav
# parameters:
#   -i infile
#   -vn                 disable video
#   -ar[:<stream_spec>] <rate>  set audio sampling rate (in Hz)

./main -m models/ggml-large-q5_1.bin -f /path/to/output.wav -t 56 -l zh -pc -pp -osrt
# parameters:
#   -m FNAME,  --model FNAME       [models/ggml-base.en.bin] model path
#   -f FNAME,  --file FNAME        [       ] input WAV file path
#   -t N,      --threads N         [4      ] number of threads to use during computation
#   -pc,       --print-colors      [false  ] print colors
#   -pp,       --print-progress    [false  ] print progress
#   -osrt,     --output-srt        [false  ] output result in a srt file
```

上述参数中，路径需要换成对应文件的，然后 `-t` 参数需要换成你 cpu 的核心数。我的 cpu 有 32c64t ，所以我填的 56 ，一般情况下这个数应该是 8 或者 16

以上命令行实例是 linux 格式，如果是在 windows 中，需要使用 `./ffmpeg.exe` 或者 `.\ffmpeg.exe` 来执行命令，且路径的格式也不同。通常是 `D:\path\to\test.wav` ，也可以将文件直接拖进命令行窗口，会自动生成文件路径
执行完以上命令后就会在 `/path/to/` 中生成字幕文件 `output.wav.srt`  ，播放视频时将该文件拖入播放器中即可加载字幕

# 后续处理

上面生成的字幕文件虽然能用，但是仍然存在一些问题。最常见的， whisper 的识别文字可能会有错，且 whisper 是一段一段处理音频的，因此可能会出现奇怪的断句，需要手动处理。因此，如果需要得到一个相对完美的字幕文件，需要将上述的结果导入 aegisub 中进行文字和轴的修正

# 总结

虽然这个方案制作的字幕会存在一些问题，但是在配置好了环境的情况下，能够快速生成一个基本可用的字幕，能大大减少工作量

# 参考链接
- [Whisper]
- [ChatGPT]
- [ffmpeg]
- [aegisub]

[Whisper]: https://github.com/openai/whisper
[ChatGPT]: https://openai.com/chatgpt/
[llama]: https://llama.meta.com
[whisper.cpp-release]: https://github.com/ggerganov/whisper.cpp/releases
[whisper-model-download]: https://github.com/ggerganov/whisper.cpp/blob/master/models/README.md
[ffmpeg]: https://ffmpeg.org
[aegisub]: https://aegisub.org
[whisper.cpp]: https://github.com/ggerganov/whisper.cpp
[yt-dlp]: https://github.com/yt-dlp/yt-dlp
[python]: https://www.python.org