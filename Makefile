manageDB:
	javac -d code/bin -classpath code/bin/ojdbc6.jar:code/bin/json-simple-1.1.jar -sourcepath code/src code/src/DatabaseManagement.java; java -classpath code/bin:code/bin/ojdbc6.jar:code/bin/json-simple-1.1.jar DatabaseManagement
interface:
	javac -d code/bin -classpath code/bin/ojdbc6.jar:code/bin/json-simple-1.1.jar -sourcepath code/src code/src/Interface.java; java -classpath code/bin:code/bin/ojdbc6.jar:code/bin/json-simple-1.1.jar Interface
clean:
	rm -rf code/bin/*.class
