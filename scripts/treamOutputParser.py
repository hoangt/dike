
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
outputfile = open(addr+'treamSummary.csv','w')
outputfile.write(",ExecutionTime(S)\n") 

for i in range(sampleSize):
	inputfilename=addr+"tream"+str(i)+".log"
	print inputfilename
	outputStr="tream"+str(i)+","
	with open(inputfilename) as f:
		for line in f:
			#words=re.findall(r"[\w']+", line)
			words=line.split()
			#print words[0]
			if len(words) > 1 :
				if (words[0]=='time') and (words[1]=='='):
					outputStr=outputStr+words[2]+","

	outputfile.write(outputStr+"\n") 


outputfile.close() 

