JFLAGS = -g
JC = javac
JVM= java
#FILE=

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

SRCDIR = ./src

CLASSES = \
$(SRCDIR)/inspector.java \
$(SRCDIR)/perf.java \
$(SRCDIR)/supervisor.java \
$(SRCDIR)/Program.java \
$(SRCDIR)/Core.java 

MAIN = inspector

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN)

clean:
	$(RM) *.class
