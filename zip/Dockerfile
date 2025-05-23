# alpine linux included openjdk 21
FROM openjdk:21-jdk

# project directory
ENV project="zip"

# set timezone
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo "Asia/Seoul" > /etc/timezone

# create project directory
RUN mkdir ${project}

#COPY build/libs/*.jar /${workdir}/spring-base.jar
COPY build/libs/*.jar /${project}/${project}.jar

# exec jar without env
ENTRYPOINT ["sh", "-c", "java -jar /${project}/${project}.jar"]