build:
	javac -d out src/*.java

run:
	javac -d out src/*.java && java -cp out src/Main.java