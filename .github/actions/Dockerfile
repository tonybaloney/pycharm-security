FROM anthonypjshaw/pycharm-security:release-1.4.4
COPY action.sh /action.sh
COPY parse.py /parse.py
RUN apt-get -y install python3
RUN ["chmod", "+x", "/action.sh"]
ENTRYPOINT ["/action.sh"]
