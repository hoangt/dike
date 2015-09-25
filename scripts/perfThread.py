
#Perfer.py

import subprocess
import sys
import time
import re

def findPID(benchmark):
	bashCommand = "pgrep "+benchmark

	process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE)
	output = process.communicate()[0]

	if output=="":
		output=-1
		
	return int(output)


def perf(pid, index):
	freq = 500
	#bashCommand = "taskset -c 39 perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p "+ str(pid) + " sleep "+str( float(freq) / 1000) +" 2>&1 "
	#bashCommand = "taskset -c 39 perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -p "+ str(pid) + " sleep "+str( float(freq) / 1000)
	bashCommand = "ls /proc/"+str(pid)+"/task/"

	process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
	out, err = process.communicate()

	tids=findTIDs(out)

	for i in range(0, len(tids)):
		bashCommand = bashCommand = "taskset -c 39 perf stat -e cycles -e LLC-load-misses -e LLC-store-misses -t "+ str(tids[i]) + " sleep "+str( float(freq) / 1000)+ " & "
		process = subprocess.Popen(bashCommand.split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
		out, err = process.communicate()
		printStats(err, tids[i] ,index)		

	print "--------------------------------------------------------------"
	time.sleep(0.48)
	#printStats(err, pid ,index)
	#print parts

def findTIDs(out):
	tids=[]
	parts=out.split()
	for i in range(0, len(parts)):
		tids.append(int(parts[i]))

	#print tids
	return tids

def printStats(txt, pid , index):
	parts=txt.split()

	cycles=0
	LLCloadmisses=0
	LLCstoremisses=0

	for i in range(0, len(parts)):
		if parts[i]=="cycles":
			tmp=parts[i-1].replace(",","")
			if tmp.isdigit():
				cycles=int(tmp)
			else:
				cycles=1
		if parts[i]=="LLC-load-misses":
			tmp=parts[i-1].replace(",","")
			if tmp.isdigit():
				LLCloadmisses=int(tmp)
			else:
				LLCloadmisses=0
		if parts[i]=="LLC-store-misses":
			tmp=parts[i-1].replace(",","")
			if tmp.isdigit():
				LLCstoremisses=int(tmp)
			else:
				LLCstoremisses=0

	BW= ((LLCloadmisses+LLCstoremisses) / float(cycles) )*1000
	print str(index)+" : " +str(pid)+ " : "+ str(BW) + " : "+str(parts)

	#return cycles, LLC-load-misses, LLC-store-misses

def main():
	benchmark=sys.argv[1]
	pid=0
	index=0
	while (pid!=-1):
		pid=findPID(benchmark)
		#print "pid is "+ str(pid)
		if pid!=-1:
			perf(pid, index)

		
		index+=1




main()