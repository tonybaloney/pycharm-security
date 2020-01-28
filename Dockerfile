FROM ubuntu:18.04

WORKDIR /sources
RUN apt-get -y update && apt-get -y install wget
RUN wget https://download.jetbrains.com/python/pycharm-community-2019.3.2.tar.gz && tar xzf pycharm-community-2019.3.2.tar.gz -C /opt/
RUN apt-get -y install unzip
RUN wget https://github.com/tonybaloney/pycharm-security/releases/download/1.10.0/pycharm-security-1.1.0.zip && unzip pycharm-security-1.1.0.zip -d /opt/pycharm-community-2019.3.2/plugins
ENTRYPOINT /opt/pycharm-community-2019.3.2/bin/inspect.sh
