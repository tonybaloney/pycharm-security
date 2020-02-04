FROM anthonypjshaw/pycharm-security:latest
COPY action.sh /action.sh
COPY parse.py /code/parse.py
RUN apt-get -y install python3
RUN ["chmod", "+x", "/action.sh"]
ENTRYPOINT ["/action.sh"]
