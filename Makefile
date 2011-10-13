#####################################################################
# Targets
#####################################################################

JAVA_FILES := $(wildcard */*.java) $(wildcard */*/*.java) $(wildcard */*/*/*.java)

clean:
	@find . -name "*.class" -delete

build:
	@javac */*.java */*/*.java */*/*/*.java

run: build
	@java -cp . boot.Boot ../JavaCompilerOut $(JAVA_FILES)

#####################################################################
# Qt Creator Files
#####################################################################

files:
	git add -f */*.java */*/*.java
	find . -name "*.bnf" > find_bnf
	find . -name "*.java" >find_java
	find . -name "*.txt" > find_bnf
	find . -name "Makefile" >find_makefile
	find . -name "README" >find_readme
	find . -name "TODO" >find_todo
	cat find_* >find_x
	sort find_x >JavaCompiler.files
	rm -f find_*
