ARG PYCHARM_VERSION=2021.2
FROM ubuntu:18.04
ARG PYCHARM_VERSION
RUN echo "Building PyCharm $PYCHARM_VERSION with python-security"

COPY . /sources/plugin
RUN rm -rf /sources/plugin/build

WORKDIR /sources

# Install dependencies
RUN apt-get -y update && apt-get -y install wget unzip openjdk-11-jre-headless --no-install-recommends \
    && rm -rf /var/lib/apt/lists/* \

# Install PyCharm
    && wget https://download.jetbrains.com/python/pycharm-community-${PYCHARM_VERSION}.tar.gz \
    && tar xzf pycharm-community-${PYCHARM_VERSION}.tar.gz -C /opt/ \
    && mv /opt/pycharm-community-${PYCHARM_VERSION} /opt/pycharm-community \
    && rm -f /sources/pycharm-community-${PYCHARM_VERSION}.tar.gz \

# Test and compile plugin
    && cd plugin/ && ./gradlew test --no-daemon -PintellijPublishToken=FAKE_TOKEN \
    && ./gradlew buildPlugin --no-daemon -PintellijPublishToken=FAKE_TOKEN \
    && unzip build/distributions/pycharm-security-*.zip -d /opt/pycharm-community/plugins \
    && cd .. && rm -rf plugin/ && rm -rf ~/.gradle \

# Install default inspection profile
    && wget https://github.com/tonybaloney/pycharm-security/raw/master/doc/_static/SecurityInspectionProfile.xml -O /sources/SecurityInspectionProfile.xml

# Configure entrypoint
ENTRYPOINT /opt/pycharm-community/bin/inspect.sh /code /sources/SecurityInspectionProfile.xml out.log -format plain -v0 2> /dev/null && cat out.log
