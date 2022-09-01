CC=gcc
CFLAGS=-Wall
BIN=bin
SRC=src
C_SRC=$(SRC)/main/c
JAVA_SRC=$(SRC)/main/java

build: makebin header libnanotunes.so libnativeaudioutil.so

run: build
	LD_LIBRARY_PATH=$(BIN) mvn compile exec:java -Dexec.mainClass=io.github.rowak.nanotunes.Main

run-beats: build
	LD_LIBRARY_PATH=$(BIN) mvn compile exec:java -Dexec.mainClass=io.github.rowak.nanotunes.Main_Beats

makebin:
	mkdir -p $(BIN)

header:
	javac -h . $(JAVA_SRC)/io/github/rowak/nanotunes/NativeAudioUtil.java

clean:
	rm -rd $(BIN)

libnativeaudioutil.so: NativeAudioUtil.o
	$(CC) $(CFLAGS) -shared -fPIC -o $(BIN)/libnativeaudioutil.so $(BIN)/io_github_rowak_nanotunes_NativeAudioUtil.o -L$(BIN) -lc -lnanotunes -lpulse-simple -lkissfft -lm

NativeAudioUtil.o:
	$(CC) $(CFLAGS) -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -o $(BIN)/io_github_rowak_nanotunes_NativeAudioUtil.o $(C_SRC)/io_github_rowak_nanotunes_NativeAudioUtil.c -h $(C_SRC)/nanotunes_pulse.h

libnanotunes.so: nanotunes_pulse.o
	$(CC) $(CFLAGS) -shared -o $(BIN)/libnanotunes.so $(BIN)/nanotunes_pulse.o

nanotunes_pulse.o:
	$(CC) $(CFLAGS) -c -fpic -o $(BIN)/nanotunes_pulse.o $(C_SRC)/nanotunes_pulse.c -lpulse-simple -lkissfft -lm