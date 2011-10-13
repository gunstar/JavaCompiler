#####################################################################
# Targets
#####################################################################

JAVA_FILES := $(wildcard */*.java)

clean:
	@find . -name "*.class" -delete

build:
	@javac */*.java

run: build
	@java -cp . boot.Boot ../JavaCompilerOut $(JAVA_FILES)

#####################################################################
# Qt Creator Files
#####################################################################

files:
	find . -name "*.java" >find_java
	find . -name "Makefile" >find_makefile
	find . -name "README" >find_readme
	cat find_* >find_x
	sort find_x >JavaCompiler.files
	rm -f find_*
