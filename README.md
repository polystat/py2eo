
<img src="https://www.yegor256.com/images/books/elegant-objects/cactus.svg" height="100px" />

[![Java CI](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml/badge.svg)](https://github.com/polystat/py2eo/actions/workflows/github-ci.yml)

This is a translator of Python to [EOLANG](https://www.eolang.org).

User Manual
----
Required software:
* [Java 11](https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_windows-x64_bin.zip) - add path to /bin folder to system variables

Run info:
* download [archive](https://s01.oss.sonatype.org/service/local/repositories/releases/content/org/polystat/py2eo/0.0.2/py2eo-0.0.2.jar) with executable file / or open [link](https://s01.oss.sonatype.org/#nexus-search;quick~py2eo) and download `py2eo-0.0.2.jar`
* open Powershell and move to folder with local git repo
* run command `java -jar path_to_jar path_to_py_code`
* check output .eo file 

Sample .py code:
```
def conditionalCheck2():
    a = 4
    b = 2

    return a > b
```

Sample .eo output from the code scope above:
