
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
outputfile = open(addr+'leukocyteSummary.csv','w')
outputfile.write(",ExecutionTime(S)\n") 

for i in range(sampleSize):
	inputfilename=addr+"leukocyte"+str(i)+".log"
	print inputfilename
	outputStr="leukocyte"+str(i)+","
	with open(inputfilename) as f:
		for line in f:
			#words=re.findall(r"[\w']+", line)
			words=line.split()
			#print words[0]
			if len(words) > 5 :
				if (words[5]=='seconds'):
					outputStr=outputStr+words[4]+","

	outputfile.write(outputStr+"\n") 


outputfile.close() 

