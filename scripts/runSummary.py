
import sys
import re
import numpy as np
import csv

if len(sys.argv) == 1:
	print "You can also give filename as a command line argument"
	benchmark = raw_input("Enter Experiment Name: ")
	mixNumber = int(raw_input("Enter Mix Number: "))
	runSize = int(raw_input("Enter Run Size: "))
else:
	benchmark = sys.argv[1]
	mixNumber = int(sys.argv[2])
	runSize = int(sys.argv[3])
	
addr="../Results/mix"+str(mixNumber)+"/"
outputfile = open(addr+'runSummary.csv','w')
for i in range(runSize):
	outputfile.write(",Run"+str(i)) 
outputfile.write("\n") 

my_dict = {}


apps=[]

for RunNumber in range(runSize):
	addr="../Results/mix"+str(mixNumber)+"/run"+str(RunNumber)+"/"
	inputfilename=addr+benchmark+"Summary.csv"
	col=[]
	with open(inputfilename) as csvfile:
		reader = csv.DictReader(csvfile)
		for row in reader:
			#print(row['ExecutionTime(S)'])
			col.append(row['ExecutionTime(S)'])
			
	my_dict[str(RunNumber)]=col
			

for row in range(0, len(my_dict["0"])):
	outputStr=benchmark+str(row)+","
	for key in sorted(my_dict):
		outputStr+=my_dict[key][row]+","

	print outputStr
	outputfile.write(outputStr+"\n") 

#print my_dict
outputfile.close() 




