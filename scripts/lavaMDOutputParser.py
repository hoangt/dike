
import sys
import re
import numpy as np

if len(sys.argv) == 1:
	print "You can also give filename as a command line argument"
	sampleSize = int(raw_input("Enter Experiment Size: "))
	RunNumber = int(raw_input("Enter Run NUmber: "))
else:
	sampleSize = int(sys.argv[1])
	RunNumber = int(sys.argv[2])



#sampleSize=40
addr="../Results/run"+str(RunNumber)+"/"
outputfile = open(addr+'lavaMDSummary.csv','w')
outputfile.write(",ExecutionTime(S)\n") 

for i in range(sampleSize):
	inputfilename=addr+"lavaMD"+str(i)+".log"
	print inputfilename
	outputStr="lavaMD"+str(i)+","
	with open(inputfilename) as f:
		for line in f:
			#words=re.findall(r"[\w']+", line)
			words=line.split()
			#print words[0]
			if len(words) == 2 :
				if (words[1]=='s'):
					outputStr=outputStr+words[0]+","

	outputfile.write(outputStr+"\n") 


outputfile.close() 

