# Spring GeekApk Server [![checks](https://img.shields.io/travis/duangsuse/GeekApk.svg?label=tests&logo=Travis%20integration&style=popout-square)](https://travis-ci.org/duangsuse/GeekApk/) [![code size](https://img.shields.io/github/languages/code-size/duangsuse/GeekApk.svg?style=flat-square)](https://github.com/duangsuse/GeekApk/pulse) [![agpl-3.0](https://img.shields.io/github/license/duangsuse/GeekApk.svg?style=flat-square)](https://www.gnu.org/licenses/agpl-3.0.html)

<p align="center">
    <img width="40%" src="https://user-images.githubusercontent.com/10570123/52161551-8321ed00-2701-11e9-963b-18e5791d553c.png" alt="Cup - The Mascot for GeekApk" /><br>
    <i><sub>(illustration by duangsuse)</sub></i>
</p>

> The way to get started is to quit talking and begin doing.
_— Walt Disney_

## Introduction

GeekApk Spring server built with (\*v\*) and Kotlin/SpringBoot

## Features in version 1.0♭

- [ ] Migrations (Export data tables to binary file)
- [x] RPC interfaces (server, admin, category, user, app/collab, reversion, comment, star, follow, timeline, notification)
- [x] User last online, App pinned comment
- [x] GeekApk bot i18n
- [x] Minimal counter check before Data pull 
- [x] MetaApp for users
- [x] AppType, GeekApk bot
- [ ] Query Combating Language support (bulk query)
- [ ] Lexical scoping (lambda calculus) for QCL Interpreter

## Deploying

### Java Runtime

> GeekApk Spring is a simple application, or microservice (and transitively, [JavaEE](https://javaee.github.io/) *Platform* Application) built against [SpringBoot](https://start.spring.io/)

A Java Runtime with sufficient dynamic linking requirements (like spring boot runtime dependencies) is required to run GeekApk

This project's Kotlin compiler is configured to generate code for JVM 1.8, which is popular in nowadays Java server-side development 

Requirements: JRE 1.8 or above

Recommended: [Oracle JDK/JRE](https://java.net) or [OpenJDK 8-11](https://adoptopenjdk.net/) 

[GraalVM](https://www.graalvm.org/) with Ahead-Of-Time optimizations is also a good option for the server program runtime (since SpringBoot has been [migrated to SubstrateVM](https://github.com/spring-projects/spring-framework/issues/21529))

### Required configuration properties

Since GeekApk is a RDBMS-based web backend application, a working [PostgreSQL](https://www.postgresql.org) instance is required to run GeekApk

Set up the GeekApk database with following commands

```bash
psql -c 'CREATE DATABASE geekapk_db;' -U postgres # Create database in pg clusters for geekapk with user postgres
sudo -u postgres psql
```

```sql
CREATE USER geekapk WITH PASSWORD 'password'; -- Create geekapk application database user

GRANT ALL PRIVILEGES ON DATABASE geekapk_db TO geekapk; -- Make this database geekapk owned
```

To make a usable database for GeekApk

For database connection and authentication, you may need to change cluster's `pg_hba.conf`, please make sure that PostgreSQL is only accepting
connections from local loopback for server security.

All microservice properties is in `application.properties` from application classpath 

### Make GeekApk a service (continuous running)

[Systemd](https://en.wikipedia.org/wiki/Systemd) is a popular daemon service manager in modern GNU/Linux distributions, and it's recommended to use when making microservices continuous running

create a file with these content in `/lib/systemd/system/geekapk_spring.service`:

```ini
[Unit]
Description=GeekApk Spring server
After=network.target postgresql.service

[Service]
ExecStart=#Your start command

Restart=always
RestartSec=1

Environment=K=V
Environment=K=V
```

Then you can use `sudo systemctl start geekapk_spring` to start the server

For server status checking, execute shell command `sudo systemctl status geekapk_spring.service`

Process control daemon [Supervisor](http://supervisord.org/) is also a good option for this case

[man page for systemd](https://www.freedesktop.org/software/systemd/man/systemd.service.html) | [related practice](https://gitlab.com/duangsuse/naive_fortunes/blob/master/bootstrap.sh)

### (Optional) Make a reverse proxy

[CloudFlare](https://www.cloudflare.com/) is also a usable option for reverse proxying the service

#### Nginx configuration

```
user www-data;
worker_processes 4;
pid /run/nginx.pid;

events {
    worker_connections 65535;
    multi_accept on;
}

http {
    include mime.types;
    upstream geeksvc {
        server localhost:233;
    }

    server {
        listen 80;
        server_name api.geekapk.org;

        location / {
            proxy_pass http://geeksvc;
            proxy_redirect off;
            proxy_set_header Host $host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
```

#### Apache configuration

```
LoadModule proxy_module modules/mod_proxy.so
LoadModule proxy_http_module modules/mod_proxy_http.so

<VirtualHost *:443>
    ServerName api.geekapk.org
    ProxyPass / http://localhost:233/
    ProxyPassReverse / http://localhost:233/
</VirtualHost>
```

### Server stress testing

You can use wrk / siege / ab for server stress testing

You SHOULD NOT stress testing on the production environment GeekApk API service,
run stress tests on private machines.

```bash
# Using Apache HTTP server benchmarking tool
ab -n request_num -t timeout -c concurrency -m http_method -C cookie http://127.0.0.1:8080/

# Using Siege, a HTTP/FTP load tester and benchmarking utility
# -b means "no delays between requests"
siege -b -n request_num -t timeout -c concurrency -H "Cookie: ${cookies}" http://127.0.0.1:8080/

# use man ab / siege for documents

# Wrk is not packaged for most distributions, through.
# You can install it from scratch with the documentation from https://github.com/wg/wrk/wiki/Installing-Wrk-on-Linux
```


## Configuration

All GeekApk application related properties (e.g. server access password) is in `info.ini` (default settings)
and can be overridden in `geekapk.ini`, message translations (string templates) can be found in `translations.ini`

Add your configuration override file paths separated by column `:` in environment variable `GEEKAPK_INI`

Those configuration files are self-documented, just read inline comments and set the values on your purpose

## API Mapping

### Main

+ [/](http://api.geekapk.moe/) API Index, help message
+ [/serverVersion](http://api.geekapk.moe/serverVersion) Server version string
+ [/serverDescription](http://api.geekapk.moe/serverDescription) description string
+ [/serverBoot](http://api.geekapk.moe/serverBoot) bootUp datetime
+ [/detail](http://api.geekapk.moe/serverDetail) server detail map

### Others

The GeekApk API Server has built-in API documents, see [API Index](http://api.geekapk.moe/) 

## References

+ [GeekApkR](https://github.com/geekapk-r/) GitHub Organization
+ [GeekApk](https://github.com/geekapk/) GitHub Organization

+ [GeekApkSpecShort](https://gist.github.com/duangsuse/335d87276bfb8ca3a4d00c0d0eb71f3f#file-geekapkspecshort-pdf) GitHub Gist

### Future reading

+ [GeekApk 参与者公约](CODE_OF_CONDUCT.md)
+ [Pull Request 需知](PULL_REQUEST_TEMPLATE.md)
+ [贡献指南 Contributing guides](CONTRIBUTING.md)
+ [APIs for GeekApk v1.0b](geekapk_v1b_api.geekspec)

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
