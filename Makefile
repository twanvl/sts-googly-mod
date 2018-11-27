
all:
	make -C src/main/resources/googlymod/images
	mvn package

clean:
	mvn clean
	make clean -C src/main/resources/googlymod/images
