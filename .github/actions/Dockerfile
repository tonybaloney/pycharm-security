FROM docker.pkg.github.com/tonybaloney/pycharm-security/pycharm-security:1.4.2
COPY action.sh /action.sh
RUN ["chmod", "+x", "/action.sh"]
ENTRYPOINT ["/action.sh"]
COPY . /code