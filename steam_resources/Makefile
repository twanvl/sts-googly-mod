.PHONY: upload

content/GooglyMod.jar: ../target/GooglyMod.jar
	cp $^ $@

image.jpg: image.png
	convert $^ -resize 467x467 $@

upload: content/GooglyMod.jar image.jpg
	java -jar ../../_ModTheSpire/mod-uploader.jar upload -w .
