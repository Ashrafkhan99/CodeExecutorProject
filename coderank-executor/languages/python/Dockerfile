FROM python:3.11-slim

# Create non-root user and sandbox dir
RUN groupadd -g 10001 sandbox \
 && useradd -u 10001 -g 10001 -s /usr/sbin/nologin -m sandbox \
 && mkdir -p /sandbox \
 && chown -R sandbox:sandbox /sandbox

USER sandbox
WORKDIR /sandbox

# Default command (overridden at run)
CMD ["python3", "--version"]
