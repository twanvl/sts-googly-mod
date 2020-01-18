.PHONY: all clean
all:
	make -C src/main/resources/googlymod/images
	mvn package

clean:
	mvn clean
	make clean -C src/main/resources/googlymod/images

steam_upload: all
	make -C steam_resources upload
