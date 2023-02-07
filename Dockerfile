FROM selenium/standalone-chrome

USER root
RUN apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update && apt-get install -y openjdk-16-jdk libxkbcommon-x11-0

ADD testar/target/distributions/testar.tar .

ENV JAVA_HOME "/usr/lib/jvm/java-16-openjdk-amd64"
ENV DISPLAY=":99.0"

COPY runImage /runImage
COPY README.Docker /README.Docker
RUN chmod 777 /runImage

CMD [ "sh", "/runImage"]

