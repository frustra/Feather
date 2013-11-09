feather: build-deps make-jar

EMPTY :=
FEATHER_LIBS = lib/asm-4.2.jar lib/asm-tree-4.2.jar lib/sqljet-1.1.8.jar lib/antlr-runtime-3.4.jar

compile-bin:
	find src -name \*.java -print > sourcefiles.list.tmp
	rm -rf bin
	mkdir -p bin
	javac -classpath "$(subst $(EMPTY) $(EMPTY),:,$(FEATHER_LIBS)):deps/filament/bin" -d bin/ -sourcepath src/ @sourcefiles.list.tmp
	rm sourcefiles.list.tmp

make-jar: compile-bin
	cp -r deps/filament/bin/* bin/
	cd bin; \
		$(foreach file, $(FEATHER_LIBS), unzip -o ../$(file);) \
		find . -name \*.class -print > classfiles.list.tmp; \
		echo "Main-Class: org.frustra.feather.Feather" > MANIFEST.txt; \
		jar cfm ../Feather.jar MANIFEST.txt @classfiles.list.tmp; \
		rm classfiles.list.tmp MANIFEST.txt

fetch-deps:
	-git clone git://github.com/xthexder/filament.git deps/filament
	cd deps/filament; git fetch --all; git reset --hard origin/master

build-deps: fetch-deps
	cd deps/filament; \
		find src -name \*.java -print > sourcefiles.list.tmp; \
		rm -rf bin; \
		mkdir -p bin; \
		javac -classpath "lib/asm-4.2.jar:lib/asm-tree-4.2.jar" -d bin/ -sourcepath src/ @sourcefiles.list.tmp; \
		rm sourcefiles.list.tmp

clean:
	rm -rf bin deps Feather.jar

