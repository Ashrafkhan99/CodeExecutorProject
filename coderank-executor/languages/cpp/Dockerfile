FROM gcc:13-bookworm

RUN groupadd -g 10001 sandbox \
 && useradd -u 10001 -g 10001 -s /usr/sbin/nologin -m sandbox \
 && mkdir -p /sandbox \
 && chown -R sandbox:sandbox /sandbox

USER sandbox
WORKDIR /sandbox

CMD ["g++", "--version"]
