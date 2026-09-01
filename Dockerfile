# SPDX-FileCopyrightText: Copyright (c) 2021-2022 py2eo team
# SPDX-License-Identifier: MIT

FROM yegor256/rultor-image
MAINTAINER Yegor Bugayenko <yegor256@gmail.com>
WORKDIR /eo

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get clean && \
  apt-get update -y --fix-missing && \
  apt-get install -y locales && \
  locale-gen en_US.UTF-8 && \
  dpkg-reconfigure locales && \
  echo "LC_ALL=en_US.UTF-8\nLANG=en_US.UTF-8\nLANGUAGE=en_US.UTF-8" > /etc/default/locale && \
  update-java-alternatives --set java-1.17.0-openjdk-amd64 && \
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

RUN apt-get install -y openjdk-17-jdk

COPY py2eo.jar /usr/local/py2eo.jar

ENTRYPOINT ["java", "-jar", "/usr/local/py2eo.jar"]
