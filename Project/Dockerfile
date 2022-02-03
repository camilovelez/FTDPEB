FROM openjdk:11
COPY ./target/classes /FTDPEB
COPY ./ConfigurationFiles /FTDPEB/ConfigurationFiles
COPY ./jars /FTDPEB/jars
WORKDIR /FTDPEB

CMD ["java", "-cp", "jars/*:.", "Main", "SmallInstance.json"]