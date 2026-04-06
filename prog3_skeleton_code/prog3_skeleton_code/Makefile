JFLAGS=-g
SHELL=/bin/bash
CP=lib/antlr-4.13.2-complete.jar:lib/prog2_withoutAbsyn.jar:lib/prog1.jar:.

Parse:
	javac -g -cp $(CP) **/*.java

clean:
	rm -rf ./bin;
	rm -rf Parse/antlr_build;
