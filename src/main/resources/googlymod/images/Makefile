.PHONY: all clean
all: eye.png pupil.png

%.png: source/%.png
	convert $^ -background transparent -gravity Center -extent 110% -resize 256x256 $@

clean: