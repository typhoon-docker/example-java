#!/bin/bash

docker run --name loljava -p 8866:8066 -e VIRTUAL_HOST=java.local.me --rm example-java
