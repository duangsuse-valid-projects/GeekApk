# Spring GeekApk Server [![code size](https://img.shields.io/github/languages/code-size/duangsuse/GeekApk.svg?style=flat-square)](https://github.com/duangsuse/GeekApk/pulse) [![agpl-3.0](https://img.shields.io/github/license/duangsuse/GeekApk.svg?style=flat-square)](https://www.gnu.org/licenses/agpl-3.0.html)

<p align="center"><img width="40%" src="https://user-images.githubusercontent.com/10570123/52161551-8321ed00-2701-11e9-963b-18e5791d553c.png" alt="Cup - The Mascot for GeekApk" /></p>

> The way to get started is to quit talking and begin doing.
_— Walt Disney_

## Introduction

GeekApk Spring server built with (\*v\*) and Kotlin and SpringBoot

## Features in version 1.0♭

- [ ] Migrations
- [x] RPC interfaces (server, admin, category, user, app/collab, reversion, comment, star, follow, timeline, notification)
- [x] User last online, App pinned comment
- [x] GeekApk bot i18n
- [x] Minial counter check before Data pull 
- [x] MetaApp for users
- [x] AppType, GeekApk bot
- [ ] Query Combinating Language support (bulkquery)
- [ ] Lexical scoping (lambda calculus) for QCL Interpreter

## Deploying

## Configuration

## API Mapping

### Main

+ [/](http://geekapk.h2o2.moe/) API Index, help message
+ [/serverVersion](http://geekapk.h2o2.moe/serverVersion) Server version string
+ [/serverDescription](http://geekapk.h2o2.moe/serverDescription) description string
+ [/serverBoot](http://geekapk.h2o2.moe/serverBoot) bootUp datetime
+ [/detail](http://geekapk.h2o2.moe/serverDetail) server detail map

### Others

The GeekApk API Server has built-in API documents, see [API Index](http://geekapk.h2o2.moe/) 

## References

+ [GeekApkR](https://github.com/geekapk-r/) GitHub Organization
+ [GeekApk](https://github.com/geekapk/) GitHub Organization

+ [GeekApkSpecShort](https://gist.github.com/duangsuse/335d87276bfb8ca3a4d00c0d0eb71f3f#file-geekapkspecshort-pdf) GitHub Gist

## License

    Copyright (C) 2019  duangsuse

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
