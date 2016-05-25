JFLAGS = -g
JC = javac
JVM= java
JAVA_FILES := $(wildcard src/*.java)
#OBJ_FILES := $(addprefix classes/,$(notdir $(JAVA_FILES:.cpp=.class)))


.SUFFIXES: .java .class

SRCDIR = ./src
CLASSESDIR = ./classes


#all: $(JAVA_FILES)
#	$(JC) $(JFLAGS) -o $@ $^

#classes/%.class: src/%.java
#	$(JC) $(JFLAGS) -o $@ $<

all: $(FILES)
	$(JC) $(JFLAGS) $(JAVA_FILES) 

#@mkdir -p $(CLASSESDIR)
#@cp $(SRCDIR)/cores.layout $(CLASSESDIR)/cores.layout
#$(JC) $(JFLAGS) $(FILES) -d $(CLASSESDIR)
	

MAIN= inspector

run:
	cd $(SRCDIR) ; ./jacobi.sh 8 500 0 1 jacobi ; cd .. 	

clean:
	$(RM) -f $(SRCDIR)/*.class
