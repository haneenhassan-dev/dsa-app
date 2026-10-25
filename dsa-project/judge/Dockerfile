FROM eclipse-temurin:21-jdk

# Bake the Jackson jars into the image at a fixed path
COPY libs/ /opt/judge/libs/

# Submissions get mounted here at run time
WORKDIR /work


