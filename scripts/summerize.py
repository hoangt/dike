#
# python summerize.py 51 8 4 jacobi leukocyte lavaMD srad
# 51 -> runIndex in results folder, 8: copies of each benchmark, 4:total number of benchmarks

import sys
import re
import numpy as np
import csv

if len(sys.argv) == 1:
	print "how to use: python summerize.py 51 8 4 jacobi leukocyte lavaMD srad"
else:
	runIndex = int(sys.argv[1])
	copies = int(sys.argv[2])
	bmsize = int(sys.argv[3])
	benchmark=[]
	for i in range(bmsize):
		benchmark.append(sys.argv[i+4])
		print(sys.argv[i+4])
	
addr="../Results/run"+str(runIndex)+"/"
outputfile = open(addr+'runSummary.csv','w')

my_dict = {}
for index in range(bmsize):
	bm=benchmark[index]
	addr="../Results/run"+str(runIndex)+"/"
	inputfilename=addr+bm+"Summary.csv"
	with open(inputfilename, 'rb') as f:
		reader = csv.reader(f)
		your_list = list(reader)

	#print your_list
	your_list.pop(0)
	for x, y, z in your_list:
		my_dict[x.lower()] = y

#print my_dict
	
outputStr=","
for index in range(bmsize):
	outputStr=outputStr+benchmark[index]+","
outputStr = outputStr[:-1]+"\n"
outputfile.write(outputStr) 
#print outputStr	

outputStr=""
for row in range(copies):
	outputStr="app"+str(row)+","
	for index in range(bmsize):
		app=benchmark[index]+str(row)
		outputStr=outputStr+my_dict[app.lower()]+","
	outputStr = outputStr[:-1]+"\n"
	outputfile.write(outputStr) 
	print outputStr	
	#outputStr=benchmark+str(row)+","
#	for key in sorted(my_dict):
#		outputStr+=my_dict[key][row]+","

#	print outputStr
#	outputfile.write(outputStr+"\n") 

#print my_dict
outputfile.close() 




