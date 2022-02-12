gerrit-support REST API
==============================

NAME
----
collect/1 - Prepare a support .zip file

SYNOPSIS
--------
```
POST /a/plugins/gerrit-support/collect
```

DESCRIPTION
-----------
Prepares a zip file collecting information requested in the JSON request.

The JSON payload contains a series of flags with the list of information to be
collected and archived in the .zip file.

- gerritVersion - JSON String with the version of running Gerrit server
- cpuInfo - JSON Object with all the CPU information collected by [jHardware](https://github.com/profesorfalken/jHardware)
- memInfo - JSON Object with all the Memory information collected by [jHardware](https://github.com/profesorfalken/jHardware)
- diskInfo - JSON Object describing the disk information. Here a possible output:

```
{
    "diskFree": 106969321472,
    "diskTotal": 235089907712,
    "diskUsable": 95003811840,
    "path": "/home/pakkio/g2.14-stable/data"
}
```

- configInfo - This will add in the zip all the *.config files in the $GERRIT/etc folder
- pluginsInfo - This will add 3 elements in the zip containing the plugins_dir,
  lib_dir and the versions for each plugin. Here a possible output:

lib_dir:

```
[
  {
    "name": "github-oauth.jar",
    "perms": "rw-rw-r--",
    "owner": "gerrit",
    "group": "gerrit",
    "date": "2017-06-04T08:35:02Z",
    "size": 128567
  }
]
```

plugins_dir:

```
[
  {
    "name": "gerrit-support.jar",
    "perms": "rw-rw-r--",
    "owner": "gerrit",
    "group": "gerrit",
    "date": "2017-06-04T08:35:02Z",
    "size": 9639813
  }
]
```

plugins_versions:

```
{
     "gerrit-support": {
        "id"       : "gerrit-support",
        "version"  : "1.0",
        "indexUrl" : "/plugins/myplugin",
        "disabled" : false }
}
```

NOTE: API must be authenticated with the credentials of a user with the
'Collect Server Data' capability.

EXAMPLES
--------

Ask the server to prepare zip file for version, cpuinfo and meminfo

```
curl -v -H "Content-Type: application/json" \
   -d '{"gerritVersion": true,"cpuInfo": true, "memInfo": true }' \
    http://host:port/a/plugins/gerrit-support/collect

< HTTP/1.1 201 Created
< Date: Tue, 04 Apr 2017 22:53:33 GMT
< Content-Type: text/plain; charset=UTF-8
< Location: /plugins/gerrit-support/collect/20170405-005334-collect-b6d2bc6a-7f01-4b93-9f74-ad28b4a68e67.zip
< Content-Length: 2

```

NOTE: Location header gives the name of the prepared file created on Gerrit
server. API must be authenticated with the credentials of a user with the
'Collect Server Data' capability.


NAME
----
collect/2 - Download a support file

SYNOPSIS
--------
```
GET /a/plugins/gerrit-support/collect/<zip file name>
```

DESCRIPTION
-----------
Download a Gerrit support .zip file previously prepared.

EXAMPLES
--------

Download the .zip support file

```
     curl http://host:port/a/plugins/gerrit-support/collect/20170405-005334-collect-b6d2bc6a-7f01-4b93-9f74-ad28b4a68e67.zip \
          -o received.zip
```

